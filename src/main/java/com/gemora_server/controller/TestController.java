package com.gemora_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController
    public class TestController {
        @GetMapping("/public/test")
        public String test() {
            return "public ok";
        }
    }




