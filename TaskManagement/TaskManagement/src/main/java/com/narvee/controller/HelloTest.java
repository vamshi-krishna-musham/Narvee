package com.narvee.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;

@RestController
public class HelloTest {
    @PostConstruct
    public void init() {
        System.out.println(">>> HelloTest loaded by Spring <<<");
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
