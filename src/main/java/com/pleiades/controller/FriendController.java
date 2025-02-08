package com.pleiades.controller;

import com.pleiades.dto.friend.FriendDto;
import com.pleiades.dto.friend.FriendListDto;
import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.service.FriendService;
import com.pleiades.strings.FriendStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/requests")
    public ResponseEntity<Map<String, Object>> friendRequest(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        log.info("friend-request controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        String receiverId = requestBody.get("receiverId").toString();

        return friendService.sendFriendRequest(email, receiverId);
    }

    @GetMapping("")
    public ResponseEntity<FriendListDto> getFriendList(HttpServletRequest request) {
        log.info("friend List 출력 Controller 진입");

        String email = (String) request.getAttribute("email");
        if(email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("사용자 email = {}", email);

        List<FriendDto> receivedDtos = friendService.getReceivedFriendRequests(email);
        List<FriendDto> friendDtos = friendService.getFriends(email);
        List<FriendDto> sentDtos = friendService.getSentFriendRequests(email);

        FriendListDto response = new FriendListDto(receivedDtos, friendDtos, sentDtos);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/requests/{friend_id}")
    public ResponseEntity<Map<String, String>> handleFriendRequest(HttpServletRequest request, @RequestBody Map<String, Object> requestBody, @PathVariable("friend_id") Long friend_id) {
        log.info("handle request controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        FriendStatus status = FriendStatus.valueOf(requestBody.get("status").toString().toUpperCase());

        return friendService.updateFriendStatus(email, friend_id, status);
    }

    @DeleteMapping("/requests/{friend_id}")
    public ResponseEntity<Map<String, String>> deleteFriend(HttpServletRequest request, @PathVariable("friend_id") Long friend_id) {
        log.info("handle request controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        return friendService.deleteFriend(email, friend_id);
    }
}
