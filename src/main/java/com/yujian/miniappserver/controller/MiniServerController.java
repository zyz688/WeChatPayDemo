package com.yujian.miniappserver.controller;

import com.yujian.miniappserver.config.WxpayConfig;
import com.yujian.miniappserver.entity.MeterPara;
import com.yujian.miniappserver.service.WxpayService;
import com.yujian.miniappserver.service.MeterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ze
 * @create 下午 03:54
 */
@RestController
public class MiniServerController {

    @Autowired
    public MeterOperation meterOperation;
    @Autowired
    WxpayService wxpayService;

    //查询电表实时余额
    @RequestMapping("/QueryBalance")
    public Map QueryBalance(@RequestParam("DBXH") String DBXH, //电表序号
                                @RequestParam(value = "DBBH",required = false) String DBBH,  //电表表号
                                @RequestParam(value = "DCU",required = false) String DCU,   //DCU
                                @RequestParam(value = "HH",required = false) String HH, //用户户号
                                @RequestParam(value = "motherMeterID",required = false) String MotherMeterID //母表id

                                ){
         MeterPara meterPara = new MeterPara();
         meterPara.setDBXH(DBXH);
         meterPara.setDBBH(DBBH);
         meterPara.setDCU(DCU);
         meterPara.setHH(HH);
         meterPara.setMotherMeterID(MotherMeterID);




        return     meterOperation.queryMeterBalance(meterPara);
    }

    //查询电表实时电量
    @RequestMapping("/CurrentEnergy")
    public Map queryCurrentEnergy(@RequestParam("DBXH") String DBXH, //电表序号
                                @RequestParam(value = "DBBH",required = false) String DBBH,  //电表表号
                                @RequestParam(value = "DCU",required = false) String DCU,   //DCU
                                @RequestParam(value = "HH",required = false) String HH //用户户号


                                ){
         MeterPara meterPara = new MeterPara();
         meterPara.setDBXH(DBXH);
         meterPara.setDBBH(DBBH);
         meterPara.setDCU(DCU);
         meterPara.setHH(HH);





        return     meterOperation.queryCurrentEnergy(meterPara);
    }

  //用户发起支付 预充值电表
  @RequestMapping("/buyPower")
  public Map buyPower(@RequestParam("DBXH") String DBXH,
                      @RequestParam("DBBH") String DBBH,
                      @RequestParam("DCU") String DCU,
                      @RequestParam("HH") String HH,
                      @RequestParam("JLDBM") String JLDBM,
                      @RequestParam("orderNo") String orderNo,
                      @RequestParam("Amount") String Amount


                       ){



      return     wxpayService.buyPower(DBXH,DBBH,DCU,HH,JLDBM,orderNo,Amount, WxpayConfig.appid);







  }

}
