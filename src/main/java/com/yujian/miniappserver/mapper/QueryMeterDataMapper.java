package com.yujian.miniappserver.mapper;

import com.yujian.miniappserver.entity.QueryCheck;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author ze
 * @create 上午 09:35
 */
@Mapper
public interface QueryMeterDataMapper {


    //查询订单   对比金额

    QueryCheck queryCheck(String out_trade_no);

    //改变订单状态

    int upDataCheck(Map map);




    



}
