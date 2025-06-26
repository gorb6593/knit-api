package com.knit.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/test1")
    public String test() {
        return "test!!";
    }

    @GetMapping("/test2")
    public String test2() {
        return "한글!!";
    }

    @GetMapping("/test3")
    public String test3() {
        return "1테스트";
    }
}
