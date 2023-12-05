package com.yujian.miniappserver.config;


/**
 * @author YUJIAN
 */

public class WxpayConfig {

    //小程序ID
    public static String appid = "";

    // 小程序的secret
    public static String secret = "";

    //商户号
    public static String mch_id = "";

    // 商户支付秘钥
    public static String key = "";

    // 回调通知地址
    public static String notify_url = "";

    //交易类型
    public static  String trade_type = "JSAPI";

    //统一下单API接口链接
    public static String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //查询订单API接口链接
    public static String query_url = "https://api.mch.weixin.qq.com/pay/orderquery";






    }
