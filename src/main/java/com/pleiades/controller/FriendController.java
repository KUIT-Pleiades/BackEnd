package com.pleiades.controller;

import com.pleiades.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/friends")
public class FriendController {

    UserRepository userRepository;

    @Autowired
    FriendController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/requests")
    public ResponseEntity<Map<String, Object>> friendRequest() {
        log.info("/friends/requests");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
