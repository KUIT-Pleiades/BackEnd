package com.pleiades.controller;

import com.pleiades.annotations.UserNotFoundResponse;
import com.pleiades.dto.store.*;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.store.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Store", description = "상점 공통 ")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {
    private final UserRepository userRepository;
    private final StoreService storeService;

    @Operation(summary = "테마 목록", description = "테마 목록 불러오기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = OfficialAndRestoreThemesDto.class))
    )
    @GetMapping("/theme")
    public ResponseEntity<OfficialAndRestoreThemesDto> getThemes() {
        return new ResponseEntity<>(storeService.getThemes(), HttpStatus.OK);
    }

    @Operation(summary = "판매 가능한 내 아이템", description = "내가 구매한 아이템 중 매물로 올리지 않은 아이템들 불러오기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = MyItemsDto.class))
    )
    @UserNotFoundResponse
    @GetMapping("/sellable")
    public ResponseEntity<MyItemsDto> getSellableItems(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new ResponseEntity<>(storeService.getMySellableItems(user), HttpStatus.OK);
    }

    @Operation(summary = "구매한 아이템", description = "내가 구매한 아이템 불러오기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PurchasesCountResponseDto.class))
    )
    @UserNotFoundResponse
    @GetMapping("/purchases")
    public ResponseEntity<PurchasesCountResponseDto> getPurchases(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new ResponseEntity<>(storeService.getMyItemsByDate(user.getId()), HttpStatus.OK);
    }

    @Operation(summary = "판매한 아이템", description = "내가 판매한 아이템 불러오기")
    @ApiResponse(
            responseCode = "200",
            description = "성공: 판매된 내 매물 반환",
            content = @Content(schema = @Schema(implementation = SalesCountResponseDto.class))
    )
    @UserNotFoundResponse
    @GetMapping("/sales")
    public ResponseEntity<SalesCountResponseDto> getSales(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SalesCountResponseDto purchasesResponseDto = storeService.getSoldItems(user);

        return new ResponseEntity<>(purchasesResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "착용 가능한 아이템", description = "기본 아이템과 내가 구매한 아이템 중 매물로 올리지 않은 아이템들")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = CharacterWearableItemsDto.class))
    )
    @UserNotFoundResponse
    @GetMapping("/wearable")
    public ResponseEntity<CharacterWearableItemsDto> getWearableItems(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new ResponseEntity<>(storeService.getWearableItems(user), HttpStatus.OK);
    }
}
