package com.yujian.miniappserver.controller;


import cn.hutool.core.date.DateUtil;
import com.yujian.miniappserver.config.WxpayConfig;
import com.yujian.miniappserver.entity.QueryCheck;
import com.yujian.miniappserver.mapper.QueryMeterDataMapper;
import com.yujian.miniappserver.service.WxpayService;
import com.yujian.miniappserver.util.PayUtil;
import com.yujian.miniappserver.util.WechatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author ze
 * @create 上午 10:43
 */
@RestController
public class WxpayController {
    Logger logger = LoggerFactory.getLogger(WxpayController.class);
     @Autowired
     WxpayService wxpayService;
     @Autowired
     QueryMeterDataMapper queryMeterDataMapper;

    //获取用户的 openid
   @RequestMapping("/wx/login")
    public String login(@RequestParam("code") String code){

        logger.info("前端传过来的 code" + code);
       String openId = WechatUtil.getOpenId(code);


       logger.info(openId);
       return openId;


  }



    //预定订单
    @RequestMapping("/wx/payment")
    public Map payment(@RequestParam("body") String body,
                       @RequestParam("out_trade_no") String out_trade_no,
                       @RequestParam("total_fee") String total_fee,
                       @RequestParam("openid") String openid,
                       @RequestParam(value = "detail" ,required = false) String detail

                         ){


        return      wxpayService.payment(body,out_trade_no,total_fee,openid,detail);
    }



    /**
     * 微信小程序支付成功回调函数
     */
    @RequestMapping(value = "/weixin/callback")
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
         logger.info("开始回调............");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //sb为微信返回的xml
        String notityXml = sb.toString();
        String resXml = "";
         logger.info("接收到的报文：" + notityXml);

        Map map = PayUtil.doXMLParse(notityXml);

        //取出支付结果
        String returnCode = (String) map.get("result_code");
        if ("SUCCESS".equals(returnCode)) {
            //验证签名是否正确
             logger.info("验证签名是否正确");
            Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
            String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            String sign = PayUtil.sign(validStr, WxpayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名


            //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
            if (sign.equals(map.get("sign"))) {
                //查出订单      //进行金额 appid 对比



                 QueryCheck check = queryMeterDataMapper.queryCheck(map.get("out_trade_no").toString());
                    //微信给的金额 转成元
                   String  total_fee =   map.get("total_fee").toString();
                   if (total_fee.length()>=2){
                       total_fee = total_fee.substring(0,total_fee.length()-2);
                   }



                   String appid =  map.get("appid").toString();


                //判断支付订单是否存在
                if (check != null){
                    //支付 金额 appid 对比通过 进行下一步操作
                     logger.info("微信官方数据"  + "转换金额--" +total_fee + "-- Appid--" + appid);
                     logger.info("订单数据"  + "金额--" +check.getTOTAL_AMOUNT() + "-- Appid--" + check.getAPP_ID());
                    if (StringUtils.equals(check.getTOTAL_AMOUNT(),total_fee) && StringUtils.equals(check.getAPP_ID(),appid) ){
//                    if (true){
                         logger.info("进行金额 appid 对比通过 进行修改订单状态");
                        //后台用支付宝交易状态校验
                        String   trade_state =  map.get("result_code").toString();
                        if (StringUtils.equals("SUCCESS",trade_state)){
                            //修改为支付宝的 交易成功状态 看个人情况
                            //微信支付成功 修改数据库支付状态 用支付宝标识
                             logger.info("修改为支付宝的交易成功状态");
                            map.put("result_code","TRADE_SUCCESS");
                        }else {

                            map.put("result_code","TRADE_CLOSED");
                        }

                    //修改订单状态
                         int i = queryMeterDataMapper.upDataCheck(map);

                        if (i>0){
                             logger.info("修改订单支付状态成功");
                            //通知微信服务器已经支付成功
                             logger.info("通知微信服务器已经支付成功");
                            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

                        }

                    }


                }


            } else {
                 logger.info("微信支付回调失败!签名不一致");
            }
        } else {
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
         logger.info(resXml);
         logger.info("微信支付回调数据结束");

        BufferedOutputStream out = new BufferedOutputStream(
                response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }


 @RequestMapping("/test")

    public  String test(){

        return  DateUtil.now();

 }



}











