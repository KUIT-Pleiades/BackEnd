package com.pleiades.controller;

import com.pleiades.dto.SearchUserDto;
import com.pleiades.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "User", description = "사용자 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 검색", description = "유저 아이디로 사용자 검색하기")
    @GetMapping("")
    public ResponseEntity<Map<String, Object>>  searchUser(HttpServletRequest request, @RequestParam("user_id") String userId) {
        log.info("search user controller 진입: user_id = {}", userId);
        String email = (String) request.getAttribute("email");

        List<SearchUserDto> searchResult = userService.searchUser(userId, email);

        return ResponseEntity.ok(Map.of("users",searchResult));
    }

    @Operation(summary = "사용자 검색 기록", description = "사용자 검색 기록 불러오기")
    @GetMapping("/histories")
    public ResponseEntity<Map<String, Object>> searchUserHistory(HttpServletRequest request) {
        log.info("search user history controller 진입");
        String email = (String) request.getAttribute("email");

        List<SearchUserDto> searchResult = userService.searchUserHistory(email);

        return ResponseEntity.ok(Map.of("users", searchResult));
    }

    @Operation(summary = "사용자 검색 기록 추가", description = "사용자 검색 기록 추가하기")
    @PostMapping("/histories")
    public ResponseEntity<Map<String, Object>> addUserHistory(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        log.info("add user history controller 진입");
        String email = (String) request.getAttribute("email");

        String searchedId = requestBody.get("searchedId").toString();

        return userService.addUserHistory(email, searchedId);
    }

    @Operation(summary = "사용자 검색기록 삭제", description = "사용자 검색기록 삭제하기")
    @DeleteMapping("/histories/{searched_id}")
    public ResponseEntity<Map<String, Object>> deleteUserHistory(HttpServletRequest request, @PathVariable("searched_id") String searchedId) {
        log.info("delete user history controller 진입");
        String email = (String) request.getAttribute("email");

        return userService.deleteOldUserHistory(email, searchedId);
    }

    @Operation(summary = "사용자 검색기록 전체 삭제", description = "사용자 검색기록 모두 삭제하기")
    @DeleteMapping("/histories")
    public ResponseEntity<Map<String, Object>> deleteAllUserHistory(HttpServletRequest request) {
        log.info("delete all user history controller 진입");
        String email = (String) request.getAttribute("email");

        return userService.deleteAllUserHistory(email);
    }

    @Operation(summary = "", description = "")
    @GetMapping("/stone")
    public ResponseEntity<Map<String, Long>> getPrices(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Long stone = userService.getStone(email);

        return ResponseEntity.ok(Map.of("stone", stone));
    }

    @Operation(summary = "", description = "")
    @PostMapping("/stone")
    public ResponseEntity<Map<String, String>> addStone(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        userService.chargeStone(email);

        return ResponseEntity.ok(Map.of("message", "Added amount = 10"));
    }

    @GetMapping("/stone-charge")
    public ResponseEntity<Map<String, Boolean>> getStoneCharge(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        boolean isStoneCharged = userService.getStoneCharge(email);

        return ResponseEntity.ok(Map.of("isStoneCharged", isStoneCharged));
    }
}
