package com.oeynet.dev.mockserver.domain.models;

import java.util.ArrayList;

/**
 * 每个房间有多个关卡
 */
public class ConfigRoom {

    private String title;
    private String no;
    private int status;
    private int id = 0;
    private boolean online = false;
    private int current = 0;
    private int action = 0;
    private ArrayList<ConfigGameLevel> levels = new ArrayList<>();
    private String body = "";
    private ArrayList<ConfigGameDevices> outputs = new ArrayList<>();
    private ArrayList<ConfigGameDevices> inputs = new ArrayList<>();


    public ArrayList<ConfigGameDevices> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<ConfigGameDevices> outputs) {
        this.outputs = outputs;
    }

    public ArrayList<ConfigGameDevices> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<ConfigGameDevices> inputs) {
        this.inputs = inputs;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<ConfigGameLevel> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<ConfigGameLevel> levels) {
        this.levels = levels;
    }

}
