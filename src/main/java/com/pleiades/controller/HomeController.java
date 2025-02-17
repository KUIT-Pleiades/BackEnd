package com.pleiades.controller;

import com.pleiades.dto.CharacterDto;
import com.pleiades.dto.StarBackgroundDto;
import com.pleiades.entity.User;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.service.UserService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/home")
public class HomeController {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    AuthService authService;
    UserService userService;
    JwtUtil jwtUtil;

    @Autowired
    public HomeController(AuthService authService, UserService userService, JwtUtil jwtUtil, UserRepository userRepository, FriendRepository friendRepository) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> home(@RequestHeader("Authorization") String authorization) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        return authService.responseUserInfo(accessToken);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> friendHome(HttpServletRequest request, @PathVariable("userId") String userId) {
        // 친구 아이디 존재 여부
        if (userId == null) { log.info("no userId"); return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(Map.of("message", "no userId")); }

        // 친구 존재 여부
        Optional<User> friend = userRepository.findById(userId);
        if (friend.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "user not found")); }

        // 사용자 존재 여부
        String email = (String) request.getAttribute("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "need sign-up")); }

        return authService.responseFriendInfo(user.get(), friend.get());
    }

    @PostMapping("/settings/character")
    public ResponseEntity<Map<String, Object>> characterSetting(HttpServletRequest request, @RequestBody @Validated CharacterDto characterDto) {
        String email = (String) request.getAttribute("email");

        ValidationStatus setCharacter = userService.setCharacter(email, characterDto);

        // user 없음
        if (setCharacter == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        // character 없음
        if (setCharacter == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no character")); }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/settings/background")
    public ResponseEntity<Map<String, Object>> backgroundSetting(HttpServletRequest request, @RequestBody StarBackgroundDto starBackgroundDto) {
        String email = (String) request.getAttribute("email");

        String backgroundName = starBackgroundDto.getBackgroundName();
        log.info("backgroundName: " + backgroundName);

        ValidationStatus setBackground = userService.setBackground(email, backgroundName);

        // user 없음
        if (setBackground == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "user not found")); }

        // star 없음
        if (setBackground == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "star not found")); }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
