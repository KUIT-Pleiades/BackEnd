package com.pleiades.controller;

import com.pleiades.dto.*;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.service.UserService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
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

    AuthService authService;
    UserService userService;
    JwtUtil jwtUtil;

    @Autowired
    public HomeController(AuthService authService, UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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
        log.info("사용자 email = {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "need sign-up")); }

        return authService.responseFriendInfo(user.get(), friend.get());
    }

    @PostMapping("/settings/character")
    public ResponseEntity<Map<String, Object>> characterSetting(HttpServletRequest request, @RequestBody UserInfoDto userInfoDto) {
        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        CharacterDto characterDto = userService.userInfoDto2CharacterDto(userInfoDto);

        ValidationStatus setCharacter = userService.setCharacter(email, characterDto);
        ValidationStatus setBackground = userService.setBackground(email, userInfoDto.getBackgroundName());

        // user 없음
        if (setCharacter == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        // character 없음
        if (setCharacter == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no character")); }

        // star 없음
        if (setBackground == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "star not found")); }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/settings/profile")
    public ResponseEntity<ProfileDto> getProfile(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }

        ProfileDto profileDto = userService.getProfile(user.get());

        return ResponseEntity.status(HttpStatus.OK).body(profileDto);
    }

    @PostMapping("/settings/profile")
    public ResponseEntity<Map<String, String>> profileSetting(HttpServletRequest request, @RequestBody ProfileSettingDto profileSettingDto) {
        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        Map<String, String> response = userService.setProfile(email, profileSettingDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
