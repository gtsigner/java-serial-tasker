package com.oeynet.dev.mockserver.api;

import com.oeynet.dev.mockserver.domain.models.ConfigGame;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;
import com.oeynet.dev.mockserver.domain.models.ConfigSerial;
import com.oeynet.dev.mockserver.domain.models.Message;
import com.oeynet.dev.mockserver.serial.PlcSerialPort;
import com.oeynet.dev.mockserver.serial.SerialProtocolType;
import com.oeynet.dev.mockserver.utils.FileRead;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;

public class Config {

    //单例
    private static Config _instance;
    private int code = 0;
    private PlcSerialPort serialPort;
    private ConfigRoot current;
    private boolean isConnected;
    //这个队列是获取信息的
    private Queue<Message> messageQueue = new ArrayDeque<>();

    /**
     * 队列添加消息
     *
     * @param message
     * @return
     */
    public boolean pushMessage(int type, String message, String theme) {
        int max = current.getQueue().getMessage();

        if (messageQueue.size() >= max) {
            messageQueue.poll();
        }
        //创建message 加入队列
        Message msg = new Message();
        msg.setMessage(message);
        msg.setType(type);
        msg.setTheme(theme);
        messageQueue.add(msg);
        return true;
    }

    public PlcSerialPort getSerialPort() {
        return serialPort;
    }

    /**
     * 这个getConfig只是获取了一个json配置文件的初始化状态，具体的状态需要后续对内存进行获取
     *
     * @return
     * @throws Exception
     */
    private ConfigRoot initConfig() throws Exception {
        try {
            current = FileRead.getConfigRoot();
        } catch (FileNotFoundException ex) {
            System.err.println("对不起，config/config.json 核心配置文件未找到");
            System.exit(-1);
        }
        return current;
    }

    private Config() throws Exception {
        ConfigRoot root = this.initConfig();
        //copy 一份
        current = root;
        //配置参数
        ConfigSerial serial = root.getSerial();
        serialPort = new PlcSerialPort(this, serial.getName(), serial.getBaud());
        serialPort.connect();//自动进行链接
        isConnected = true;
        serialPort.setCallbackFunc(buffer -> {
            byte type = buffer[3];
            //查询正确
            if (type == SerialProtocolType.RES_SUCCESS) {
                //解析数据表述查询主机状态信息


            }
            switch (type) {
                case SerialProtocolType.RES_SUCCESS:
                    break;
                case SerialProtocolType.RES_FAIL:
                    break;
                case SerialProtocolType.RES_SET_SUCCESS:
                    break;
                case SerialProtocolType.RES_SET_FAIL:
                    break;
                default:
                    break;
            }
        });


        pushMessage(1, "初始化成功", "success");
    }

    public ConfigRoot getCurrent() {
        return current;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static synchronized Config getInstance() {
        if (null == _instance) {
            try {
                _instance = new Config();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _instance;
    }

    public ConfigRoot getConfig() {
        return current;
    }


    public void update() {
        //不停的发送查询状态
        try {
            //查询所有的状态
            code++;
            if (code >= 255) {
                code = 0;
            }
            serialPort.sendCommand(SerialProtocolType.SEARCH_COMMAND, SerialProtocolType.ALL_ADDR);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Queue<Message> getMessageQueue() {
        return this.messageQueue;
    }

    public ConfigGame getGame(int id) {
        for (int i = 0; i < current.getGames().size(); i++) {
            ConfigGame game = current.getGames().get(i);
            if (id == game.getId()) {
                return game;
            }
        }
        return null;
    }
}
