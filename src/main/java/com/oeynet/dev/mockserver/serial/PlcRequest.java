package com.oeynet.dev.mockserver.serial;

import com.oeynet.dev.mockserver.api.Config;
import com.oeynet.dev.mockserver.domain.models.ConfigGame;
import com.oeynet.dev.mockserver.domain.models.ConfigGameDevices;
import com.oeynet.dev.mockserver.domain.models.ConfigRoom;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;
import com.oeynet.dev.mockserver.utils.ByteUtil;

import java.util.ArrayList;

public class PlcRequest {

    private byte[] buffer;
    //超时
    private int timeout = 2000;
    private PlcResponse response;
    private int noByteIndex = 2;

    public PlcResponse getResponse() {
        return response;
    }

    public void setResponse(PlcResponse response) {
        this.response = response;
    }

    /**
     * 获取当前数请求数据的帧序号
     *
     * @return
     */
    public int getNo() {
        return Byte.toUnsignedInt(buffer[noByteIndex]);
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
                    cr.setBody(body.getBufferString());//包体的hex数据传递过去，然后通过前端来解析
                    //解析所有的状态

                    cr.getInputs().forEach(device -> {
                        int v = parseDevice(device, body);
                        device.setValue(v);
                    });

                    cr.getOutputs().forEach(device -> {
                        int v = parseDevice(device, body);
                        device.setValue(v);
                    });
                }
            }
        }
    }

    private int parseDevice(ConfigGameDevices device, PlcBody body) {
        String[] names = device.getStatus().split("\\-");
        if (names.length != 2) {
            device.setValue(2);//未知
        }
        System.out.println(names[0] + names[1]);
        //取第几个字节的第几位，然后分析高点还是低电
        int w = Integer.parseInt(names[0]) - 4;//第几个字节
        int v = Integer.parseInt(names[1]);//第几位
        v = parseGdVal(w, v, body.getBufferString());
        return v;
    }


    private int parseGdVal(int w, int v, String bufferStr) {
        String[] arr = bufferStr.split(",");
        byte val = (byte) Integer.parseInt(arr[w], 16);//具体字节
        String va = ByteUtil.byte2BinStr(val);
        //取第几位
        return Integer.parseInt(va.substring(v, v + 1));
    }
}
