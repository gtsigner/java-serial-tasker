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
     */
    @Scheduled(fixedRate = 1000)
    public void asyncPlc() {
        Config.getInstance().update();
    }

    /**
     * 不停的读取队列中的数据发送出去
     *
     * @throws IOException
     */
    @Scheduled(fixedRate = 50)
    public void sendRequest() throws IOException {
        PlcSerialPort port = Config.getInstance().getSerialPort();
        port.request();
    }

    /**
     * 释放Response 锁
     */
    @Scheduled(fixedRate = 1000)
    public void freeLock() {
        PlcSerialPort port = Config.getInstance().getSerialPort();
        port.freeLock(5);//2s 请求超时
    }
}
