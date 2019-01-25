package com.oeynet.dev.mockserver.domain.models;

public class ConfigGameLevel {
    private String title;
    private int value = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
