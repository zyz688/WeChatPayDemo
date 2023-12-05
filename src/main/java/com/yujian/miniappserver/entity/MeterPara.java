package com.yujian.miniappserver.entity;

import com.yujian.miniappserver.util.MeterUtil;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;


public class MeterPara {
    private String nowBalance = "";            //当前余额
    private String HH;
    ;                      //户号
    private String DBXH = "";                  //电表序号
    private String DBBH = "";                  //电表表号
    private String DCU = "";                   //集中器ID
    private int JZQBM = 0;                     //集中器编码
    private String TOKEN;
    private int rechargeCode;


    private String motherMeterID;            //母表表号
    private String error;                    //错误码
    private int RECHARGERESULT;              //预充值、确认充值结果

    MeterUtil mu = new MeterUtil();

    public String getNowBalance() {
        return nowBalance;
    }

    public void setNowBalance(String nowBalance) {
        this.nowBalance = nowBalance;
    }

    public String getHH() {
        return HH;
    }

    public void setHH(String HH) {

        //高低位互换
        if (StringUtils.isNotEmpty(HH))   HH = mu.low2high(HH);
        this.HH = HH;
    }

    @Override
    public String toString() {
        return "MeterPara{" +
                "nowBalance='" + nowBalance + '\'' +
                ", HH='" + HH + '\'' +
                ", DBXH='" + DBXH + '\'' +
                ", DBBH='" + DBBH + '\'' +
                ", DCU='" + DCU + '\'' +
                ", JZQBM=" + JZQBM +
                ", TOKEN='" + TOKEN + '\'' +
                ", rechargeCode=" + rechargeCode +
                ", motherMeterID='" + motherMeterID + '\'' +
                ", error='" + error + '\'' +
                ", RECHARGERESULT=" + RECHARGERESULT +
                ", mu=" + mu +
                '}';
    }

    public String getDBXH() {
        return DBXH;
    }

    public void setDBXH(String DBXH) {

        DBXH = Integer.toHexString(Integer.valueOf(DBXH));
        //两个字节不够补零
        DBXH = "0000" + DBXH;
        // 两个字节
        DBXH = DBXH.substring(DBXH.length()-4);

        //高低位互换
        DBXH = mu.low2high(DBXH);

        this.DBXH = DBXH;




    }

    public String getDBBH() {
        return DBBH;
    }

    public void setDBBH(String DBBH) {

        DBBH = mu.low2high(DBBH);

        this.DBBH = DBBH;
    }

    public String getDCU() {
        return DCU;
    }

    public void setDCU(String DCU) {

        if (StringUtils.isNotEmpty(DCU))  DCU = DCU.substring(0, 4) + mu.low2high(DCU.substring(4, 8));

        this.DCU = DCU;


    }

    public int getJZQBM() {
        return JZQBM;
    }

    public void setJZQBM(int JZQBM) {
        this.JZQBM = JZQBM;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getRechargeCode() {
        return rechargeCode;
    }

    public void setRechargeCode(int rechargeCode) {
        this.rechargeCode = rechargeCode;
    }

    public int getRECHARGERESULT() {
        return RECHARGERESULT;
    }

    public void setRECHARGERESULT(int RECHARGERESULT) {
        this.RECHARGERESULT = RECHARGERESULT;
    }

    public String getMotherMeterID() {
        return motherMeterID;
    }

    public void setMotherMeterID(String motherMeterID) {

        if (StringUtils.isNotEmpty(motherMeterID)) motherMeterID = mu.low2high(motherMeterID);



        this.motherMeterID = motherMeterID;
    }
}