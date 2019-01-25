package com.oeynet.dev.mockserver.domain.models;

public class Message {

    private int type = 0;
    private String message = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    private String theme = "primary";

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
