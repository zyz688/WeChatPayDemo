package com.yujian.miniappserver.socket;

import com.yujian.miniappserver.entity.MeterPara;
import com.yujian.miniappserver.util.DesUtil;
import com.yujian.miniappserver.util.MeterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class CmdOper {
    private String err="";
    private String balance="";
    private int RECHARGERESULT = 1;
    private String cmmId = "42";             //主站通讯地址与命令序号 16转2 进制 取后六位 值为 2 代表手机app命令

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    private  String  energy=""; //当前电表正向总电量

    MeterUtil mu = new MeterUtil();
    SocketUtil su = new SocketUtil();

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getRECHARGERESULT() {
        return RECHARGERESULT;
    }

    public void setRECHARGERESULT(int RECHARGERESULT) {
        this.RECHARGERESULT = RECHARGERESULT;
    }

    //日志
    Logger logger = LoggerFactory.getLogger(CmdOper.class);
    //主站通讯地址与命令序号       表示手机APP命令
    private String getCommId() {
        Random random = new Random();
        //给定一个参数n，nextInt(n)将返回一个大于等于0小于n的随机数，即：0 <= nextInt(n) < n。
        int a = random.nextInt(31);
        String CommAdd = Integer.toHexString(a);
        if (CommAdd.length() == 1) {
            CommAdd =  "0" + CommAdd;
        }
        cmmId = "42" + CommAdd.toUpperCase();
        return cmmId;
    }


    public void ReceiveEvent(Socket socket, CmdType type, MeterPara mp) {
        String packet;
        try {
            if (!socket.isClosed() && socket.isConnected() && !socket.isInputShutdown()) {
                InputStream inputStream = socket.getInputStream();
                BufferedInputStream in = new BufferedInputStream(inputStream);
                byte[] temp = new byte[2048];
                int n;
                while ((n = in.read(temp)) != -1) {
                    byte[] buffer = new byte[n];
                    System.arraycopy(temp, 0, buffer, 0, n);
                    //字节数组转 hex进制字符串
                    packet = DesUtil.byte2String(buffer);
                    logger.info("客户端接收" + mu.fillSpace(DesUtil.byte2String(buffer)));
                    if (packet.length() > 0) {
                        //加密数据 开头 AA   结尾 BB
                        if ("AA".equals(packet.substring(0, 2)) && "BB".equals(packet.substring(packet.length() - 2))) {
                            StringBuilder sb = new StringBuilder(packet);
                           // System.out.println("接受的数据 加密前>>>"+ sb);
                           do {
                                int dataLength = Integer.parseInt(sb.substring(14, 16), 16);
                             //  System.out.println("数据位长度"+dataLength);
                                packet = sb.substring(0, (10 + dataLength) * 2);
//                               System.out.println("packet >>>"+packet);
//                               System.out.println("packet长度 >>>"+packet.length());

                                sb.delete(0, (10 + dataLength) * 2);

                                //解密数据
                                packet = su.unpack(packet);
                                logger.info("客户端接收" + mu.fillSpace(packet));
                                if (packet != null) {
                                    //外部控制码
                                    String controlOut = packet.substring(12, 14);
                                    switch (controlOut) {
                                        case "01":
                                            //电表操作
                                            meterControl(packet);
                                            return;
                                        case "02":
                                            //客户端登入主站
                                            logger.info("客户端登入主站返回:" + packet);
                                            if (type == CmdType.QUERY_BALANCE) {
                                                //子表查询
                                                if (mp.getMotherMeterID() != null){

                                                    queryChildMeterBalance(socket,mp.getDBXH(),mp.getDBBH(),mp.getHH(),mp.getDCU(),getCommId(), mp.getMotherMeterID());
                                                }else {
                                                    //开始查询余额
                                                    logger.info("开始查询余额...");
                                                    queryMeterBalance(socket,mp.getDBXH(),mp.getDBBH(),mp.getHH(),mp.getDCU(),getCommId());
                                                }

                                            } else if (type == CmdType.METER_RECHARGE) {
                                                //开始电表充值
                                                logger.info("开始电表充值...");
                                                mu.recharge(socket, mp.getRechargeCode(), mp.getHH(), mp.getDCU(),getCommId(),
                                                        mp.getDBBH(),mp.getDBXH(),mp.getJZQBM(), mp.getTOKEN());
                                            }else if (type == CmdType.METER_ENERGR){
                                                //查询电表正向总功能 （总电量）
                                                logger.info("开始查询电表正向总功能...");
                                                mu.queryCurrentEnergy(socket,mp.getHH(),mp.getDCU(),getCommId(),mp.getDBBH(),mp.getDBXH());

                                            }
                                            break;
                                        case "03":
                                            //客户端心跳检测
//                                            IsRevHeartbeat = true;
//                                            Log.e("客户端心跳检测返回", packet);
                                            break;
                                        case "04":
                                            //客户端断开与主站链接
//                                            Log.e("客户端断开与主站链接返回", packet);
//                                            EventBus.getDefault().post(new DisconnectEvent(socket));
                                            return;
                                        default:
                                            break;
                                    }
                                } else {
                                    //解密出错，packet为空
                                }

                            } while (sb.length() != 0);   //while (sb.length() != 0);
                        } else {
                            //packet格式有误
                        }
                    } else {
                        //packet为空
                    }
                }
        //资源释放
                try {
                    if (inputStream != null){
                        inputStream.close();
                    }
                    if (in != null){
                        in.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } catch (SocketTimeoutException e) {
            //EventBus.getDefault().post(new ErrorEvent("Error 0xEE", "网络连接超时，请重新登入。"));
        } catch (IOException e) {
            //e.printStackTrace();
            //EventBus.getDefault().post(new ErrorEvent("Error 0xDD", "网络连接中断，请检查网络设置后重新登入。"));
        }
      //释放资源
        try {
            if (socket != null){
                socket.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void queryMeterBalance(Socket socket, String DBXH, String meterID, String HH, String DCU, String commAdd) {

        mu.queryDcuMeterBalance(socket, HH, DCU, commAdd, meterID, DBXH);
    }

    //子表查询
    private void queryChildMeterBalance(Socket socket, String DBXH, String DBBH, String HH, String DCU, String commAdd,String motherMeterID ) {

        //  motherMeterID 在下面方法中 高低位互换
        mu.queryChildDcuMeterBalance(socket, HH, DCU, commAdd, DBBH, DBXH,motherMeterID);
    }

    private void meterControl(String packet) {
        System.out.println("子表回复"+packet);
        String error = packet.substring(30, 32);//错误类型定义
        err = error;
        System.out.println("错误码" +err);
        switch (error) {
            case "01":
                //电表不存在
                //EventBus.getDefault().post(new ErrorEvent("Error 0x01", "电表不存在，请尝试重新登入。若无法解决，请联系管理员确认电表表号。"));
                break;
            case "02":
                //电表不在线
//                EventBus.getDefault().post(new ErrorEvent("02", "电表不在线"));
                break;
            case "03":
                //电表正忙
//                EventBus.getDefault().post(new ErrorEvent("03", "电表繁忙，请稍后再试！"));
                break;
            case "04":
                //电表正在执行自动任务
//                EventBus.getDefault().post(new ErrorEvent("04", "电表正在执行自动任务，请稍后再试！"));
                break;
            case "05":
                //电表无应答
//                EventBus.getDefault().post(new ErrorEvent("05", "电表无响应！"));
                break;
            case "06":
                //转发命令到设备失败
                //EventBus.getDefault().post(new ErrorEvent("Error 0x06","转发命令到设备失败，请尝试重新登入。若无法解决，请联系管理员确认电表状况。"));
                break;
            case "07":
                //服务端禁止接收该命令
                //EventBus.getDefault().post(new ErrorEvent("Error 0x07","服务端禁止接收该命令，请尝试重新登入。若无法解决，请联系管理员确认电表状况。"));
                break;
            case "FF":
                //未知错误
//                EventBus.getDefault().post(new ErrorEvent("FF", "电表出现未知错误！"));
                break;


            //这里代表 手机端与主站的 数据区  集中器与电表通讯为 68 开头  如果没有数据 那 就是错误类型  1个字节
            case "68":
                //内部控制码
                err = "";
                String controlIn = packet.substring(46, 48);
                String commId = packet.substring(40, 44).toUpperCase();
                //主站通讯地址与命令序号 16转2 进制 取后六位 00 00 10 值为2  代表手机app命令   cmmId 4005
                if(!commId.contains(cmmId)){
                    logger.info("命令序号不一样");
                    logger.info("commId="+commId+",cmmId="+cmmId);
                    return;
                }
                switch (controlIn) {
                    case "91":
                        //实时召测命令  集中器正常应答 取 控制码 91  子表 96
                        String coding91 = packet.substring(64, 68);
                        switch (coding91) {
                            case "10D0":
                            case "43D0":
                                //查询余额
                                getBalance(packet);
                                break;
                            case "1090":
                                    getEnergy(packet);
                                break;
                        }
                        break;
                    //子表查询余额回复码 96
                    case "96":
                       // System.out.println(packet);
                        //System.out.println("接收到的数据" + packet);
                        getChildBalance(packet);

                        break;
                    case "89":
                        //远程设置电表参数
                        String coding89 = packet.substring(68, 72);
                        switch (coding89) {
                            case "03F0":
                                //直接充值电表
                                recharge(packet);
                                break;
                            case "04F0":
                                //预充值电表
                                recharge(packet);
                                break;
                            case "05F0":
                                //确认、取消、查询预充值
                                recharge(packet);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    private void recharge(String packet) {
        String errorType = packet.substring(72, 74);
        if(errorType.contains("00")||errorType.contains("84"))
            this.RECHARGERESULT = 0; //预充值、直接充值、确认充值成功
        else
            this.RECHARGERESULT = 1; //预充值、直接充值、确认充值失败
    }
    private String hexToStrInt(String hex){
        BigInteger b = new BigInteger(hex, 16);
        int d = b.intValue();
        String v = String.valueOf(d);
        if(d<10)
            v = "0"+d;

        return v;
    }
    private void getBalance(String packet) {
        //使用正则表达式，将字符串左边的0去掉
        String data = packet.substring(68, 76);
        if(data.contains("FFFFFFFF")){
            this.balance = "";
            return;
        }
        double b = Double.parseDouble(data)/100d;
        //余额两位小数点
        this.balance = String.format("%.2f", b);
    }
    private void getChildBalance(String packet) {
        //使用正则表达式，将字符串左边的0去掉
        String data = packet.substring(80, 88);
        if(data.contains("FFFFFFFF")){
            this.balance = "";
            return;
        }
        double b = Double.parseDouble(data)/100d;
        //余额两位小数点
        this.balance = String.format("%.2f", b);
    }
    //获得正向有功总电能
    private void getEnergy(String packet) {

        String data = packet.substring(68, 76);

        String energy = mu.low2high(data);

           double d=  Double.parseDouble(energy)/100d;

           this.energy = String.format("%.2f", d);



    }
}