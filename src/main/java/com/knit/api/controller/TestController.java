package com.knit.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/1")
    public String test() {
        return "test!!";
    }

    @GetMapping("/2")
    public String test2() {
        return "한글!!";
    }

    @GetMapping("/3")
    public String test3() {
        return "테스트3";
    }
}
