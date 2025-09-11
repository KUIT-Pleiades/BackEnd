package com.pleiades.controller;

import com.pleiades.dto.store.ResaleItemDto;
import com.pleiades.dto.store.ResaleStoreDto;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.store.ResaleStoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Resale Store", description = "중고몰")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store/resale")
public class ResaleStoreController {
    private final ResaleStoreService resaleStoreService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/face")
    public ResponseEntity<ResaleStoreDto> getFaceList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ResaleItemDto> dtos = resaleStoreService.getFaceItems();

        List<Long> wishIds = resaleStoreService.getFaceWishlistItems(user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResaleStoreDto(dtos, wishIds));
    }

    @GetMapping("/fashion")
    public ResponseEntity<ResaleStoreDto> getFashionList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ResaleItemDto> dtos = resaleStoreService.getFashionItems();

        List<Long> wishIds = resaleStoreService.getFashionWishlistItems(user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResaleStoreDto(dtos, wishIds));
    }

//    @GetMapping("/bg")
//    public String getBgList() {
//
//    }
}
