package com.pleiades.controller;

import com.pleiades.entity.Star;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.User;
import com.pleiades.entity.character.Characters;
import com.pleiades.service.AuthService;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/home")
public class StarController {

    AuthService authService;

    @Autowired
    public StarController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
    }


    // 이러면 /auth랑 완전히 똑같지 않나?
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> home(@RequestHeader("Authorization") String authorization) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
//        return authService.responseUserInfo(accessToken);
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
    }

}
