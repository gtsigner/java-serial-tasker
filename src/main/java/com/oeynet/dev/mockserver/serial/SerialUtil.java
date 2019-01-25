package com.oeynet.dev.mockserver.serial;

import gnu.io.*;

public class SerialUtil {

    /**
     * 打老端口
     *
     * @param portName 端口名称
     * @param baudrate 波特率
     * @return null | SerialPort
     * @throws NoSuchPortException
     * @throws UnsupportedCommOperationException
     * @throws PortInUseException
     */
    public static SerialPort connect(String portName, int baudrate) throws Exception {
        //通过端口名称识别端口
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            throw new Exception("Error: Port is currently in use");
        }
        //打开串口
        CommPort commPort = portIdentifier.open(portName, 2000);

        //判断是否是串口
        if (commPort instanceof SerialPort) {
            SerialPort serialPort = (SerialPort) commPort;
            // 设置一下串口的波特率等参数
            // 数据位：8
            // 停止位：1
            // 校验位：None
            serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            return serialPort;
        } else {
            return null;
        }
    }

    /**
     * 关闭串口
     *
     * @param port 串口实例
     */
    public static void closePort(SerialPort port) {
        if (port != null) {
            port.notifyOnDataAvailable(false);
            port.removeEventListener();
            port.close();
        }
    }
}
