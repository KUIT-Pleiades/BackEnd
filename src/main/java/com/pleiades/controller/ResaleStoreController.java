package com.pleiades.controller;

import com.pleiades.annotations.*;
import com.pleiades.dto.store.*;
import com.pleiades.entity.User;
import com.pleiades.entity.store.Ownership;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.store.ResaleStoreService;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Resale Store", description = "중고몰")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store/resale")
public class ResaleStoreController {
    private final ResaleStoreService resaleStoreService;
    private final UserRepository userRepository;

    @Operation(summary = "얼굴 목록", description = "피부색/머리/눈/코/입/점 목록 불러오기")
    @ResaleStoreResponses
    @GetMapping("/face")
    public ResponseEntity<ResaleStoreDto> getFaceList(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);
        List<ResaleItemDto> dtos = resaleStoreService.getItems(types);

        List<Long> wishIds = resaleStoreService.getWishlistItems(types, user.get().getId());

        ResaleStoreDto dto = new ResaleStoreDto(dtos, wishIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(summary = "패션 목록", description = "상의/하의/세트/신발 목록 불러오기")
    @ResaleStoreResponses
    @GetMapping("/fashion")
    public ResponseEntity<ResaleStoreDto> getFashionList(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);
        List<ResaleItemDto> dtos = resaleStoreService.getItems(types);

        List<Long> wishIds = resaleStoreService.getWishlistItems(types, user.get().getId());

        ResaleStoreDto dto = new ResaleStoreDto(dtos, wishIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(summary = "배경 목록", description = "별/정거장 배경 목록 불러오기")
    @ResaleStoreResponses
    @GetMapping("/bg")
    public ResponseEntity<ResaleStoreDto> getBgList(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.STAR_BG, ItemType.STATION_BG);
        List<ResaleItemDto> dtos = resaleStoreService.getItems(types);

        List<Long> wishIds = resaleStoreService.getWishlistItems(types, user.get().getId());

        ResaleStoreDto dto = new ResaleStoreDto(dtos, wishIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(summary = "찜 추가", description = "찜 추가하기")
    @AddWishlistResponses
    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> addWishlist(HttpServletRequest request, @RequestBody WishListDto wishlist) {
        String email = (String) request.getAttribute("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        ValidationStatus validationStatus = resaleStoreService.addWishlist(user.get().getId(), wishlist.getId());

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

        ValidationStatus validationStatus = resaleStoreService.removeWishlist(user.getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Wishlist Not Found"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Removed"));
    }

    @Operation(summary = "판매 시세 확인", description = "팔려고 하는 아이템의 시세 확인하기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = ListingPriceDto.class))
    )
    @ItemNotFoundResponse
    @GetMapping("/items/{item_id}/price")
    public ResponseEntity<List<ListingPriceDto>> getListingsPrice(@PathVariable("item_id") Long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(resaleStoreService.getListingsPrice(itemId));
    }

    @Operation(summary = "아이템 구매", description = "중고 아이템 구매하기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PurchaseResponseDto.class))
    )
    @UserNotFoundResponse
    @ItemNotFoundResponse
    @AlreadyHaveItemResponse
    @NotOnSaleResponse
    @PostMapping("/trades")
    public ResponseEntity<PurchaseResponseDto> buyItem(HttpServletRequest request, @RequestBody ListingIdDto listingIdDto) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long ownershipId = resaleStoreService.buyItem(user.getId(), listingIdDto.getListingId());
        return ResponseEntity.status(HttpStatus.OK).body(PurchaseResponseDto.successOf(ownershipId));
    }

    @Operation(summary = "매물 등록", description = "내 아이템을 매물로 등록하기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PurchaseResponseDto.class))
    )
    @NotMyItemResponse
    @UserNotFoundResponse
    @ItemNotFoundResponse
    @AlreadyListedItemResponse
    @PostMapping("/listings")
    public ResponseEntity<ListingIdDto> addListing(HttpServletRequest request, @RequestBody AddListingRequestDto addListingRequestDto) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long listingId = resaleStoreService.addListing(user.getId(), addListingRequestDto.getOwnershipId(), addListingRequestDto.getPrice());

        ListingIdDto listingIdDto = new ListingIdDto(listingId);

        return ResponseEntity.status(HttpStatus.OK).body(listingIdDto);
    }

    @Operation(summary = "매물 정보 수정", description = "내 아이템을 매물로 등록하기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PurchaseResponseDto.class))
    )
    @NotMyItemResponse
    @UserNotFoundResponse
    @ItemNotFoundResponse
    @AlreadyListedItemResponse
    @PatchMapping("/listings")
    public ResponseEntity<ListingIdDto> updateListing(HttpServletRequest request, @RequestBody AddListingRequestDto addListingRequestDto) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long listingId = resaleStoreService.updateListing(user.getId(), addListingRequestDto.getOwnershipId(), addListingRequestDto.getPrice());

        ListingIdDto listingIdDto = new ListingIdDto(listingId);

        return ResponseEntity.status(HttpStatus.OK).body(listingIdDto);
    }

    @Operation(summary = "매물 내리기", description = "내가 올린 매물 내리기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                            {
                                "message": "Listing Deleted Successfully"
                            }
                            """
                    )
            )
    )
    @UserNotFoundResponse
    @NotMyListingResponse
    @ItemNotFoundResponse
    @NotOnSaleResponse
    @DeleteMapping("/listings/{listingId}")
    public ResponseEntity<Map<String,String>> deleteListing(HttpServletRequest request, @PathVariable Long listingId) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        resaleStoreService.deleteListing(user.getId(), listingId);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Listing Deleted Successfully"));
    }

    @Operation(summary = "내 매물", description = "내가 올린 판매 중 상태인 매물 보기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = ListingsDto.class))
    )
    @UserNotFoundResponse
    @ItemNotFoundResponse
    @GetMapping("/listings")
    public ResponseEntity<ListingsDto> myListings(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ListingDto> ids = resaleStoreService.getListings(user.getId());

        ListingsDto listingsDto = new ListingsDto(ids);

        return ResponseEntity.status(HttpStatus.OK).body(listingsDto);
    }

}
