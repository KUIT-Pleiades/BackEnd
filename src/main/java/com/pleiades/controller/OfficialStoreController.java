package com.pleiades.controller;

import com.pleiades.annotations.*;
import com.pleiades.dto.store.*;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.store.OfficialStoreService;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Official Store", description = "공식몰")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store/official")
public class OfficialStoreController {
    private final OfficialStoreService officialStoreService;
    private final UserRepository userRepository;

    @Operation(summary = "얼굴 목록", description = "피부색/머리/눈/코/입/점 목록 불러오기")
    @OfficialStoreResponses
    @GetMapping("/face")
    public ResponseEntity<OfficialStoreDto> getFaceList(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);
        List<OfficialItemDto> dtos = officialStoreService.getOfficialItems(types);

        List<Long> wishIds = officialStoreService.getWishlistItems(types, user.getId());

        OfficialStoreDto dto = new OfficialStoreDto(dtos, wishIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(summary = "패션 목록", description = "상의/하의/세트/신발 목록 불러오기")
    @OfficialStoreResponses
    @GetMapping("/fashion")
    public ResponseEntity<OfficialStoreDto> getFashionList(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);
        List<OfficialItemDto> dtos = officialStoreService.getOfficialItems(types);

        List<Long> wishIds = officialStoreService.getWishlistItems(types, user.getId());

        OfficialStoreDto dto = new OfficialStoreDto(dtos, wishIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(summary = "배경 목록", description = "별/정거장 배경 목록 불러오기")
    @OfficialStoreResponses
    @GetMapping("/bg")
    public ResponseEntity<OfficialStoreDto> getBgList(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ItemType> types = List.of(ItemType.STAR_BG, ItemType.STATION_BG);
        List<OfficialItemDto> dtos = officialStoreService.getOfficialItems(types);

        List<Long> wishIds = officialStoreService.getWishlistItems(types, user.getId());

        OfficialStoreDto dto = new OfficialStoreDto(dtos, wishIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(summary = "찜 추가", description = "찜 추가하기")
    @AddWishlistResponses
    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> addWishlist(HttpServletRequest request, @RequestBody WishListDto wishlist) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ValidationStatus validationStatus = officialStoreService.addWishlist(user.getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Wishlist Already Existing"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Added"));
    }

    @Operation(summary = "찜 해제", description = "찜 해제하기")
    @RemoveWishlistResponses
    @DeleteMapping("/wishlist")
    public ResponseEntity<Map<String, String>> removeWishlist(HttpServletRequest request, @RequestBody WishListDto wishlist) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ValidationStatus validationStatus = officialStoreService.removeWishlist(user.getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Wishlist Not Found"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Removed"));
    }

    @Operation(summary = "아이템 구매", description = "아이템 구매하기")
    @ApiResponse(
            responseCode = "200 OK",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PurchaseResponseDto.class))
    )
    @UserNotFoundResponse
    @ItemNotFoundResponse
    @AlreadyHaveItemResponse
    @PostMapping("/trades")
    public ResponseEntity<PurchaseResponseDto> buyItem(HttpServletRequest request, @RequestBody ItemIdDto itemIdDto) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long ownershipId = officialStoreService.buyItem(user.getId(), itemIdDto.getItemId());
        return ResponseEntity.status(HttpStatus.OK).body(PurchaseResponseDto.successOf(ownershipId));
    }

    @Operation(summary = "공식몰 검색", description = "공식몰 아이템 검색")
    @GetMapping
    public ResponseEntity<OfficialStoreDto> searchOfficialStore(
            HttpServletRequest request,
            @RequestParam(required = false) String query
    ) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        OfficialStoreDto dto = officialStoreService.searchOfficialStore(query, user.getId());

        return ResponseEntity.ok(dto);
    }

}
