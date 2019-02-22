package com.oeynet.dev.mockserver.serial;

public class SerialProtocolType {

    //头部
    public static final byte[] HEAD_BYTES = new byte[]{
            0x3e,//帧头
            0x7b,//{
    };

    //尾部
    public static final byte[] FOOT_BYTES = new byte[]{
            0x7d,//}
            0x0d,//\r
            0x0a//\n
    };


    public static final byte START_TAG = 0x3e;
    public static final byte END_TAG = 0x0a;


    public static final byte SEARCH_COMMAND = 0x01;//查询
    public static final byte SET_COMMAND = 0x02;//设置

    public static final byte ALL_ADDR = 0x00;//所有地址

    //查询正确
    public static final byte RES_SUCCESS = (byte) 0x81;
    //查询失败
    public static final byte RES_FAIL = (byte) 0xc1;


    //设置
    public static final byte RES_SET_SUCCESS = (byte) 0x82;
    //设置失败
    public static final byte RES_SET_FAIL = (byte) 0xc2;


    public static final int TYPE_SET_COMMAND = 1;
    public static final int TYPE_GET_COMMAND = 2;

}
