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
import java.util.Map;

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

    @GetMapping("/histories")
    public ResponseEntity<List<SearchUserDto>> searchUserHistory(HttpServletRequest request) {
        log.info("search user history controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        List<SearchUserDto> searchResult = userService.searchUserHistory(email);

        return ResponseEntity.ok(searchResult);
    }

    @PostMapping("/histories")
    public ResponseEntity<Map<String, Object>> addUserHistory(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        log.info("add user history controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        String searchedId = requestBody.get("searchedId").toString();

        return userService.addUserHistory(email, searchedId);
    }

    @DeleteMapping("/histories/{searched_id}")
    public ResponseEntity<Map<String, Object>> deleteUserHistory(HttpServletRequest request, @PathVariable("searched_id") String searchedId) {
        log.info("delete user history controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        return userService.deleteOldUserHistory(email, searchedId);
    }
}

