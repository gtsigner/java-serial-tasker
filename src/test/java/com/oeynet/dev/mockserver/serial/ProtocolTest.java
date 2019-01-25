package com.oeynet.dev.mockserver.serial;

import com.oeynet.dev.mockserver.domain.models.ConfigRoom;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ProtocolTest {


    @Test
    public void parse() {
        byte[] buffer = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x00, 0x13,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x02, 0x00, 0x13,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x13,
        };

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
            //解析
            ConfigRoom room = body.getRoom();
            System.out.println(room.getAction());
        }
        System.out.println(list.size());
    }
}
