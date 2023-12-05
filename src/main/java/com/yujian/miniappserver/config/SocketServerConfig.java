package com.yujian.miniappserver.config;

/**
 * @author ze
 * @create 下午 02:19
 */
public class SocketServerConfig {

    /**
     * 昆山
     * 主站IP和WebService服务地址
     */
    private static String host_ks = "120.79.239.161";
    private static int port_ks = 6566;
    private static String WSDL_URI_ks = "http://120.79.239.161:8083/AMIWebService.asmx/";
    private static String namespace_ks = "http://yujian.com/AMIWebService/";

    public static String getIP() {
        return host_ks;
    }

    public static int getPort() {
        return port_ks;
    }
    public static String getWSDL_URI() {
        return WSDL_URI_ks;
    }
    public static String getNameSpace() {
        return namespace_ks;
    }


    
}
