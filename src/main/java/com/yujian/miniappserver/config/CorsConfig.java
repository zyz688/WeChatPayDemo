package com.yujian.miniappserver.config;

import com.yujian.miniappserver.interceptor.TokenCheck;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ze
 * @create 上午 09:38
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {


    //跨域处理 另一种方式
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("*")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600);
    }











    //注册登入检查拦截器

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new TokenCheck())
                .addPathPatterns("/**")               //添加拦截路径
                .excludePathPatterns( //放行路径
                        "/weixin/callback", //微信回调放开
                        "/test",
                        "/app",
                        "/favicon.ico"
                );


    }






}
