package com.yujian.miniappserver.socket;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SysLog {
    private static boolean IsPrintLog = true;
    public static void println(String log){
        if(IsPrintLog){
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            String curDate = sdf.format(new Date());
            String x = "["+curDate+"]"+log;
            System.out.println(x);
        }
    }
}
