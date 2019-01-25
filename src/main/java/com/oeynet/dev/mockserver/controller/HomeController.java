package com.oeynet.dev.mockserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/home")
@RestController
public class HomeController {

    @RequestMapping(method = RequestMethod.GET, value = "/home")
    public String home() {
        return "home22-hello";
    }


    @RequestMapping(value = "/json")
    public Object json() {
        return new Object() {
            String demo = "hello";
        };
    }
}
