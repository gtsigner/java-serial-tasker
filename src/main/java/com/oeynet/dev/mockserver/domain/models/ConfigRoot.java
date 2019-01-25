package com.oeynet.dev.mockserver.domain.models;

import java.util.ArrayList;

public class ConfigRoot {


    private ConfigSerial serial;

    public ConfigSerial getSerial() {
        return serial;
    }

    public void setSerial(ConfigSerial serial) {
        this.serial = serial;
    }

    public ArrayList<ConfigGame> getGames() {
        return games;
    }

    public void setGames(ArrayList<ConfigGame> games) {
        this.games = games;
    }

    private ArrayList<ConfigGame> games;

    private ConfigQueue queue;

    public ConfigQueue getQueue() {
        return queue;
    }

    public void setQueue(ConfigQueue queue) {
        this.queue = queue;
    }
}
