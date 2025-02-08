package com.pleiades.controller;

import com.pleiades.dto.CharacterDto;
import com.pleiades.entity.Friend;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
    public ResponseEntity<Map<String, Object>> friendHome(@RequestHeader("Authorization") String authorization, @PathVariable("userId") String userId) {
        // 친구 아이디 존재 여부
        Optional<User> friend = userRepository.findById(userId);
        if (friend.isEmpty()) { return ResponseEntity.notFound().build(); }

        // 친구 관계에 있는지 검증
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        boolean relationship = friendRepository.isFriend(user.get(), friend.get(), FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        return authService.responseUserInfo(userId);
    }

    // todo: dto 유효성 검사 필요
    @PostMapping("/settings/character")
    public ResponseEntity<Map<String, Object>> characterSetting(@RequestHeader("Authorization") String authorization, @RequestBody @Validated CharacterDto characterDto) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        ValidationStatus setCharacter = userService.setCharacter(email, characterDto);

        // user 없음
        if (setCharacter == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        // character 없음
        if (setCharacter == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/settings/background")
    public ResponseEntity<Map<String, Object>> backgroundSetting(@RequestHeader("Authorization") String authorization, @RequestBody String backgroundName) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        ValidationStatus setBackground = userService.setBackground(email, backgroundName);

        // user 없음
        if (setBackground == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        // star 없음
        if (setBackground == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
