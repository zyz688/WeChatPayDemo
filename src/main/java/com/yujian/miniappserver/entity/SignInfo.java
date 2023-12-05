package com.yujian.miniappserver.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * @author
 * @version 1.0.0
 * @ClassName SignInfo.java
 * @Description 签名实体类
 * @createTime 2022年04月09日 13:04:00
 */
@Data
public class SignInfo {

    private String appId;//小程序ID

    private String timeStamp;//时间戳

    private String nonceStr;//随机串

    @XStreamAlias("package")
    private String repay_id;

    private String signType;//签名方式

}