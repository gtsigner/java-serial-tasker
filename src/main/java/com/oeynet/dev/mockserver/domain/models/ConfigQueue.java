package com.oeynet.dev.mockserver.domain.models;

public class ConfigQueue {
    private int request = 100;
    private int message = 100;

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}
