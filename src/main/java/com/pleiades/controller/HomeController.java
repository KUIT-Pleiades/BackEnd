package com.pleiades.controller;

import com.pleiades.dto.*;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.UserService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static io.jsonwebtoken.Jwts.header;

@Tag(name = "Home", description = "내 별 관련 API")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController {
    private final UserRepository userRepository;

    private final AuthService authService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Operation(summary = "내 별", description = "내 별")
    @GetMapping("")
    public ResponseEntity<UserInfoDto> home(HttpServletRequest request) {
        String email = request.getAttribute("email").toString();

        ValidationStatus userValidation = authService.userValidation(email);
        if (userValidation.equals(ValidationStatus.NOT_VALID)) throw new CustomException(ErrorCode.SIGN_UP_REQUIRED);     // 202

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        UserInfoDto userInfoDto = userService.buildUserInfoDto(user.get());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userInfoDto);
    }

    @Operation(summary = "친구 별", description = "친구 별")
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

    @Operation(summary = "캐릭터 설정", description = "캐릭터 옷 입히기")
    @PostMapping("/settings/character")
    public ResponseEntity<Map<String, Object>> characterSetting(HttpServletRequest request, @Valid @RequestBody UserInfoDto userInfoDto) {
        String email = (String) request.getAttribute("email");

        CharacterDto characterDto = modelMapper.map(userInfoDto, CharacterDto.class);

        ValidationStatus setCharacter = userService.setCharacter(email, characterDto);
        ValidationStatus setBackground = userService.setBackground(email, userInfoDto.getBackgroundName());

        // user 없음
        if (setCharacter == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        // character 없음
        if (setCharacter == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no character")); }

        // star 없음
        if (setBackground == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "star not found")); }

        return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json").body(Map.of("message", "character set"));
    }

    @Operation(summary = "설정 보기", description = "프로필 설정 불러오기")
    @GetMapping("/settings/profile")
    public ResponseEntity<ProfileDto> getProfile(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }

        ProfileDto profileDto = modelMapper.map(user.get(), ProfileDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(profileDto);
    }

    @Operation(summary = "설정 변경", description = "프로필 설정 변경하기")
    @PostMapping("/settings/profile")
    public ResponseEntity<Map<String, String>> profileSetting(HttpServletRequest request, @Valid @RequestBody ProfileSettingDto profileSettingDto) {
        String email = (String) request.getAttribute("email");

        Map<String, String> response = userService.setProfile(email, profileSettingDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
