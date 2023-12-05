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


  










}
