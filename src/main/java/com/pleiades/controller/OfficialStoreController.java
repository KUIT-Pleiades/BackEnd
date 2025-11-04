package com.pleiades.controller;

import com.pleiades.dto.store.*;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.store.OfficialStoreService;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
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
    private final AuthService authService;
    private final UserRepository userRepository;

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

    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> addWishlist(HttpServletRequest request, @RequestBody WishListDto wishlist) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ValidationStatus validationStatus = officialStoreService.addWishlist(user.getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Wishlist Already Existing"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Added"));
    }

    @DeleteMapping("/wishlist")
    public ResponseEntity<Map<String, String>> removeWishlist(HttpServletRequest request, @RequestBody WishListDto wishlist) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ValidationStatus validationStatus = officialStoreService.removeWishlist(user.getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Wishlist Not Found"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Removed"));
    }

    @PostMapping("/trades")
    public ResponseEntity<PurchaseResponseDto> buyItem(HttpServletRequest request, @RequestBody ItemIdDto itemIdDto) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long ownershipId = officialStoreService.buyItem(user.getId(), itemIdDto.getItemId());
        return ResponseEntity.status(HttpStatus.OK).body(PurchaseResponseDto.successOf(ownershipId));
    }
}
