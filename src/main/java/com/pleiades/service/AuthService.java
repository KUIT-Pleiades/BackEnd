package com.pleiades.service;

import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.Star;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.User;
import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.Item;
import com.pleiades.repository.StarBackgroundRepository;
import com.pleiades.repository.StarRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class AuthService {
    UserRepository userRepository;
    StarRepository starRepository;
    StarBackgroundRepository starBackgroundRepository;
    CharacterRepository characterRepository;

    JwtUtil jwtUtil;
    ImageJsonCreator imageJsonCreator;

    @Autowired
    AuthService(UserRepository userRepository, StarRepository starRepository, StarBackgroundRepository starBackgroundRepository,
                   CharacterRepository characterRepository, JwtUtil jwtUtil, ImageJsonCreator imageJsonCreator) {
        this.userRepository = userRepository; this.starRepository = starRepository;
        this.starBackgroundRepository = starBackgroundRepository;
        this.characterRepository = characterRepository;
        this.jwtUtil = jwtUtil; this.imageJsonCreator = imageJsonCreator;
    }


    public ValidationStatus checkToken(String token) {
        if (token == null || token.isEmpty()) { return ValidationStatus.NONE; }

        Claims tokenClaim = jwtUtil.validateToken(token);
        if (tokenClaim == null) { return ValidationStatus.NOT_VALID; }

        return ValidationStatus.VALID;
    }




//    public ResponseEntity<Map<String, String>> responseAccessTokenStatus(String accessToken) {
//
//    }

    public ResponseEntity<Map<String, String>> responseTokenStatus(String refreshToken) {
        Map<String, String> body = new HashMap<>();
        ValidationStatus tokenStatus = checkToken(refreshToken);

        if (tokenStatus == ValidationStatus.NONE ) {
            body.put("message", "token required");
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(body);  // 428
        }
        if (tokenStatus == ValidationStatus.NOT_VALID) {
            body.put("message", "token expired");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);  // 403
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);   // 401
    }

    public ResponseEntity<Map<String, String>> responseTokenValidation(String accessToken, String refreshToken) {
        ValidationStatus accessTokenStatus = checkToken(accessToken);

        if (accessTokenStatus == ValidationStatus.NONE) {
            return responseTokenStatus(refreshToken);
        }

        if (accessTokenStatus == ValidationStatus.NOT_VALID) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Access Token expired - Refresh Token is required."));
        }

        return null;
    }

    // access token 유효한 경우에만 사용
    public ResponseEntity<Map<String, String>> responseUserInfo(String accessToken) {
        Map<String, String> body = new HashMap<>();

        if (accessToken == null) { return new ResponseEntity<>(body, HttpStatus.PRECONDITION_REQUIRED);}

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            body.put("message", "User not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(body);
        }

        Optional<Star> star = starRepository.findByUserId(user.get().getId());
        if (star.isEmpty()) {
            body.put("message", "Star not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        Optional<StarBackground> starBackground = starBackgroundRepository.findById(star.get().getBackground().getId());

        if (starBackground.isEmpty()) {
            body.put("message", "Background not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        Optional<Characters> character = characterRepository.findByUser(user.get());

        if (character.isEmpty()) {
            body.put("message", "Character not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        CharacterFaceDto faceDto = imageJsonCreator.makeCharacterFaceJson(character.get().getSkin(), character.get().getExpression(), character.get().getHair());
        CharacterOutfitDto outfitDto = imageJsonCreator.makeCharacterOutfitJson(character.get().getTop(), character.get().getBottom(), character.get().getShoes());

        List<Item> items = new ArrayList<>();
        for (CharacterItem item : character.get().getCharacterItems()) { items.add(item.getItem()); }
        CharacterItemDto itemDto = imageJsonCreator.makeCharacterItemJson(items);

        body.put("userId", user.get().getId());
        body.put("username", user.get().getUserName());
        body.put("backgroundName", starBackground.get().getName());
        body.put("face", faceDto.toString());
        body.put("outfit", outfitDto.toString());
        body.put("items", itemDto.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    public Cookie setRefreshToken(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);        // 추후 true로 변경
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }
}
