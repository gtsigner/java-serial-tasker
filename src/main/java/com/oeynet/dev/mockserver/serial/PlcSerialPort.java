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

public class PlcSerialPort {

    private SerialPort port = null;
    private String portName;
    private int baud;
    private InputStream inputStream;
    private OutputStream outputStream;


    //等待RS485串口服务器返回上一帧反馈信息（或失败延时）后，才能发送下一帧数据
    private Queue<PlcRequest> queue;
    private int requestNo = 0;

    //-1就没占用
    private int lock = -1;
    private PlcRequest request;

    private int errCount = 0;


    public boolean isConnected() {
        return isConnected;
    }

    private boolean isConnected = false;

    private Config config;

    /**
     * 收到数据的回调
     *
     * @param callbackFunc
     */
    public void setCallbackFunc(RecvCallbackInterface callbackFunc) {
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
        this.queue = new LinkedBlockingQueue<>(capacity);
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
     * @throws IOException
     */
    public void send(byte[] buff) throws IOException {
        //再发松之前先添加到队列里面
        PlcRequest request = new PlcRequest();
        request.setNo(requestNo);
        request.setBuffer(buff);
        if (config.getCurrent().getQueue().getRequest() <= queue.size()) {
            queue.poll();//废弃之前的请求
        }
        //添加到请求
        queue.add(request);
        requestNo++;
    }


    /**
     * 发送指令
     *
     * @param command 命令
     * @param addr    地址
     * @throws IOException
     */
    public void sendCommand(byte command, byte addr) throws IOException {
        byte[] buffer = new byte[24];//15个空字节
        Arrays.fill(buffer, (byte) 0x00);
        //前三个字节是写死的

        //拷贝末尾
        System.arraycopy(SerialProtocolType.HEAD_BYTES, 0, buffer, 0, 2);
        //帧序号
        Global.NO++;
        int no = Global.NO;
        //byte ff
        if (no > 255) {
            no = 0;
        }

        buffer[2] = (byte) no;//第2个字节
        buffer[3] = command;//命令

        buffer[19] = addr;
        //帧相加帧序号到从机地址的字节和

        buffer[20] = 0x0;
        //前面字节和
        for (int i = 2; i < 20; i++) {
            buffer[20] += buffer[i];
        }
        System.arraycopy(SerialProtocolType.FOOT_BYTES, 0, buffer, 21, 3);
        //填充序号
        this.send(buffer);
    }


    public void close() {
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
    public void readComm() {
        try {
            byte[] buffer = new byte[this.inputStream.available()];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                // 保存串口返回信息
                String data = new String(buffer, 0, len).trim();
                // 保存串口返回信息十六进制
                System.out.println("Recv Data：" + data);
                break;
            }
            //获取帧序号,然后销毁队列相关的请求
            int no = (int) buffer[2];//帧序号
            if (request != null) {
                String msg = "当前帧序号：" + request.getNo() + " 收到帧序号：" + no;
                System.out.println(msg);
                //否则这个帧序号不一致怎么办？？
                if (request.getNo() <= no) {
                    //释放锁
                    this.lock = -1;
                }
            }
            //和当前的序号对比
            //if (request != null && request.getNo() == no) {
            if (request != null) {
                //回调
                PlcResponse response = new PlcResponse(request, buffer);
                request.callback(response);
                this.lock = -1;
                this.request = null;
            }


            //buffer 中解析发给我的是什么数据
            if (this.callbackFunc != null) {
                this.callbackFunc.callback(buffer);
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
    public void request() throws IOException {
        //是否目前锁住了，不能发送消息
        if (lock > -1) return;
        this.request = queue.poll();//
        if (this.request == null) return;
        System.out.println("发送数据：" + Arrays.toString(request.getBuffer()));
        //发送数据
        outputStream.write(request.getBuffer());
        this.lock = 0;
    }


    /**
     * 释放锁
     *
     * @param timeout
     */
    public void freeLock(int timeout) {
        this.lock++;
        if (lock >= timeout + 1) {
            String msg = "PLC反馈超时,当前：" + queue.size() + " 条未发送";
            if (request != null) {
                msg += ",当前请求：" + request.getNo();
            }
//            config.pushMessage(5, msg, "error");
            System.out.println(msg);
            this.lock = -1;
            errCount++;//错误次数
        }
        if (errCount > 5) {
            String msg = "PLC反馈超时,跳关PLC与服务器的连接已掉线，请检查线路或重启服务器";
            config.pushMessage(10, msg, "error");
        }

    }
}
