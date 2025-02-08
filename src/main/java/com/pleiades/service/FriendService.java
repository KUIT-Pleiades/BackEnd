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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // 받은 친구 요청 목록
    @Transactional
    public List<Friend> getReceivedFriendRequests(String email) {
        User receiver = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "User not found"));

        return friendRepository.findByReceiverAndStatus(receiver, FriendStatus.PENDING);
    }

    @Transactional
    public List<Friend> getFriends(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "User not found"));

        return friendRepository.findBySenderAndStatusOrReceiverAndStatusOrderByCreatedAtDesc(
                user, FriendStatus.ACCEPTED, user, FriendStatus.ACCEPTED);
    }

    //보낸 친구 요청 목록
    @Transactional
    public List<Friend> getSentFriendRequests(String email) {
        User sender = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "User not found"));

        return friendRepository.findBySenderAndStatus(sender, FriendStatus.PENDING);
    }

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

    @Transactional
    public ResponseEntity<Map<String, String>> updateFriendStatus(String email, Long friendId, FriendStatus newStatus) {
        Optional<Friend> optionalFriend = friendRepository.findById(friendId);

        if (optionalFriend.isPresent()) {
            Friend friend = optionalFriend.get();

            // 저장된 friend receiver != 현재 user
            if(!friend.getReceiver().getEmail().equals(email)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message","You cannot handle the request"));
            }

            // status update
            friend.setStatus(newStatus);
            friendRepository.save(friend);

            // response
            if(newStatus.equals(FriendStatus.ACCEPTED)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message","Friend request accepted"));
            }
            if(newStatus.equals(FriendStatus.REJECTED)){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message","Friend request rejected"));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message","Friend request not found"));
    }
}
