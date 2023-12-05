package com.yujian.miniappserver.service;

import com.thoughtworks.xstream.XStream;
import com.yujian.miniappserver.config.WxpayConfig;
import com.yujian.miniappserver.entity.*;
import com.yujian.miniappserver.socket.CmdOper;
import com.yujian.miniappserver.socket.CmdType;
import com.yujian.miniappserver.socket.SocketUtil;
import com.yujian.miniappserver.util.DesUtil;
import com.yujian.miniappserver.util.HttpClientUtil;
import com.yujian.miniappserver.util.MeterUtil;
import com.yujian.miniappserver.util.Signature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ze
 * @create 下午 03:47
 */


@Service
public class WxpayService {

    Logger logger = LoggerFactory.getLogger(WxpayService.class);


    @Autowired
    MeterUtil meterUtil;











    //微信支付的预定单
    public Map<String, Object> payment(String body, String out_trade_no, String total_fee, String openid,String detail){
        Map<String, Object> map = new HashMap<>();
        try {
        //装填预订单 实体类
        OrderInfo orderInfo = new OrderInfo();
            //小程序ID
             orderInfo.setAppid(WxpayConfig.appid);
            //商户号
             orderInfo.setMch_id(WxpayConfig.mch_id);
            //随机字符串
             orderInfo.setNonce_str(DesUtil.getRandomStringByLength(32));

             //商品标题
             orderInfo.setBody(body);
             //订单详情
              orderInfo.setDetail(detail);
            //商品订单号
             orderInfo.setOut_trade_no(out_trade_no);
             //标价 金额 单位分
             orderInfo.setTotal_fee(total_fee);
             //终端IP
             orderInfo.setSpbill_create_ip("127.0.0.1");
             // 支付成功 通知地址
             orderInfo.setNotify_url(WxpayConfig.notify_url);
             //交易类型 JSAPI
             orderInfo.setTrade_type(WxpayConfig.trade_type);
             //用户 openid
             orderInfo.setOpenid(openid);
             //签名 类型
             orderInfo.setSign_type("MD5");
             //签名

            orderInfo.setSign(Signature.getSign(orderInfo));

            //发起下单请求
             String result = HttpClientUtil.sendPostXML(WxpayConfig.url, orderInfo);
             logger.info(result);

            XStream xStream = new XStream();
            xStream.alias("xml", OrderReturnInfo.class);

            OrderReturnInfo returnInfo = (OrderReturnInfo) xStream.fromXML(result);
            
             logger.info("下单是否成功---"+returnInfo.getReturn_code());
            // 二次签名
            if ("SUCCESS".equals(returnInfo.getReturn_code()) && returnInfo.getReturn_code().equals(returnInfo.getResult_code())) {

                SignInfo signInfo = new SignInfo();
                signInfo.setAppId(WxpayConfig.appid);
                long time = System.currentTimeMillis() / 1000;
                signInfo.setTimeStamp(String.valueOf(time));
                signInfo.setNonceStr(DesUtil.getRandomStringByLength(32));
                //注解 添加别名 完成二次签名
                signInfo.setRepay_id("prepay_id=" + returnInfo.getPrepay_id());

                signInfo.setSignType("MD5");
                //生成签名
                String sign1 = Signature.getSign(signInfo);

                Map<String, String> payInfo = new HashMap<>();
                // 时间戳
                payInfo.put("timeStamp", signInfo.getTimeStamp());
                // 随机字符串
                payInfo.put("nonceStr", signInfo.getNonceStr());
                //预支付交易会话标识
                payInfo.put("package", signInfo.getRepay_id());

                payInfo.put("signType", signInfo.getSignType());

                payInfo.put("paySign", sign1);

                map.put("status", "ok");
                map.put("msg", "统一下单成功!");
                map.put("payInfo", payInfo);

                // 此处可以写唤起支付前的业务逻辑

                // 业务逻辑结束 回传给小程序端唤起支付
                return map;
            }
            map.put("status", 500);
            map.put("msg", "统一下单失败!");
            map.put("data", null);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("status", 500);
        map.put("msg", "统一下单失败!");
        map.put("data", null);
     
        return map;
    }


    /**
     * 向ami
     * 发起预支付
     */

   public  Map buyPower(String DBXH , //电表序号
                        String DBBH ,  //电表表号
                        String DCU ,   //集中器
                        String HH ,    // 户号
                        String JLDBM , //计量点编码
                        String orderNo , //订单号
                        String Amount , //充值金额
                        String Appid    // 小程序程序唯一id

                        ){

       String random= DesUtil.getDataforWeb(); //获得 获取16位加密随机数

       String randomKey = DesUtil.DESEncode("YuJian@2018",random); //生成加密密钥

       String strMeteringPointCode= DesUtil.DESEncode(randomKey,String.valueOf(JLDBM)); //加密计量点编码

       String powerAmount= DesUtil.DESEncode(randomKey,Amount);                      //添加购电记录的 金额

       String strAmount = DesUtil.DESEncode(randomKey,String.valueOf(Integer.parseInt(Amount)*100));               //生成充值token 加密后的金额

       Map<String,String>  map =  new HashMap<>();
        //生成充值 Token
       try {


           
         String   token  =   meterUtil.getRechargeToken(random,strMeteringPointCode,strAmount);
         //判断是否生成成功
         if (!token.isEmpty()){
              MeterPara meterPara = new MeterPara();

              meterPara.setTOKEN(token);

              
              logger.info("充值token生成成功-----"+ token);
             String paymode= DesUtil.DESEncode(randomKey,"1"); //固定为APP支付

             String tokenKey= DesUtil.DESEncode(randomKey,token);   // 加密后的token

                //主站系统 添加购电记录
              PowerRecord powerRecord = meterUtil.addPowerPurchaseRecord(random, strMeteringPointCode, paymode, powerAmount, tokenKey);
              if (powerRecord != null){
                   logger.info("购电记录添加成功!");
                  //获取购电时间
                  String GDSJ = powerRecord.getGDSJ();
                   logger.info("得到返回的购电时间"+GDSJ);
                  String strPurchaseTime = DesUtil.DESEncode(randomKey,GDSJ);
                  String strOrderNo = DesUtil.DESEncode(randomKey,orderNo);
                  String strAppID = DesUtil.DESEncode(randomKey,Appid);

                  //添加交易订单到主站系统



                  String start = meterUtil.addOrder(random, strOrderNo, strAppID, powerAmount, strMeteringPointCode, strPurchaseTime);
                   logger.info("添加交易订单到主站系统!"+start);
                   //判断是否添加成功 0 代表成功
                   if (StringUtils.equals(start,"0")){
//                        logger.info("添加交易订单到主站系统!");
                       int JZQBM = Integer.parseInt(JLDBM);
                    //开始电表操作
                       meterPara.setDBXH(DBXH);  //添加电表序号
                       meterPara.setDBBH(DBBH);  //电表表号
                       meterPara.setDCU(DCU);    // 集中器id
                       meterPara.setHH(HH);      // 户号
                       meterPara.setJZQBM(JZQBM);  //集中器编码
                       meterPara.setRechargeCode(4); //充值代码  3:直接充值  4:预充值  5:确认、取消、查询预充值
                        //登入主站
                       String csStr = "02" + meterPara.getHH() + "02";
                       String csOut = meterUtil.getCS(csStr);
                       String data = "660000000066" + csStr + csOut + "17";
                       SocketUtil su = new SocketUtil();
                       //预充值
                       meterPara.setRECHARGERESULT(4); //3:直接充值  4:预充值  5:确认、取消、查询预充值
                       Socket socket = su.socketConnect(data);
                       CmdOper co = new CmdOper();
                       //发起预充值
                       co.ReceiveEvent(socket, CmdType.METER_RECHARGE,meterPara);
//                       jsonStr= "{ \"result\":"+co.getRECHARGERESULT()+",\"err\":\""+co.getErr()+"\",\"msg\":\""+"成功"+"\"}";

                       map.put("result",String.valueOf(co.getRECHARGERESULT()));
                       map.put("err",co.getErr());
                       map.put("msg", "ok");
                        logger.info(String.valueOf(co.getRECHARGERESULT()));


                       return map;
                   }


              }



         }








       } catch (Exception e) {
           e.printStackTrace();
       }
       map.put("msg","err");
       return  map;
   }










}
