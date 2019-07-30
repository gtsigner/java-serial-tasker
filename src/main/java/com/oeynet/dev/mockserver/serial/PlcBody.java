package com.oeynet.dev.mockserver.serial;

import com.oeynet.dev.mockserver.domain.models.ConfigRoom;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;
import com.oeynet.dev.mockserver.utils.ByteUtil;

import java.util.ArrayList;

/**
 * 解析一组16位数据
 */
public class PlcBody {
    //    输入数据1(D8-D15)		对应PLC输入端口：X10-X17
//    输入数据1(D0-D7)		对应PLC输入端口：X00-X07
//    输入数据2(D8-D15)		对应PLC输入端口：X30-X37
//    输入数据2(D0-D7)		对应PLC输入端口：X20-X27
//    输入数据3(D8-D15)		对应PLC输入端口：预留
//    输入数据3(D0-D7)		对应PLC输入端口：D0=X40  D1=X41  D2=X42  D3=X43  D4-D7预留
//    输出数据1(D8-D15)		对应PLC输出端口：Y20-Y27
//    输出数据1(D0-D7)		对应PLC输出端口：Y10-Y17
//    输出数据2(D8-D15)		对应设置与反馈信息：D14=1从机完成设置 D15=1 主机设置输出口
//    输出数据2(D0-D7)		对应PLC输出端口：Y30-Y37
//    MP3数据1(D8-D15)		D8-D12:MP3音量0x0-0x1e,D13:路数,D14=1从机已经播放, D15=1 主机令从机播放MP3某曲目
//    MP3数据1(D0-D7)	0x01-0xff	对应MP3的播放曲目
//    跳关数据1(D8-D15)		对应跳关设置与反馈信息：D14=1从机完成跳关 D15=1 主机令从机跳关
//    跳关数据1(D0-D7)	0x00-0xff	当前第几关卡
//    从机状态(D8-D15)		D15=0:从机离线  D15=1:从机在线
//    从机地址(D0-D7)	0x0-0x1f	0x0:广播地址(查询所有从机)  0x1-0x1f: 查询单台从机

    private byte[] buffer;
    private byte data1_l;
    private byte data1_t;
    private byte data2_l;
    private byte data2_t;
    private byte data3_l;
    private byte data3_t;
    private byte data4_l;
    private byte data4_t;
    private byte mp3_l;
    private byte mp3_t;
    private byte level_l;
    private byte level_t;
    private boolean isOnline = false;//是否在线
    private byte addr;//地址

    public byte getData1_l() {
        return data1_l;
    }

    public void setData1_l(byte data1_l) {
        this.data1_l = data1_l;
    }

    public byte getData1_t() {
        return data1_t;
    }

    public void setData1_t(byte data1_t) {
        this.data1_t = data1_t;
    }

    public byte getData2_l() {
        return data2_l;
    }

    public void setData2_l(byte data2_l) {
        this.data2_l = data2_l;
    }

    public byte getData2_t() {
        return data2_t;
    }

    public void setData2_t(byte data2_t) {
        this.data2_t = data2_t;
    }

    public byte getData3_l() {
        return data3_l;
    }

    public void setData3_l(byte data3_l) {
        this.data3_l = data3_l;
    }

    public byte getData3_t() {
        return data3_t;
    }

    public void setData3_t(byte data3_t) {
        this.data3_t = data3_t;
    }

    public byte getData4_l() {
        return data4_l;
    }

    public void setData4_l(byte data4_l) {
        this.data4_l = data4_l;
    }

    public byte getData4_t() {
        return data4_t;
    }

    public void setData4_t(byte data4_t) {
        this.data4_t = data4_t;
    }

    public byte getMp3_l() {
        return mp3_l;
    }

    public void setMp3_l(byte mp3_l) {
        this.mp3_l = mp3_l;
    }

    public byte getMp3_t() {
        return mp3_t;
    }

    public void setMp3_t(byte mp3_t) {
        this.mp3_t = mp3_t;
    }

    public byte getLevel_l() {
        return level_l;
    }

    public void setLevel_l(byte level_l) {
        this.level_l = level_l;
    }

    public byte getLevel_t() {
        return level_t;
    }

    public void setLevel_t(byte level_t) {
        this.level_t = level_t;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public byte getAddr() {
        return addr;
    }

    public void setAddr(byte addr) {
        this.addr = addr;
    }

    public static PlcBody parse(byte[] buffer) {
        if (buffer.length != 16) {
            return null;
        }
        PlcBody body = new PlcBody();
        body.setBuffer(buffer);
        body.setAddr(buffer[15]);
        //0x01
        body.setOnline(buffer[14] == 0x01);
        return body;
    }


    public byte[] getBuffer() {
        return buffer;
    }

    public String getBufferString() {
        return ByteUtil.byteArray2String(buffer);
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    @Override
    public String toString() {
        return new String(getBuffer());

    }

    //一个plc body 代表一个room
    public ConfigRoom getRoom() {
        ConfigRoom room = new ConfigRoom();
        //15 从机地址
        room.setId(buffer[15]);
        room.setOnline(isOnline);
        //跳关关卡
        room.setCurrent(buffer[13]);
        //对应设置反馈信息
        room.setStatus(buffer[12]);
        if (buffer[12] == 1) {
            //主机令从机进行跳关
            room.setAction(1);
        } else if (buffer[12] == 2) {
            //从机完成
            room.setAction(2);
        }
        return room;
    }
}
