package ru.acron.eurekaclient.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {
//    @GetMapping("/world")
//    public String hello(){
//        return "Hello World!";
//    }


    @Value("${eureka.instance.instance-id}")
    private String id;

    @Value("${server.port}")
    private String port;


    @GetMapping("/world")
    public String hello(){
        return id + " port: " + port;
    }

}

