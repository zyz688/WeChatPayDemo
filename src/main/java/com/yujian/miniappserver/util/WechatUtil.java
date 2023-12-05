package com.yujian.miniappserver.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yujian.miniappserver.config.WxpayConfig;


import java.util.HashMap;
import java.util.Map;

public class WechatUtil {

    public static String getOpenId(String code) {
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> requestUrlParam = new HashMap<>();
        // https://mp.weixin.qq.com/wxopen/devprofile?action=get_profile&token=164113089&lang=zh_CN
        //        小程序appId
        requestUrlParam.put("appid",WxpayConfig.appid);
        //小程序secret
        requestUrlParam.put("secret",WxpayConfig.secret);
        //小程序端返回的code
        requestUrlParam.put("js_code", code);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");
        //发送post请求读取调用微信接口获取openid用户唯一标识



        return  JSONUtil.toJsonStr(HttpClientUtil.doPost(requestUrl, requestUrlParam));
    }
}
