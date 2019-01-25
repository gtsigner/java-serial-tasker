package com.oeynet.dev.mockserver.api;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SerialApi implements DisposableBean {
    @Override
    public void destroy() throws Exception {
        Config.getInstance().getSerialPort().close();
    }
}
