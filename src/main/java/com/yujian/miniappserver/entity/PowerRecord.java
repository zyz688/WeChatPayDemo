package com.yujian.miniappserver.entity;

import lombok.Data;



/**
用于添加购电记录后 回复数据的 实体类

 */

@Data
public class PowerRecord {
    public int JLDBM;//计量点编码
    public String GDSJ;//购电时间

}
