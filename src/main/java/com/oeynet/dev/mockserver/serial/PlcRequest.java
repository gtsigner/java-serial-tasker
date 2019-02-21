package com.oeynet.dev.mockserver.serial;

import com.oeynet.dev.mockserver.api.Config;
import com.oeynet.dev.mockserver.domain.models.ConfigGame;
import com.oeynet.dev.mockserver.domain.models.ConfigRoom;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;

import java.util.ArrayList;

public class PlcRequest {

    private byte[] buffer;
    //超时
    private int timeout = 2000;
    private int no = 0;
    private PlcResponse response;

    public PlcResponse getResponse() {
        return response;
    }

    public void setResponse(PlcResponse response) {
        this.response = response;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    private RecvCallbackInterface callbackInterface;

    /**
     * @return
     */
    public RecvCallbackInterface getCallbackInterface() {
        return callbackInterface;
    }

    public void setCallbackInterface(RecvCallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    /**
     * 最后解析，设置响应的PLC状态机
     *
     * @param response
     */
    public void callback(PlcResponse response) {
        this.response = response;
        ArrayList<PlcBody> bodies = response.getBodies();


        //获取当前游戏配置状态
        ConfigRoot root = Config.getInstance().getCurrent();
        ConfigGame game = root.getGames().get(0);//目前就只有1个游戏


        for (PlcBody body : bodies) {
            ConfigRoom room = body.getRoom();
            //设置对应房间的状态
            System.out.println("房间：" + room.getId() + " 关卡：" + room.getCurrent());
            for (ConfigRoom cr : game.getRooms()) {
                if (cr.getId() == room.getId()) {
                    //设置状态
                    cr.setCurrent(room.getCurrent());
                }
            }
        }
    }
}
