package com.oeynet.dev.mockserver.utils;

public class ByteUtil {
    private static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] bytesMerge(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static String byteArray2String(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i < data.length - 1)
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    public static String byte2BinStr(byte data) {
        return Integer.toBinaryString((data & 0xFF) + 0x100).substring(1);
    }

    public static String byteArr2BinArr(byte[] bytes) {
        StringBuilder str = new StringBuilder();
        for (byte b : bytes) {
            str.append(Long.toString(b & 0xff, 2));
        }
        return str.toString();
    }
}
