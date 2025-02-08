package com.pleiades.controller;

import com.pleiades.dto.SearchUserDto;
import com.pleiades.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<SearchUserDto>> searchUser(HttpServletRequest request, @RequestParam("user_id") String userId) {
        log.info("search user controller 진입: user_id = {}", userId);

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);
        List<SearchUserDto> searchResult = userService.searchUser(userId, email);

        return ResponseEntity.ok(searchResult);
    }
}
