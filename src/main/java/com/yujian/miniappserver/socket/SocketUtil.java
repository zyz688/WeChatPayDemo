package com.yujian.miniappserver.socket;
import com.yujian.miniappserver.util.DesUtil;
import com.yujian.miniappserver.util.MeterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketUtil {

    //日志
    Logger logger = LoggerFactory.getLogger(SocketUtil.class);
    //包头
    private final String HEADER_DATA = "AA";
    //包尾
    private final String FOOTER_DATA = "BB";
    //明文密钥库
    private static final int PASSWORD_LENGTH = 256;
    public SocketUtil() {

    }

    /**
     * 打包数据
     *
     * @param data 未加密数据
     * @return 加密后数据
     */
    public String pack(String data) {
        //索引
//        int index = DESUtil.getIndex(PASSWORD_LENGTH);

        int index = 1;
        String keyIndex;
        if (index < 16) {
            keyIndex = "0" + Integer.toHexString(index).toUpperCase();
        } else {
            keyIndex = Integer.toHexString(index).toUpperCase();
        }
        //明文密钥
        String passwordX = DesUtil.getPassword(index);
        //加密随机数
        String random = DesUtil.getData();
//        String random = "AC901A8D";
        //MD5密文密钥
        // String passwordY = DESUtil.MD5encode(passwordX);
        //MD5密文密钥截取前8位并转大写
        //passwordY = passwordY.substring(0, 8).toUpperCase();
        //DES密文1
        String cipher1 = DesUtil.DESEncode(passwordX, random);
        //密钥KEY
        String KEY = DesUtil.cipherXor(cipher1, random);
        //AES加密数据
        String secretData = DesUtil.AESEncode(KEY, data);
        //加密数据长度
        String secretDataLength = Integer.toHexString(secretData.length() / 2) + "00";
        //数据包 = 包头 + 加密算法 + 密钥索引 + 加密随机数 + 数据长度 + 数据内容 + 包尾；
        return HEADER_DATA + "01" + keyIndex + random + secretDataLength + secretData + FOOTER_DATA;
    }
    //解密接收到的数据
    public String unpack(String data) {
        if ("AA".equals(data.substring(0, 2)) && "BB".equals(data.substring(data.length() - 2, data.length()))) {
            //截取密钥索引
            int index = Integer.parseInt(data.substring(4, 6), 16);
            //明文密钥
            String passwordX = DesUtil.getPassword(index);
            //截取加密随机数
            String random = data.substring(6, 14);
            //MD5密文密钥
            //String passwordY = DESUtil.MD5encode(passwordX);
            //MD5密文密钥截取前8位并转大写
            //passwordY = passwordY.substring(0, 8).toUpperCase();
            //DES密文1
            String cipher1 = DesUtil.DESEncode(passwordX, random);
            //密钥KEY
            String KEY = DesUtil.cipherXor(cipher1, random);
            //截取出数据内容
            String secretData = data.substring(18, data.length() - 2);
            //AES解密数据(如果字符串不是16字节整数倍，则已被服务端补0x02,需做过滤操作)
            String longData = DesUtil.AESDecode(KEY, secretData);
            //解密数据的过滤
            while (!"17".equals(longData.substring(longData.length() - 2, longData.length()))) {
                longData = longData.substring(0, longData.length() - 2);
            }
            return longData;
        }
        return null;
    }

    /**
     * 登入主站
     *
     * @param data 未加密数据 66 00 00 00 00 66 02 01 00 00 00 00 10 02 15 17
     */
    public Socket socketConnect(String data) {
        try {
            MySocket m = new MySocket();
            Socket socket = m.getSocket();
             logger.info("建立socket连接：" + socket);
//            logger.info("[\"+socket+\"]与主站建立连接成功！");
            sendPacket(socket, data);
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送数据到主站
     *
     * @param socket 当前socket连接
     * @param data   未加密数据
     * @return 主站返回的加密数据字符串
     */
    public String sendPacket(Socket socket, String data) {
        if (socket == null) {
            return null;
        }
        //加密数据
        String packet = pack(data);




       //  logger.info("["+socket+"]发送命令到主站:"+new MeterUtil().fillSpace(packet));
         logger.info("客户端发送packet（加密）："+new MeterUtil().fillSpace(packet));
        try {
            if (socket.isConnected() && !socket.isClosed()) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(DesUtil.hex2byte(packet));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "网络连接中断";
        }
        return packet;
    }

}
