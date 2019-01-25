package com.oeynet.dev.mockserver.domain.models;


import java.util.ArrayList;

public class ConfigGame {

    private ArrayList<ConfigRoom> rooms = new ArrayList<>();
    private Boolean enable = false;
    private String title = "";
    private String note = "";
    private int id = 0;
    private int ready = 0;
    private int start = 0;
    private int reset = 0;

    public int getReady() {
        return ready;
    }

    public void setReady(int ready) {
        this.ready = ready;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getReset() {
        return reset;
    }

    public void setReset(int reset) {
        this.reset = reset;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<ConfigRoom> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<ConfigRoom> rooms) {
        this.rooms = rooms;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
