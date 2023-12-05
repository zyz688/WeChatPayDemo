package com.yujian.miniappserver.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ze
 * @create 2022-06-29-10:51
 */


public class TokenCheck implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(TokenCheck.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String token = request.getParameter("token");
        String requestURI = request.getRequestURI();
        String remoteAddr=request.getRemoteAddr();
        System.out.println("前端token" + token);

        //请求带以下参数 可以放行   token:-- DAC0C6AD932F2FF2D4A4A8C5928C335D 随机数：--CC080AB103747737
            if (StringUtils.equals("DAC0C6AD932F2FF2D4A4A8C5928C335D",token)){


            logger.info("验证通过放行路径——>"+requestURI);
                logger.info("验证通过放,对方IP——>"+remoteAddr);


                return  true;


            }else {

                logger.info("验证不通过,验证失败路径——>"+requestURI);
                logger.info("验证不通过,验证失败对方IP——>"+remoteAddr);
                return false;
            }



    }
}