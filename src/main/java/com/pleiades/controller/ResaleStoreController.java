package com.pleiades.controller;

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
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/face")
    public ResponseEntity<ResaleStoreDto> getFaceList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);
        List<ResaleItemDto> dtos = resaleStoreService.getItems(types);

        List<Long> wishIds = resaleStoreService.getWishlistItems(types, user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResaleStoreDto(dtos, wishIds));
    }

    @GetMapping("/fashion")
    public ResponseEntity<ResaleStoreDto> getFashionList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);
        List<ResaleItemDto> dtos = resaleStoreService.getItems(types);

        List<Long> wishIds = resaleStoreService.getWishlistItems(types, user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResaleStoreDto(dtos, wishIds));
    }

    @GetMapping("/bg")
    public ResponseEntity<ResaleStoreDto> getBgList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.STAR_BG, ItemType.STATION_BG);
        List<ResaleItemDto> dtos = resaleStoreService.getItems(types);

        List<Long> wishIds = resaleStoreService.getWishlistItems(types, user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResaleStoreDto(dtos, wishIds));
    }

    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> addWishlist(@RequestHeader("Authorization") String authorization, @RequestBody WishListDto wishlist) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        ValidationStatus validationStatus = resaleStoreService.addWishlist(user.get().getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Wishlist Already Existing"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Added"));
    }

    @DeleteMapping("/wishlist")
    public ResponseEntity<Map<String, String>> removeWishlist(@RequestHeader("Authorization") String authorization, @RequestBody WishListDto wishlist) {
        String email = authService.getEmailByAuthorization(authorization);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ValidationStatus validationStatus = resaleStoreService.removeWishlist(user.getId(), wishlist.getId());

        if (validationStatus == ValidationStatus.DUPLICATE) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Wishlist Not Found"));
        if (validationStatus == ValidationStatus.NOT_VALID) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item or User Not Found"));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Wishlist Removed"));
    }

    @GetMapping("/items/{item_id}/price")
    public ResponseEntity<List<ListingPriceDto>> getListingsPrice(@PathVariable("item_id") Long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(resaleStoreService.getListingsPrice(itemId));
    }

    @PostMapping("/trades")
    public ResponseEntity<PurchaseResponseDto> buyItem(@RequestHeader("Authorization") String authorization, @RequestBody ListingIdDto listingIdDto) {
        String email = authService.getEmailByAuthorization(authorization);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long ownershipId = resaleStoreService.buyItem(user.getId(), listingIdDto.getListingId());
        return ResponseEntity.status(HttpStatus.OK).body(PurchaseResponseDto.successOf(ownershipId));
    }

    @PostMapping("/listings")
    public ResponseEntity<ListingIdDto> listings(@RequestHeader("Authorization") String authorization, @RequestBody AddListingRequestDto addListingRequestDto) {
        String email = authService.getEmailByAuthorization(authorization);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long listingId = resaleStoreService.addListing(user.getId(), addListingRequestDto.getOwnershipId(), addListingRequestDto.getPrice());

        ListingIdDto listingIdDto = new ListingIdDto(listingId);

        return ResponseEntity.status(HttpStatus.OK).body(listingIdDto);
    }

    @DeleteMapping("/listings/{listing_id}")
    public ResponseEntity<Map<String,String>> deleteListing(@RequestHeader("Authorization") String authorization, @RequestParam Long listingId) {
        String email = authService.getEmailByAuthorization(authorization);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        resaleStoreService.deleteListing(user.getId(), listingId);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Listing Deleted Successfully"));
    }

    @GetMapping("/listings")
    public ResponseEntity<ListingsDto> myListings(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Long> ids = resaleStoreService.getListings(user.getId());

        ListingsDto listingsDto = new ListingsDto(ids);

        return ResponseEntity.status(HttpStatus.OK).body(listingsDto);
    }

}
