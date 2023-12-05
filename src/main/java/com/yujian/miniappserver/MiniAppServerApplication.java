package com.yujian.miniappserver;



import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class MiniAppServerApplication {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(MiniAppServerApplication.class);

        SpringApplication.run(MiniAppServerApplication.class, args);

        logger.info("spring 开始启动");



    }


    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createHTTPConnector());
        return tomcat;
    }

    private Connector createHTTPConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        //同时启用http（9899）、https（9898）两个端口
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(9899);
        connector.setRedirectPort(9898);
        return connector;
    }


}
