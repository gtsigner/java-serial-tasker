package com.oeynet.dev.mockserver.task;

import com.oeynet.dev.mockserver.api.Config;
import com.oeynet.dev.mockserver.serial.PlcSerialPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class PlcTask {

    /**
     * 同步查询PLC状态
     * 每两秒取查询一次服务器状态
     */
    @Scheduled(fixedRate = 5000)
    public synchronized void asyncPlc() {
        Config.getInstance().update();
    }

    /**
     * 不停的读取队列中的数据发送出去
     *
     * @throws IOException
     */
    @Scheduled(fixedRate = 5)
    public synchronized void sendRequest() throws IOException {
        PlcSerialPort port = Config.getInstance().getSerialPort();
        port.request();
    }

    /**
     * 释放Response 锁
     */
    @Scheduled(fixedRate = 1000)
    public synchronized void freeLock() {
        PlcSerialPort port = Config.getInstance().getSerialPort();
        port.freeLock();//5s 请求超时
        port.checkFree();
    }
}
