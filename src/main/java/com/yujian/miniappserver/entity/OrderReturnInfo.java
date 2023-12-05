package com.yujian.miniappserver.entity;

import lombok.Data;

/**
 * @author
 * @version 1.0.0
 * @ClassName OrderReturnInfo.java
 * @Description 订单返回实体类
 * @createTime 2022年04月09日 13:01:00
 */
@Data
public class OrderReturnInfo {

    private String return_code;

    private String return_msg;

    private String result_code;

    private String appid;

    private String mch_id;

    private String nonce_str;

    private String sign;

    private String prepay_id;

    private String trade_type;
}
