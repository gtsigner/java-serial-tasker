package com.oeynet.dev.mockserver.serial;


import com.oeynet.dev.mockserver.utils.ByteUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 解包
 */
public class PlcPacket {

    private byte[] oldBuffer = new byte[0];

    /**
     * 解析数据包
     *
     * @return
     */
    public synchronized List<byte[]> parsePackets(byte[] newBuffers) {
        List<byte[]> packs = new ArrayList<>();
        this.oldBuffer = ByteUtil.bytesMerge(oldBuffer, newBuffers);
        //循环取处理一下oldBuffer
        if (this.oldBuffer.length < 16) {
            return packs;
        }
        //可能有一个完整的数据包
        int st = -1;
        for (int i = 0; i < this.oldBuffer.length; i++) {
            byte ct = this.oldBuffer[i];
            //如果是开始
            if (ct == SerialProtocolType.START_TAG) {
                st = i;
            }
            //遇到尾巴
            if (ct == SerialProtocolType.END_TAG) {
                //这个只有尾巴没有头的，数据有问题，直接丢弃
                if (st == -1) {
                    break;
                } else {
                    packs.add(ByteUtil.subBytes(this.oldBuffer, st, i - st + 1));
                    st = -1;
                }
            }
        }
        //定义了开始
        if (st != -1) {
            this.oldBuffer = ByteUtil.subBytes(this.oldBuffer, st, this.oldBuffer.length - st);
        } else {
            this.oldBuffer = new byte[0];
        }
        return packs;
    }

    /**
     * 构建发送数据的包
     *
     * @param command
     * @param addr
     * @param level
     * @param no
     * @return
     */
    public static byte[] buildSendPacket(byte command, byte addr, byte level, int no) {
        byte[] buffer = new byte[24];//15个空字节
        Arrays.fill(buffer, (byte) 0x00);
        //前三个字节是写死的
        //拷贝末尾
        System.arraycopy(SerialProtocolType.HEAD_BYTES, 0, buffer, 0, 2);
        buffer[2] = (byte) no;//第2个字节
        buffer[3] = command;//命令
        buffer[17] = level;//跳关数据
        buffer[18] = 0x0;
        buffer[19] = addr;
        buffer[20] = 0x0;
        for (int i = 2; i < 20; i++) {
            buffer[20] += buffer[i];
        }
        System.arraycopy(SerialProtocolType.FOOT_BYTES, 0, buffer, 21, 3);
        //填充序号
        return buffer;
    }
}
