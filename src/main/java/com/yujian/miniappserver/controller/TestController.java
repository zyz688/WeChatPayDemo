package com.yujian.miniappserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title: TestController
 * @Author ze
 * @Package com.yujian.miniappserver.controller
 * @Date 2023/9/7 上午 10:25
 * @description:
 */
@Controller
public class TestController {



    @RequestMapping("/app")
    public  String privateHTML(){



        return  "app";
    }
}
