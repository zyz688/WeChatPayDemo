package com.yujian.miniappserver.util;

/**
 * 通用返回对象
 *
 *
 *   @author ze
 *   @create 2022-06-21-20:57
 *
 */
public class R<T> {
    private long code;


    private T data;

 
    protected R() {
    }
 
    protected R(long code, T data) {

        this.code = code;

        this.data = data;
    }





    //成功返回 信息 数据
    public static <T> R<T> success(T data) {
        return new R<T>(ResultCode.SUCCESS.getCode(),data);
    }





    public long getCode() {
        return code;
    }
 
    public void setCode(long code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }
 
    public void setData(T data) {
        this.data = data;
    }
}