package com.yujian.miniappserver.config;


/**
 * @author YUJIAN
 */

public class WxpayConfig {

    //小程序ID
    public static String appid = "wxb1e6b761de29e9da";

    // 小程序的secret
    public static String secret = "cfd0b10e042ee9bd56f8343a64b684ac";

    //商户号
    public static String mch_id = "1648445500";

    // 商户支付秘钥
    public static String key = "cfd0b10e042ee9bd56f8343a64b684ac";

    // 回调通知地址
    public static String notify_url = "https://120.79.239.161:9898/weixin/callback";

    //交易类型
    public static  String trade_type = "JSAPI";

    //统一下单API接口链接
    public static String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //查询订单API接口链接
    public static String query_url = "https://api.mch.weixin.qq.com/pay/orderquery";






    }
