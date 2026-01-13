package com.pleiades.controller;

import com.pleiades.dto.store.ItemBasicInfoDto;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Item", description = "아이템 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserRepository userRepository;

    @Operation(summary = "배경 불러오기", description = "정거장/별 배경 불러오기. type 쿼리 파라미터 이용 - station | star")
    @GetMapping("/backgrounds")
    public ResponseEntity<List<ItemBasicInfoDto>> getStationBackground(HttpServletRequest request, @RequestParam String type) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ItemBasicInfoDto> bgs = itemService.getBackgroundsByType(user, type);

        return ResponseEntity.status(HttpStatus.OK).body(bgs);
    }
}
