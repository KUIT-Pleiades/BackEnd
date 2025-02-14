package com.pleiades.controller;

import com.pleiades.dto.SearchUserDto;
import com.pleiades.repository.StationRepository;
import com.pleiades.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<SearchUserDto>> searchUser(HttpServletRequest request) {
        log.info("station controller 진입");

        String email = (String) request.getAttribute("email");
        log.info("사용자 email = {}", email);

        return ResponseEntity.ok().build();
    }
}
