package com.oeynet.dev.mockserver.serial;

import com.oeynet.dev.mockserver.domain.models.ConfigRoom;

import java.util.ArrayList;

/**
 * PLC 服务器想要数据
 */
public class PlcResponse {


    private byte[] buffer;
    private PlcRequest request;

    public ArrayList<PlcBody> getBodies() {
        return bodies;
    }

    private ArrayList<PlcBody> bodies;

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public PlcRequest getRequest() {
        return request;
    }

    public void setRequest(PlcRequest request) {
        this.request = request;
    }

    /**
     * 创建一个响应成功的PLC
     *
     * @param request
     * @param buffer
     * @throws Exception
     */
    public PlcResponse(PlcRequest request, byte[] buffer) throws Exception {
        //解析
        int len = buffer.length;
        if (len <= 8) {
            throw new Exception("Response 解析失败，包长度：" + len);
        }
        //解析帧序号
        byte cmd = buffer[3];
        byte[] use = new byte[buffer.length - 8];
        System.arraycopy(buffer, 4, use, 0, use.length);
        this.bodies = parse(use);
        this.request = request;
    }

    /**
     * 解析出来设备列表数据
     *
     * @param buffer
     * @return
     */
    public static ArrayList<PlcBody> parse(byte[] buffer) {
        int count = buffer.length / 16;
        ArrayList<PlcBody> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int start = i * 16;
            byte[] buff = new byte[16];
            System.arraycopy(buffer, start, buff, 0, 16);
            PlcBody body = PlcBody.parse(buff);
            if (null != body) {
                list.add(body);
            }
        }
        return list;
    }
}
