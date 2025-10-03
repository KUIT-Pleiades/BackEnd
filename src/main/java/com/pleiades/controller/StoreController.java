package com.pleiades.controller;

import com.pleiades.dto.store.ListingPriceDto;
import com.pleiades.dto.store.MyItemsResponseDto;
import com.pleiades.dto.store.ThemesDto;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.store.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store", description = "상점 공통 ")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final StoreService storeService;

    @GetMapping("/theme")
    public ResponseEntity<ThemesDto> getThemes() {
        return new ResponseEntity<>(storeService.getThemes(), HttpStatus.OK);
    }

    @GetMapping("/purchases")
    public ResponseEntity<MyItemsResponseDto> getPurchases(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MyItemsResponseDto myItemsResponseDto = new MyItemsResponseDto(storeService.getMyItems(user.getId()));

        return new ResponseEntity<>(myItemsResponseDto, HttpStatus.OK);
    }
}
