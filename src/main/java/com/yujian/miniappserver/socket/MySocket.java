package com.yujian.miniappserver.socket;

import com.yujian.miniappserver.config.SocketServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class MySocket {
    //日志
    Logger logger = LoggerFactory.getLogger(MySocket.class);
    private Socket socket = null;

    public MySocket(){

    }
    public Socket getSocket() throws IOException {
       try{
           mClose();
           if (this.socket == null||this.socket.isClosed()) {
               this.socket = new Socket(SocketServerConfig.getIP(), SocketServerConfig.getPort());
               this.socket.setSoTimeout(60000);
              logger.info("创建Socket连接:"+this.socket);
               Thread.sleep(200);
           }
       }
       catch (InterruptedException e) {
           e.printStackTrace();
       }
        return this.socket;
    }
    public void mClose() throws IOException {
        if(this.socket!=null)
            this.socket.close();
        this.socket = null;
    }
}
