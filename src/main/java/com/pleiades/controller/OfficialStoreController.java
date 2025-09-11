package com.pleiades.controller;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.dto.store.OfficialStoreDto;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.store.OfficialStoreService;
import com.pleiades.strings.ItemType;
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
    public ResponseEntity<OfficialStoreDto> getFaceList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);
        List<OfficialItemDto> dtos = officialStoreService.getOfficialItems(types);

        List<Long> wishIds = officialStoreService.getWishlistItems(types, user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new OfficialStoreDto(dtos, wishIds));
    }

    @GetMapping("/fashion")
    public ResponseEntity<OfficialStoreDto> getFashionList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);
        List<OfficialItemDto> dtos = officialStoreService.getOfficialItems(types);

        List<Long> wishIds = officialStoreService.getWishlistItems(types, user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new OfficialStoreDto(dtos, wishIds));
    }

    @GetMapping("/bg")
    public ResponseEntity<OfficialStoreDto> getBgList(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        List<ItemType> types = List.of(ItemType.STAR_BG, ItemType.STATION_BG);
        List<OfficialItemDto> dtos = officialStoreService.getOfficialItems(types);

        List<Long> wishIds = officialStoreService.getWishlistItems(types, user.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new OfficialStoreDto(dtos, wishIds));
    }
}
