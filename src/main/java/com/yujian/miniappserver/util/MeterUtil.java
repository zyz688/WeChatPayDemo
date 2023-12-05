package com.yujian.miniappserver.util;


import cn.hutool.http.HtmlUtil;
import com.yujian.miniappserver.config.SocketServerConfig;
import com.yujian.miniappserver.entity.PowerRecord;
import com.yujian.miniappserver.socket.SocketUtil;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Component
public class MeterUtil {











    /**
     *  添加支付宝 微信小程序 订单等
     * @param strRandom              加密随机数
     * @param strOrderNo             订单号
     * @param strAppID               该参数无用
     * @param strPaymentAmount       支付金额
     * @param strMeteringPointCode   计量点编码
     * @param strPurchaseTime        购电时间
     * @return
     * @throws Exception
     */
    public String addOrder(String strRandom, String strOrderNo, String strAppID, String strPaymentAmount, String strMeteringPointCode,
                                 String strPurchaseTime)throws Exception {


        //请求参数
        Map<String, String> map = new HashMap<>();
        map.put("strRandom",strRandom);
        map.put("strOrderNo",strOrderNo);
        map.put("strAppID",strAppID);
        map.put("strPaymentAmount",strPaymentAmount);
        map.put("strMeteringPointCode",strMeteringPointCode);
        map.put("strPurchaseTime",strPurchaseTime);
        //调用 添加购电记录
        String result = HttpClientUtil.doPost(SocketServerConfig.getWSDL_URI() + "addAlipayOrder", map);


    String s=    HttpClientUtil.getRootElementValue(result);
        System.out.println("解析出来的状态"+s);
return    s;
    

}








    /**
     *  添加购电记录
     * @param strRandom 加密随机数
     * @param strMeteringPointCode 计量点编码
     * @param strPaymentMode 支付方式（0:现金,1:微信,2:支付宝,3:银行卡，4：POS机，5：转账）
     * @param strPaymentAmount 支付金额（元）
     * @param token 充值token
     */
    public PowerRecord addPowerPurchaseRecord(String strRandom, String strMeteringPointCode, String strPaymentMode, String strPaymentAmount, String token) throws Exception {


        //请求参数
        Map<String, String> map = new HashMap<>();
        map.put("strRandom",strRandom);
        map.put("strMeteringPointCode",strMeteringPointCode);
        map.put("strPaymentMode",strPaymentMode);
        map.put("strPaymentAmount",strPaymentAmount);
        map.put("strToken",token);
        //调用 添加购电记录
        String result = HttpClientUtil.doPost(SocketServerConfig.getWSDL_URI() + "addPowerPurchaseRecord", map);




        //结果转为实体类
        return   parseraddPowerPurchaseRecord(result);
    }


    /**
     * 解析添加购电记录 返回的实体类
     *
     */

    public PowerRecord parseraddPowerPurchaseRecord(String xmlData)throws Exception {
        //webservice 返回的xml 可能被转译 我们调用工具包 转回去
        xmlData = HtmlUtil.unescape(xmlData);


        XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
        XmlPullParser parser=factory.newPullParser();
        parser.setInput(new StringReader(xmlData));

        PowerRecord powerRecord=null;
        int eventType=parser.getEventType();
        while (eventType!= XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:


                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("ROW")){
                        powerRecord=new PowerRecord();
                    }else if(parser.getName().equals("COL")){
                        switch (parser.getAttributeValue(0)){
                            case "JLDBM":
                                if(powerRecord!=null){
                                    String jldbm=parser.nextText();
                                    if( !jldbm.isEmpty()) {
                                        powerRecord.setJLDBM(Integer.parseInt(jldbm));
                                    }
                                }
                                break;
                            case "GDSJ":
                                if(powerRecord!=null){
                                    String gdsj=parser.nextText();
                                    powerRecord.setGDSJ(gdsj);
                                }
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:

                    break;
            }
            eventType=parser.next();
        }


        return  powerRecord;
    }






