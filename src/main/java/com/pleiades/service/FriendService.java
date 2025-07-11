package com.pleiades.service;

import com.pleiades.dto.friend.FriendDto;
import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import com.pleiades.util.LocalDateTimeUtil;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // 받은 친구 요청 목록
    @Transactional
    public List<FriendDto> getReceivedFriendRequests(String email) {
        User currentUser = getUserByEmail(email);
        return friendRepository.findByReceiverAndStatus(currentUser, FriendStatus.PENDING).stream()
                .map(f -> new FriendDto(
                        f.getId(),
                        f.getSender().getId(),
                        f.getSender().getUserName(),
                        f.getSender().getProfileUrl()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<FriendDto> getFriends(String email) {
        User currentUser = getUserByEmail(email);
        return friendRepository.findBySenderAndStatusOrReceiverAndStatusOrderByCreatedAtDesc(
                        currentUser, FriendStatus.ACCEPTED, currentUser, FriendStatus.ACCEPTED
                ).stream()
                .map(f -> {
                    // sender == currentUser -> (true) f == receiver : (false) f == sender
                    User friendUser = f.getSender().equals(currentUser) ? f.getReceiver() : f.getSender();
                    return new FriendDto(
                            f.getId(),
                            friendUser.getId(),
                            friendUser.getUserName(),
                            friendUser.getProfileUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    //보낸 친구 요청 목록
    @Transactional
    public List<FriendDto> getSentFriendRequests(String email) {
        User currentUser = getUserByEmail(email);
        return friendRepository.findBySenderAndStatus(currentUser, FriendStatus.PENDING).stream()
                .map(f -> new FriendDto(
                        f.getId(),
                        f.getReceiver().getId(),
                        f.getReceiver().getUserName(),
                        f.getReceiver().getProfileUrl()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> sendFriendRequest(String email, String receiverId) {

        // access token -> sender
        User sender = userRepository.findByEmail(email).orElseThrow(()-> new CustomException(ErrorCode.INVALID_TOKEN));

        // receiver
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // sender == receiver
        if(sender == receiver){
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 이미 친구 요청 보낸 경우
        Optional<Friend> iamAlreadySender = friendRepository.findBySenderAndReceiver(sender,receiver);
        Optional<Friend> iamAlreadyReceiver = friendRepository.findBySenderAndReceiver(receiver, sender);

        if(iamAlreadyReceiver.isPresent () &&iamAlreadyReceiver.get().getStatus()==FriendStatus.PENDING){
            throw new CustomException(ErrorCode.ALREADY_RECEIVED_FRIEND_REQUEST);
        }

        if(iamAlreadySender.isPresent()){
            Friend existingFriend = iamAlreadySender.get();

            // REJECTED -> PENDING
            if(existingFriend.getStatus().equals(FriendStatus.REJECTED)){
                log.info("친구 요청: REJECTED -> PENDING");
                existingFriend.setStatus(FriendStatus.PENDING);
                existingFriend.setCreatedAt(LocalDateTimeUtil.now());
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
                .createdAt(LocalDateTimeUtil.now())
                .build();

        friendRepository.save(friend);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message","Friend request sent successfully"));

    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateFriendStatus(String email, String userId, FriendStatus newStatus) {
        User currentUser = getUserByEmail(email);
        User friendUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Optional<Friend> optionalFriend = friendRepository.findBySenderAndReceiver(friendUser, currentUser);

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
        throw new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> deleteFriend(String email, String userId) {
        User currentUser = getUserByEmail(email);
        User friendUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Optional<Friend> optionalFriend = friendRepository.findBySenderAndReceiver(currentUser, friendUser);
        if (optionalFriend.isEmpty()) {
            optionalFriend = friendRepository.findBySenderAndReceiver(friendUser, currentUser);
        }

        if (optionalFriend.isPresent()) {
            Friend friend = optionalFriend.get();

            // 요청 취소
            if(friend.getStatus() == FriendStatus.PENDING){
                // 저장된 friend sender != 현재 user
                if(!friend.getSender().getEmail().equals(email)){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message","You cannot handle the request"));
                }
                friendRepository.delete(friend);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message","Friend request canceled"));
            }

            // 친구 삭제
            if(friend.getStatus() == FriendStatus.ACCEPTED){
                friendRepository.delete(friend);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message","Friend deleted"));
            }
        }
        throw new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_EMAIL));
    }
}
