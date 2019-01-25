package com.oeynet.dev.mockserver.domain.models;

public class ConfigSerial {
    private String name;
    private int baud;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaud() {
        return baud;
    }

    public void setBaud(int baud) {
        this.baud = baud;
    }
}
