package com.yujian.miniappserver.service;


import com.yujian.miniappserver.entity.MeterPara;
import com.yujian.miniappserver.socket.CmdOper;
import com.yujian.miniappserver.socket.CmdType;
import com.yujian.miniappserver.socket.SocketUtil;
import com.yujian.miniappserver.util.MeterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ze
 * @create 下午 02:52
 */
@Service
public class MeterOperation {
    Logger logger = LoggerFactory.getLogger(MeterOperation.class);
    @Autowired
    public  MeterUtil mu ;


    //查询电表余额
    public Map queryMeterBalance(MeterPara meterPara) {
        Map map = new HashMap();
        //主站登入
        //66  00 00 00 00  66  02  00 00 00 00 00 00  02  04  17
        String csStr = "02" + meterPara.getHH() + "02";
        //校验位  主站登入无数据直接校验位   校验位 从控制码到校验位之前
        String csOut = mu.getCS(csStr);
        String data = "660000000066" + csStr + csOut + "17";
        SocketUtil su = new SocketUtil();
        //登入主站
        Socket socket = su.socketConnect(data);
        CmdOper co = new CmdOper();
        co.ReceiveEvent(socket, CmdType.QUERY_BALANCE,meterPara);

        String balance = co.getBalance();
         logger.info("余额为+++"+balance);

        map.put("balance",balance);


        return     map;

    }


    //查询电表当前总电量
    public  Map queryCurrentEnergy(MeterPara meterPara){
        Map map = new HashMap();
        //主站登入
        //66  00 00 00 00  66  02  00 00 00 00 00 00  02  04  17
        String csStr = "02" + meterPara.getHH() + "02";
        //校验位  主站登入无数据直接校验位   校验位 从控制码到校验位之前
        String csOut = mu.getCS(csStr);
        String data = "660000000066" + csStr + csOut + "17";
        SocketUtil su = new SocketUtil();
        //登入主站
        Socket socket = su.socketConnect(data);
        CmdOper co = new CmdOper();
        co.ReceiveEvent(socket, CmdType.METER_ENERGR,meterPara);
        //获得电量 封装map
        map.put("currentEnergy",co.getEnergy());


        return  map;
}







}
