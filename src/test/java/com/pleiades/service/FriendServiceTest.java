package com.pleiades.service;

import com.pleiades.dto.friend.FriendDto;
import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendService friendService;

    @Test
    @DisplayName("getReceivedFriendRequests - 정상 케이스")
    void getReceivedFriendRequests_validRequest_returnsFriendList() {
        // given
        String email = "test@example.com";
        User currentUser = User.builder().id("user1").email(email).build();
        User sender = User.builder()
                .id("user2")
                .userName("친구")
                .profileUrl("profile_url")
                .build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(sender)
                .receiver(currentUser)
                .status(FriendStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(friendRepository.findByReceiverAndStatus(currentUser, FriendStatus.PENDING))
                .thenReturn(List.of(friend));

        // when
        List<FriendDto> result = friendService.getReceivedFriendRequests(email);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user2");
        assertThat(result.get(0).getUserName()).isEqualTo("친구");
    }

    @Test
    @DisplayName("getReceivedFriendRequests - 사용자가 존재하지 않을 때 예외 발생")
    void getReceivedFriendRequests_userNotFound_throwsException() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.getReceivedFriendRequests(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_USER_EMAIL);
    }

    @Test
    @DisplayName("getFriends - 정상 케이스")
    void getFriends_validRequest_returnsFriendList() {
        // given
        String email = "test@example.com";
        User currentUser = User.builder().id("user1").email(email).build();
        User friend = User.builder()
                .id("user2")
                .userName("친구")
                .profileUrl("profile_url")
                .build();

        Friend friendEntity = Friend.builder()
                .id(1L)
                .sender(currentUser)
                .receiver(friend)
                .status(FriendStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(friendRepository.findBySenderAndStatusOrReceiverAndStatusOrderByCreatedAtDesc(
                currentUser, FriendStatus.ACCEPTED, currentUser, FriendStatus.ACCEPTED))
                .thenReturn(List.of(friendEntity));

        // when
        List<FriendDto> result = friendService.getFriends(email);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user2");
    }

    @Test
    @DisplayName("getSentFriendRequests - 정상 케이스")
    void getSentFriendRequests_validRequest_returnsFriendList() {
        // given
        String email = "test@example.com";
        User currentUser = User.builder().id("user1").email(email).build();
        User receiver = User.builder()
                .id("user2")
                .userName("친구")
                .profileUrl("profile_url")
                .build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(currentUser)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(friendRepository.findBySenderAndStatus(currentUser, FriendStatus.PENDING))
                .thenReturn(List.of(friend));

        // when
        List<FriendDto> result = friendService.getSentFriendRequests(email);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user2");
    }

    @Test
    @DisplayName("sendFriendRequest - sender와 receiver가 같을 때 예외 발생")
    void sendFriendRequest_senderEqualsReceiver_throwsException() {
        // given
        String email = "test@example.com";
        String receiverId = "user1";
        User sender = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(sender));

        // when & then
        assertThatThrownBy(() -> friendService.sendFriendRequest(email, receiverId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("sendFriendRequest - 이미 받은 친구 요청이 있을 때 예외 발생")
    void sendFriendRequest_alreadyReceivedRequest_throwsException() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        Friend existingFriend = Friend.builder()
                .id(1L)
                .sender(receiver)
                .receiver(sender)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());
        when(friendRepository.findBySenderAndReceiver(receiver, sender)).thenReturn(Optional.of(existingFriend));

        // when & then
        assertThatThrownBy(() -> friendService.sendFriendRequest(email, receiverId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_RECEIVED_FRIEND_REQUEST);
    }

    @Test
    @DisplayName("sendFriendRequest - REJECTED 상태에서 PENDING으로 변경")
    void sendFriendRequest_rejectedToPending_returnsCreated() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        Friend existingFriend = Friend.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.REJECTED)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(existingFriend));
        when(friendRepository.findBySenderAndReceiver(receiver, sender)).thenReturn(Optional.empty());
        when(friendRepository.save(any(Friend.class))).thenReturn(existingFriend);

        // when
        ResponseEntity<Map<String, Object>> response = friendService.sendFriendRequest(email, receiverId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(existingFriend.getStatus()).isEqualTo(FriendStatus.PENDING);
        verify(friendRepository).save(existingFriend);
    }

    @Test
    @DisplayName("sendFriendRequest - 이미 PENDING 상태일 때 CONFLICT 반환")
    void sendFriendRequest_alreadyPending_returnsConflict() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        Friend existingFriend = Friend.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(existingFriend));
        when(friendRepository.findBySenderAndReceiver(receiver, sender)).thenReturn(Optional.empty());

        // when
        ResponseEntity<Map<String, Object>> response = friendService.sendFriendRequest(email, receiverId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("message", "already pending");
    }

    @Test
    @DisplayName("sendFriendRequest - 이미 ACCEPTED 상태일 때 CONFLICT 반환")
    void sendFriendRequest_alreadyAccepted_returnsConflict() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        Friend existingFriend = Friend.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.ACCEPTED)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(existingFriend));
        when(friendRepository.findBySenderAndReceiver(receiver, sender)).thenReturn(Optional.empty());

        // when
        ResponseEntity<Map<String, Object>> response = friendService.sendFriendRequest(email, receiverId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("message", "already friend");
    }

    @Test
    @DisplayName("sendFriendRequest - 새로운 친구 요청 생성")
    void sendFriendRequest_newRequest_returnsCreated() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        Friend newFriend = Friend.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());
        when(friendRepository.findBySenderAndReceiver(receiver, sender)).thenReturn(Optional.empty());
        when(friendRepository.save(any(Friend.class))).thenReturn(newFriend);

        // when
        ResponseEntity<Map<String, Object>> response = friendService.sendFriendRequest(email, receiverId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("message", "Friend request sent successfully");
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("updateFriendStatus - 친구 요청이 존재하지 않을 때 예외 발생")
    void updateFriendStatus_friendRequestNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email(email).build();
        User friendUser = User.builder().id("user2").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(friendUser, currentUser)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.updateFriendStatus(email, userId, FriendStatus.ACCEPTED))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("updateFriendStatus - receiver가 아닐 때 FORBIDDEN 반환")
    void updateFriendStatus_notReceiver_returnsForbidden() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email("different@example.com").build();
        User friendUser = User.builder().id("user2").build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(friendUser)
                .receiver(currentUser)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(friendUser, currentUser)).thenReturn(Optional.of(friend));

        // when
        ResponseEntity<Map<String, String>> response = friendService.updateFriendStatus(email, userId, FriendStatus.ACCEPTED);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("message", "You cannot handle the request");
    }

    @Test
    @DisplayName("updateFriendStatus - ACCEPTED 상태로 변경")
    void updateFriendStatus_acceptRequest_returnsOk() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email(email).build();
        User friendUser = User.builder().id("user2").build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(friendUser)
                .receiver(currentUser)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(friendUser, currentUser)).thenReturn(Optional.of(friend));
        when(friendRepository.save(any(Friend.class))).thenReturn(friend);

        // when
        ResponseEntity<Map<String, String>> response = friendService.updateFriendStatus(email, userId, FriendStatus.ACCEPTED);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Friend request accepted");
        assertThat(friend.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
        verify(friendRepository).save(friend);
    }

    @Test
    @DisplayName("updateFriendStatus - REJECTED 상태로 변경")
    void updateFriendStatus_rejectRequest_returnsOk() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email(email).build();
        User friendUser = User.builder().id("user2").build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(friendUser)
                .receiver(currentUser)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(friendUser, currentUser)).thenReturn(Optional.of(friend));
        when(friendRepository.save(any(Friend.class))).thenReturn(friend);

        // when
        ResponseEntity<Map<String, String>> response = friendService.updateFriendStatus(email, userId, FriendStatus.REJECTED);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Friend request rejected");
        assertThat(friend.getStatus()).isEqualTo(FriendStatus.REJECTED);
        verify(friendRepository).save(friend);
    }

    @Test
    @DisplayName("deleteFriend - 친구 요청이 존재하지 않을 때 예외 발생")
    void deleteFriend_friendRequestNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email(email).build();
        User friendUser = User.builder().id("user2").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(currentUser, friendUser)).thenReturn(Optional.empty());
        when(friendRepository.findBySenderAndReceiver(friendUser, currentUser)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.deleteFriend(email, userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("deleteFriend - PENDING 상태 요청 취소")
    void deleteFriend_cancelPendingRequest_returnsOk() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email(email).build();
        User friendUser = User.builder().id("user2").build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(currentUser)
                .receiver(friendUser)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(currentUser, friendUser)).thenReturn(Optional.of(friend));
        doNothing().when(friendRepository).delete(friend);

        // when
        ResponseEntity<Map<String, String>> response = friendService.deleteFriend(email, userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Friend request canceled");
        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("deleteFriend - PENDING 상태이지만 sender가 아닐 때 FORBIDDEN 반환")
    void deleteFriend_pendingButNotSender_returnsForbidden() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email("different@example.com").build();
        User friendUser = User.builder().id("user2").build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(friendUser)
                .receiver(currentUser)
                .status(FriendStatus.PENDING)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(currentUser, friendUser)).thenReturn(Optional.empty());
        when(friendRepository.findBySenderAndReceiver(friendUser, currentUser)).thenReturn(Optional.of(friend));

        // when
        ResponseEntity<Map<String, String>> response = friendService.deleteFriend(email, userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("message", "You cannot handle the request");
    }

    @Test
    @DisplayName("deleteFriend - ACCEPTED 상태 친구 삭제")
    void deleteFriend_deleteAcceptedFriend_returnsOk() {
        // given
        String email = "test@example.com";
        String userId = "user2";
        User currentUser = User.builder().id("user1").email(email).build();
        User friendUser = User.builder().id("user2").build();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(currentUser)
                .receiver(friendUser)
                .status(FriendStatus.ACCEPTED)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(friendUser));
        when(friendRepository.findBySenderAndReceiver(currentUser, friendUser)).thenReturn(Optional.of(friend));
        doNothing().when(friendRepository).delete(friend);

        // when
        ResponseEntity<Map<String, String>> response = friendService.deleteFriend(email, userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Friend deleted");
        verify(friendRepository).delete(friend);
    }
}

