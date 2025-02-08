package com.pleiades.service;

import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public ResponseEntity<Map<String, Object>> sendFriendRequest(String email, String receiverId) {

        // access token -> sender
        User sender = userRepository.findByEmail(email).orElseThrow(()-> new CustomException(ErrorCode.INVALID_USER_EMAIL, "login token expired"));

        // receiver
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "receiver doesn't exist"));

        // 이미 친구 요청 보낸 경우
        Optional<Friend> optionalFriend = friendRepository.findBySenderAndReceiver(sender,receiver);

        if(optionalFriend.isPresent()){
            Friend existingFriend = optionalFriend.get();

            // REJECTED -> PENDING
            if(existingFriend.getStatus().equals(FriendStatus.REJECTED)){
                log.info("친구 요청: REJECTED -> PENDING");
                existingFriend.setStatus(FriendStatus.PENDING);
                existingFriend.setCreatedAt(LocalDateTime.now());
                friendRepository.save(existingFriend);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message","Friend request sent successfully"));
            }

            // already PENDING
            if (existingFriend.getStatus().equals(FriendStatus.PENDING)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "already pending"));
            }

            // already ACCEPTED
            if (existingFriend.getStatus().equals(FriendStatus.ACCEPTED)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "already friend"));
            }
        }

        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        friendRepository.save(friend);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message","Friend request sent successfully"));

    }
}