    /**
     * 充值TOKEN
     * @param strRandom  加密随机数
     * @param strMeteringPointCode  计量点编码
     * @param strAmount 充值金额（元） 加密前需要扩大100倍
     */
    public String getRechargeToken(String strRandom, String strMeteringPointCode, String strAmount) throws Exception {

        //请求参数
        Map<String,String> map = new HashMap<>();
        map.put("strRandom",strRandom);
        map.put("strMeteringPointCode",strMeteringPointCode);
        map.put("strAmount",strAmount);



        //通过发起请求 webservice 得到返回的 xml 我们发现已经被转译
         String result = HttpClientUtil.doPost(SocketServerConfig.getWSDL_URI() + "generateRechargeToken", map);
//        System.out.println("转译之前----" + result);



        return  getTokenXML(result) ;

    }




    private String getTokenXML(String xmlData) throws Exception {

        //webservice 返回的xml 可能被转译 我们调用工具包 转回去
        xmlData = HtmlUtil.unescape(xmlData);


        XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
        XmlPullParser parser=factory.newPullParser();
        parser.setInput(new StringReader(xmlData));
        String token = "";
        int eventType=parser.getEventType();
        while (eventType!= XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(parser.getName().equals("COL")){
                        switch (parser.getAttributeValue(0)){
                            case "TOKEN":
                                token = parser.nextText();
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:

                    break;
            }
            eventType=parser.next();
        }
        return token;
    }

    /**
     * 查询电表实时电量
     *
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH 电表序号
     */
    public String queryCurrentEnergy(Socket socket, String HH, String DCU, String CommAdd, String meterID, String DBXH) {
        String result = "连接断开";
        if (socket == null) {
            return result;
        }
        // 包装查询余额查询数据包内容
        String data = packetCurrentEnergy(HH, DCU, CommAdd, meterID, DBXH);


        result = new SocketUtil().sendPacket(socket, data);
        return result;
    }


    /**
     * 查询电表余额
     *
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH 电表序号
     */
    public String queryDcuMeterBalance(Socket socket, String HH, String DCU, String CommAdd, String meterID, String DBXH) {
        String result = "连接断开";
        if (socket == null) {
            return result;
        }
        // 包装查询余额查询数据包内容
       String data = packetBalance(HH, DCU, CommAdd, meterID, DBXH);


        result = new SocketUtil().sendPacket(socket, data);
        return result;
    }
    /**
     * 查询子电表余额
     *
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH 电表序号
     */
    public String queryChildDcuMeterBalance(Socket socket, String HH, String DCU, String CommAdd, String meterID, String DBXH,String motherMeterID) {
        String result = "连接断开";
        if (socket == null) {
            return result;
        }
        // 包装查询余额查询数据包内容
//        String data = packetBalance(HH, DCU, CommAdd, meterID, DBXH);
        String data = packetBalanceChild(HH,DCU,CommAdd,meterID,DBXH,motherMeterID);

        result = new SocketUtil().sendPacket(socket, data);
        return result;
    }
    /**
     * 充值电表
     *
     * @param rechargeCode      充值类型代码（数据项编码）
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID //
     * @param CommAdd 主站通讯地址与命令序号 //
     * @param meterID 电表ID
     * @param DBXH 电表序号
     * @param TOKEN  一次性TOKEN码
     */
    public String recharge(Socket socket, int rechargeCode, String HH, String DCU, String CommAdd, String meterID, String DBXH,int JZQBM, String TOKEN) {
        String result = "";
        String data = packet4recharge(rechargeCode, HH, DCU, CommAdd, meterID, DBXH,JZQBM, TOKEN);
        result = new SocketUtil().sendPacket(socket, data);
        return result;
    }

    /**
     * 打包充值数据
     *
     * @param rechargeCode      充值类型代码（数据项编码）
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH   电表序号
     * @param TOKEN   TOKEN码
     * @return 打包后的充值数据
     */
    private String packet4recharge(int rechargeCode, String HH, String DCU, String CommAdd, String meterID, String DBXH, int JZQBM, String TOKEN) {
        String dataCoding = "";
        switch (rechargeCode) {
            case 3:
                //直接充值
                dataCoding = "03F0";
                break;
            case 4:
                //预充值
                dataCoding = "04F0";
                break;
            case 5:
                //确认、取消、查询预充值
                dataCoding = "05F0";
                break;
            default:
                break;
        }

//        HH = low2high(HH);
//        meterID = low2high(meterID);
//        DBXH = low2high(DBXH);

        String csStr="";
//        DCU = DCU.substring(0, 4) + low2high(DCU.substring(4, 8));
        String dataLength="";
        String data="";
        csStr="68" + DCU + CommAdd + "6809";
        if(JZQBM==0){
            data= "0100113333333333333333333333" + dataCoding + TOKEN;
        }else{
            //通讯密码
            String pwd = "333333333333";
            data = "010011" +pwd + meterID + DBXH + "3333333333333333FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" + dataCoding + TOKEN;
        }
        dataLength = Integer.toHexString(data.length()/2);
        for(int i=0;i<4;i++){
            if(dataLength.length()<4){
                dataLength="0"+dataLength;
            }else{
                break;
            }
        }
        csStr=csStr+low2high(dataLength)+data;
        String csIn = getCS(csStr);
        data=csStr+csIn+"16";
        dataLength=Integer.toHexString(data.length()/2);
        for(int i=0;i<4;i++){
            if(dataLength.length()<4){
                dataLength="0"+dataLength;
            }else{
                break;
            }
        }

        csStr = "01" + HH + "02"+data;
        dataLength=low2high(dataLength);
        String csOut = getCS(csStr);
        return "66"+dataLength+dataLength+"66"+csStr+csOut+"17";
    }

    /**
     * 查询余额所需发送的数据包内容
     *
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH 电表序号
     * @return 未加密查询余额数据包
     */
    private String packetBalance(String HH, String DCU, String CommAdd, String meterID, String DBXH) {
       
        //68开头 16结尾 集中器发电表
        String csStr = "68" + DCU + CommAdd + "6811" ; // 11 控制码 代表 召读电表数据
        //1090 为查询电量
        //本来是D043   低字节在前
        String dataId = "43D0";
       // String dataId = "1090";

        String data = meterID + DBXH + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"+dataId;

        //data 数据 字节个数 转 16 进制  然后 高低位互换
        String dataLength=Integer.toHexString(data.length()/2);
        //数据区长度 两个字节
        for(int i=0;i<4;i++){
            if(dataLength.length()<4){
                dataLength="0"+dataLength;
            }else{
                break;
            }
        }

        //组装命令 68 - 68  （11 控制码） 数据长度 数据区
        csStr = csStr + low2high(dataLength)+data;
      //  System.out.println("手机客户端发主站数据区字节个数"+csStr.length()/2);
        String csIn = getCS(csStr);
        //01控制码 采集设备的操作 02 客户端发送到主站 HH 终端id  "02"后面数据区 +校验位
        //数据区长度
      //int len =(csStr + csIn + "16").length() / 2;
      //  System.out.println("手机与主站通讯数据区长度 2个字节 低字节在前 >>>" +Integer.toString(len,16));
        csStr = "01" + HH + "02" + csStr + csIn + "16";
        String csOut = getCS(csStr);

        //手机客户端与主站通讯
        return "663000300066" + csStr + csOut + "17";
    }





    /**  子表查询
     * 查询余额所需发送的数据包内容
     *
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH 电表序号
     * @return 未加密查询余额数据包
     */
    private String packetBalanceChild(String HH, String DCU, String CommAdd, String meterID, String DBXH , String motherMeterID) {
        //后两个字节 高低位互换

        //68开头 16结尾 集中器发电表
        String csStr = "68" + DCU + CommAdd + "6816" ; //  16控制码 代表 召读子表数据
        //1090 为查询电量
        //本来是D043  低字节在前 数据id
        String dataId = "43D0";

        //查询子表 母表表号位置 高低位互换                                                                   //子表id位置 子表表号 子表数据区都是在数据ID前增加子表表号
                                                                                                        //对于电表回复的数据，如果是子表，那么都会在母表表号后面再接子表表号
        String data =  motherMeterID + DBXH + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"+meterID+dataId;

        //data 数据 字节个数 转 16 进制  然后 高低位互换
        String dataLength=Integer.toHexString(data.length()/2);
        //数据区长度 两个字节
        for(int i=0;i<4;i++){
            if(dataLength.length()<4){
                dataLength="0"+dataLength;
            }else{
                break;
            }
        }

        //组装命令 68 - 68  （11 控制码） 数据长度 数据区
        csStr = csStr + low2high(dataLength)+data;
        //System.out.println("手机客户端发主站数据区字节个数"+csStr.length()/2);
        String csIn = getCS(csStr);
        //01控制码 采集设备的操作 02 客户端发送到主站 HH 终端id  "02"后面数据区 +校验位
        //数据区长度
        int len =(csStr + csIn + "16").length() / 2;
        String appDataLen = Integer.toString(len,16);
        // 2个字节不够补0
        if (appDataLen.length() < 4){
            appDataLen = "0000"+appDataLen;
            appDataLen = appDataLen.substring(appDataLen.length() -4);
        }
        //低位在前 数据区长度
        appDataLen   = low2high(appDataLen);
        //System.out.println("手机与主站通讯数据区长度 2个字节 低字节在前 >>>" +appDataLen);


        //终端id  HH
        csStr = "01" + HH + "02" + csStr + csIn + "16";
        String csOut = getCS(csStr);

        //手机客户端与主站通讯 66 3600 3600 66 01 590000000110 02 68 11000900 4211 68 16 2900 140000111122 1900 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF 090000111122 43D0 34 16 eb 17
        return "66" + appDataLen +appDataLen + "66" + csStr + csOut + "17";
    }






    /**
     * 包装查询实时电量数据内容
     *
     * @param HH      低压计量点表中的户号：5位区域编号+7位区域内编号
     * @param DCU     集中器ID
     * @param CommAdd 主站通讯地址与命令序号
     * @param meterID 电表ID
     * @param DBXH 电表序号
     * @return 未加密查询余额数据包
     */
    private String packetCurrentEnergy(String HH, String DCU, String CommAdd, String meterID, String DBXH) {

        //68开头 16结尾 集中器发电表
        String csStr = "68" + DCU + CommAdd + "6811" ; // 11 控制码 代表 召读电表数据
        //1090 为查询电量
         String dataId = "1090";

        String data = meterID + DBXH + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"+dataId;

        //data 数据 字节个数 转 16 进制  然后 高低位互换
        String dataLength=Integer.toHexString(data.length()/2);
        //数据区长度 两个字节
        for(int i=0;i<4;i++){
            if(dataLength.length()<4){
                dataLength="0"+dataLength;
            }else{
                break;
            }
        }

        //组装命令 68 - 68  （11 控制码） 数据长度 数据区
        csStr = csStr + low2high(dataLength)+data;
        //  System.out.println("手机客户端发主站数据区字节个数"+csStr.length()/2);
        String csIn = getCS(csStr);
        //01控制码 采集设备的操作 02 客户端发送到主站 HH 终端id  "02"后面数据区 +校验位
        //数据区长度
        //int len =(csStr + csIn + "16").length() / 2;
        //  System.out.println("手机与主站通讯数据区长度 2个字节 低字节在前 >>>" +Integer.toString(len,16));
        csStr = "01" + HH + "02" + csStr + csIn + "16";
        String csOut = getCS(csStr);

        //手机客户端与主站通讯
        return "663000300066" + csStr + csOut + "17";
    }







    /**
     * 十六进制校验位计算
     *
     * @param csStr 十六进制字符串
     */
    public String getCS(String csStr) {
        int sum = 0;
        String temp;

        if (csStr.length() != 0) {
            for (int i = 0; i < csStr.length() / 2; i++) {
                temp = csStr.substring(2 * i, 2 * i + 2);
                // 一个字节一个字节--> 16进制转为10进制
                sum = sum + Integer.parseInt(temp, 16);
            }
        }
        //从帧起始符开始到校验码之前的所有各字节的和模256的余
        sum = sum % 256;

        String cs = Integer.toHexString(sum);
        if (cs.length() < 2) {
            cs = "0" + cs;
        }
        return cs;
    }
    /**
     * 偶数字符串高低位互换
     */
    public String low2high(String str) {
        String result = "";
        for (int i = 0; i < str.length() / 2; i++) {
            result = str.substring(i * 2, i * 2 + 2) + result;
        }
        return result;
    }
     //填充空间
    public String fillSpace(String str){
        String result = "";
        for (int i = 0; i < str.length() / 2; i++) {
            if(i!=0)
                result += " " + str.substring(i * 2, i * 2 + 2);
            else
                result += str.substring(0, 2);
        }
        return result;
    }
}
