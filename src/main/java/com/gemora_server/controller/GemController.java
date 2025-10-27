package com.gemora_server.controller;

import com.gemora_server.service.AuthService;
import com.gemora_server.service.GemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gems")
@RequiredArgsConstructor
public class GemController {

    private final GemService gemService;
    private final AuthService  userService;



}
