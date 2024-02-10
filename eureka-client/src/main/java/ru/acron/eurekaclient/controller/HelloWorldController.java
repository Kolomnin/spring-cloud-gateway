package ru.acron.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {
//    @GetMapping("/world")
//    public String hello(){
//        return "Hello World!";
//    }

    @Value("${eureka.instance.instance-id}")
    private String id;

    @GetMapping("/world")
    public String hello(){
        return id;
    }

}

