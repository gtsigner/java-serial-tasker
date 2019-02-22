package com.oeynet.dev.mockserver.serial;

import com.oeynet.dev.mockserver.Global;
import com.oeynet.dev.mockserver.api.Config;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static com.oeynet.dev.mockserver.serial.SerialProtocolType.TYPE_SET_COMMAND;

public class PlcSerialPort {

    private SerialPort port = null;
    private String portName;
    private int baud;
    private InputStream inputStream;
    private OutputStream outputStream;


    //等待RS485串口服务器返回上一帧反馈信息（或失败延时）后，才能发送下一帧数据
    private Queue<PlcRequest> queue;
    private Queue<PlcRequest> setQueue;


    //-1就没占用>0就是被占用了
    private int lock = 0;
    private int timeout = 0;//超时计数器

    private PlcRequest request;

    private int errCount = 0;


    public boolean isConnected() {
        return isConnected;
    }

    private boolean isConnected = false;

    //plc 包
    private PlcPacket plcPacket = new PlcPacket();


    private Config config;

    /**
     * 收到数据的回调
     *
     * @param callbackFunc
     */
    public synchronized void setCallbackFunc(RecvCallbackInterface callbackFunc) {
        this.callbackFunc = callbackFunc;
    }

    private RecvCallbackInterface callbackFunc;


    /**
     * @param config
     * @param portName
     * @param baud
     */
    public PlcSerialPort(Config config, String portName, int baud) {
        this.config = config;
        this.portName = portName;
        this.baud = baud;
        //设置容量
        int capacity = config.getCurrent().getQueue().getRequest();
        //普通查询请求发送队列
        this.queue = new LinkedBlockingQueue<>(capacity);
        //设置请求发送队列
        this.setQueue = new LinkedBlockingQueue<>(capacity);
    }

    //链接
    public void connect() throws Exception {
        this.port = SerialUtil.connect(this.portName, baud);
        if (port == null) {
            this.config.pushMessage(0, "链接串口失败", "error");
            throw new Exception("链接串口失败");
        }
        this.inputStream = port.getInputStream();
        this.outputStream = port.getOutputStream();
        isConnected = true;
        this.port.addEventListener(serialPortEvent -> {
            int eventType = serialPortEvent.getEventType();
            switch (eventType) {
                case SerialPortEvent.DATA_AVAILABLE:
                    readComm();
                    errCount = 0;
                    break;
                case SerialPortEvent.OE:
                    break;
                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                    break;
                default:
                    break;
            }
        });
        port.notifyOnDataAvailable(true);
    }


    /**
     * 发送数据
     *
     * @param buff
     */
    public synchronized void sendBuffers(byte[] buff, int type) {
        //再发松之前先添加到队列里面
        PlcRequest request = new PlcRequest();
        request.setBuffer(buff);

        if (type == TYPE_SET_COMMAND) {
            if (config.getCurrent().getQueue().getRequest() <= setQueue.size()) {
                setQueue.poll();//废弃之前的请求
            }
            setQueue.add(request);
        } else {
            if (config.getCurrent().getQueue().getRequest() <= queue.size()) {
                queue.poll();//废弃之前的请求
            }
            //添加到请求
            queue.add(request);
        }
    }


    public synchronized int buildRequestNo() {
        Global.NO++;
        if (Global.NO > 255) {
            Global.NO = 0;
        }
        return Global.NO;
    }

    /**
     * 发送指令
     *
     * @param addr 地址
     */
    public synchronized void sendSearchCmd(byte addr) {
        int no = buildRequestNo();
        byte[] buffer = PlcPacket.buildSendPacket(SerialProtocolType.SEARCH_COMMAND, addr, (byte) 0x0, no);
        this.sendBuffers(buffer, SerialProtocolType.TYPE_GET_COMMAND);
    }

    /**
     * 发送设置命令
     *
     * @param level
     * @param addr
     */
    public synchronized void sendSetCmd(byte level, byte addr) {
        int no = buildRequestNo();
        byte[] buffer = PlcPacket.buildSendPacket(SerialProtocolType.SET_COMMAND, addr, (byte) 0x0, no);
        this.sendBuffers(buffer, SerialProtocolType.TYPE_SET_COMMAND);
    }

    public synchronized void close() {
        try {
            inputStream.close();
            outputStream.close();
            SerialUtil.closePort(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * /**
     * 读取串口返回信息
     *
     * @author LinWenLi
     * @date 2018年7月21日下午3:43:04
     * @return: void
     */
    public synchronized void readComm() {
        try {
            byte[] buffer = new byte[this.inputStream.available()];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                break;
            }
            System.out.println("收到新数据：" + Arrays.toString(buffer));

            //使用解包器进行解包
            List<byte[]> list = plcPacket.parsePackets(buffer);

            System.out.println("解析到数据包：" + list.size());

            for (byte[] itm : list) {
                System.out.println("数据包：" + Arrays.toString(itm));

                //获取帧序号,然后销毁队列相关的请求
                int no = Byte.toUnsignedInt(itm[2]);//帧序号
                if (request != null) {
                    //回调
                    PlcResponse response = new PlcResponse(request, itm);
                    request.callback(response);
                    //否则这个帧序号不一致怎么办？？和当前的序号对比
                    if (request.getNo() <= no) {
                        //已经收到消息了，直接释放锁
                        this.lock = 0;
                        this.timeout = 0;
                        this.request = null;
                    }
                }

                //buffer 中解析发给我的是什么数据
                if (this.callbackFunc != null) {
                    this.callbackFunc.callback(itm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //读取串口数据时发生IO异常
        }
    }

    /**
     * 自动发送请求
     * 需要等到上一帧返回后才能继续发送消息
     *
     * @throws IOException
     */
    public synchronized void request() throws IOException {
        //是否目前锁住了，不能发送消息
        if (lock > 0) return;

        //看看当前的优先设置队列是不是空
        this.request = setQueue.poll();
        if (this.request == null) {
            this.request = queue.poll();//
        }
        if (this.request == null) return;

        System.out.println("发送数据：" + Arrays.toString(request.getBuffer()));
        //发送数据
        outputStream.write(request.getBuffer());

        //验证是什么锁，需要锁住多少时间，默认全部5s
        this.timeout = 0;
        this.lock = 5;
    }

    //验证是否超时需要释放锁
    public synchronized boolean checkFree() {
        if (this.lock < this.timeout) {
            String msg = "PLC反馈超时，自动释放锁发送下一条数据，当前：" + queue.size() + " 条未发送，Lock=" + this.lock + ",Time=" + this.timeout;
            if (request != null) {
                msg += ",当前请求帧号：" + request.getNo() + "";
            }
            System.out.println(msg);
            errCount++;//错误次数

            //释放锁
            this.lock = 0;
            this.timeout = 0;

            this.request = null;
            return true;
        }
        if (errCount > 5) {
            String msg = "PLC反馈超时,跳关PLC与服务器的连接已掉线，请检查线路或重启服务器";
            config.pushMessage(10, msg, "error");
            errCount = 0;
        }
        return false;
    }

    /**
     * 没秒释放倒计时
     */
    public synchronized void freeLock() {
        this.timeout++;
    }
}
