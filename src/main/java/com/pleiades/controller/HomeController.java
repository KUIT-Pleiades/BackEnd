package com.pleiades.controller;

import com.pleiades.service.AuthService;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/home")
public class HomeController {

    AuthService authService;

    @Autowired
    public HomeController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
    }


    @GetMapping("")
    public ResponseEntity<Map<String, Object>> home(@RequestHeader("Authorization") String authorization) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        return authService.responseUserInfo(accessToken);
    }

}
