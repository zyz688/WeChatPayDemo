package com.yujian.miniappserver;

import com.yujian.miniappserver.entity.MeterPara;
import com.yujian.miniappserver.entity.OrderInfo;
import com.yujian.miniappserver.mapper.QueryMeterDataMapper;
import com.yujian.miniappserver.service.WxpayService;
import com.yujian.miniappserver.socket.CmdOper;
import com.yujian.miniappserver.socket.CmdType;
import com.yujian.miniappserver.socket.SocketUtil;
import com.yujian.miniappserver.util.DesUtil;
import com.yujian.miniappserver.util.MeterUtil;
import com.yujian.miniappserver.util.Signature;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Socket;

@SpringBootTest
class MiniAppServerApplicationTests {

    @Autowired
    WxpayService wxpayService;
    @Autowired
    QueryMeterDataMapper queryMeterDataMapper;
    @Autowired
    public  MeterUtil mu ;
    @Test

    void contextLoads() throws Exception {
        String random= DesUtil.getDataforWeb();

        String randomKey = DesUtil.DESEncode("YuJian@2018",random);
        String strMeteringPointCode= DesUtil.DESEncode(randomKey,String.valueOf("807"));

        String rechageMonkey1 = String.valueOf(1000); //参数需要扩大 100倍


        String strAmount = DesUtil.DESEncode(randomKey,rechageMonkey1);

         MeterUtil meterUtil = new MeterUtil();


        System.out.println("得到token---"+meterUtil.getRechargeToken(random, strMeteringPointCode, strAmount));


    }

    @Test
    void  strTest()throws Exception{




        //添加支付宝 微信小程序 订单等 回复操作 成功的字符串
        String str = "<string xmlns=\"http://yujian.com/AMIWebService/\">9</string>";

        //存储数据集


        Document document = DocumentHelper.parseText(str);
         Element rootElement = document.getRootElement();



        System.out.println(  rootElement.getStringValue());


//        //匹配 0 为操作成功 且出现次数为一次
//        System.out.println(StringUtils.countMatches(str,"0") == 1);

//        wxpayService.buyPower(null,null,null,null,"807",null,"100",null);

    }

@Test
  void  queryCheck(){
//String total_fee ="1000";
//    total_fee = total_fee.substring(0,total_fee.length()-2);
    System.out.println(queryMeterDataMapper.queryCheck("20230717143936815991") );
//    System.out.println(total_fee);

}



@Test
 void  sg() throws Exception{

     OrderInfo orderInfo = new OrderInfo();

                orderInfo.setAppid("wxb1e6b761de29e9da");
                orderInfo.setBody("预见科技微信小程序充值");
                orderInfo.setMch_id("1648445500");
                orderInfo.setDetail("{&quot;goods_detail&quot;:[{&quot;goods_id&quot;:&quot;20230719114330185651&quot;,&quot;goods_name&quot;:&quot;预见科技微信小程序:充值账户：13688888888电表表号：221012600001区域编号：99999&quot;,&quot;quantity&quot;:1,&quot;price&quot;:&quot;3000&quot;}]}");
                orderInfo.setNonce_str("1eevah7rsny215yrsigx7dg1wu73xo91");
                orderInfo.setOpenid("oNlTG5SowS9749hlRPb6hBMNZdcE");
                orderInfo.setOut_trade_no("20230719114330185651");
                orderInfo.setSpbill_create_ip("127.0.0.1");
                orderInfo.setTotal_fee("1");
                orderInfo.setTrade_type("JSAPI");
                orderInfo.setSign_type("MD5");
                orderInfo.setNotify_url("http://120.79.239.161:9898/weixin/callback");




    System.out.println(Signature.getSign(orderInfo));


}


@Test
    void  eqstr(){






    String random= DesUtil.getDataforWeb(); //获得 获取16位加密随机数

    String randomKey = DesUtil.DESEncode("YuJian@2018",random); //生成加密密钥

    String token= DesUtil.DESEncode(randomKey,"yujianMninApp"); //开始加密
    System.out.println("token:--" + token + "随机数：--" + random);

//    token:--DAC0C6AD932F2FF2D4A4A8C5928C335D随机数：--CC080AB103747737







}




@Test
   void  queryCurrentEnergy(){
    MeterPara meterPara = new MeterPara();
     //装入数据
    meterPara.setDBBH("221111000014");
    meterPara.setDCU("11000014");
    meterPara.setHH("100100000058");
    meterPara.setDBXH("23");
    System.out.println(meterPara);


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
    System.out.println("电量为 ——————"+co.getEnergy());



}

@Test
    void  test2(){
    String random = DesUtil.getData();
    System.out.println(random);



}
@Test
    void  test3(){
    String random = DesUtil.getData();
    System.out.println(random);



}



}
