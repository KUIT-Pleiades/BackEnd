package com.pleiades.service;

import com.pleiades.dto.SignalResponseDto;
import com.pleiades.entity.Signal;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.SignalRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalServiceTest {

    @Mock
    private SignalRepository signalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private SignalService signalService;

    @Test
    @DisplayName("sendSignal - sender가 존재하지 않을 때 예외 발생")
    void sendSignal_senderNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        int imageIndex = 0;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signalService.sendSignal(email, receiverId, imageIndex))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("sendSignal - receiver가 존재하지 않을 때 예외 발생")
    void sendSignal_receiverNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        int imageIndex = 0;
        User sender = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signalService.sendSignal(email, receiverId, imageIndex))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("sendSignal - 친구가 아닐 때 예외 발생")
    void sendSignal_notFriend_throwsException() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        int imageIndex = 0;
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.isFriend(sender, receiver, FriendStatus.ACCEPTED)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> signalService.sendSignal(email, receiverId, imageIndex))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("sendSignal - 이미 시그널이 존재할 때 OK 반환")
    void sendSignal_signalAlreadyExists_returnsOk() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        int imageIndex = 0;
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.isFriend(sender, receiver, FriendStatus.ACCEPTED)).thenReturn(true);
        when(signalRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(true);

        // when
        ResponseEntity<Map<String, String>> response = signalService.sendSignal(email, receiverId, imageIndex);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("message", "Signal sent successfully");
        verify(signalRepository, never()).save(any(Signal.class));
    }

    @Test
    @DisplayName("sendSignal - 정상 케이스")
    void sendSignal_validRequest_returnsOk() {
        // given
        String email = "test@example.com";
        String receiverId = "user2";
        int imageIndex = 0;
        User sender = User.builder().id("user1").email(email).build();
        User receiver = User.builder().id("user2").build();

        Signal signal = Signal.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .imageIndex(imageIndex)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRepository.isFriend(sender, receiver, FriendStatus.ACCEPTED)).thenReturn(true);
        when(signalRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(false);
        when(signalRepository.save(any(Signal.class))).thenReturn(signal);

        // when
        ResponseEntity<Map<String, String>> response = signalService.sendSignal(email, receiverId, imageIndex);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("message", "Signal sent successfully");
        verify(signalRepository).save(any(Signal.class));
    }

    @Test
    @DisplayName("getReceivedSignals - receiver가 존재하지 않을 때 예외 발생")
    void getReceivedSignals_receiverNotFound_throwsException() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signalService.getReceivedSignals(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("getReceivedSignals - 정상 케이스")
    void getReceivedSignals_validRequest_returnsSignals() {
        // given
        String email = "test@example.com";
        User receiver = User.builder().id("user1").email(email).build();
        User sender1 = User.builder().id("user2").userName("친구1").build();
        User sender2 = User.builder().id("user3").userName("친구2").build();

        Signal signal1 = Signal.builder()
                .id(1L)
                .sender(sender1)
                .receiver(receiver)
                .imageIndex(0)
                .createdAt(LocalDateTime.now())
                .build();

        Signal signal2 = Signal.builder()
                .id(2L)
                .sender(sender2)
                .receiver(receiver)
                .imageIndex(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiver));
        when(signalRepository.findByReceiver(receiver)).thenReturn(List.of(signal1, signal2));

        // when
        ResponseEntity<Map<String, List<SignalResponseDto>>> response = signalService.getReceivedSignals(email);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsKey("signals");
        List<SignalResponseDto> signals = response.getBody().get("signals");
        assertThat(signals).hasSize(2);
        assertThat(signals.get(0).getUserId()).isEqualTo("user2");
        assertThat(signals.get(0).getImageIndex()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteSignal - receiver가 존재하지 않을 때 예외 발생")
    void deleteSignal_receiverNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String senderId = "user2";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signalService.deleteSignal(email, senderId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("deleteSignal - sender가 존재하지 않을 때 예외 발생")
    void deleteSignal_senderNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String senderId = "user2";
        User receiver = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signalService.deleteSignal(email, senderId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("deleteSignal - 시그널이 존재하지 않을 때 예외 발생")
    void deleteSignal_signalNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String senderId = "user2";
        User receiver = User.builder().id("user1").email(email).build();
        User sender = User.builder().id("user2").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(signalRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> signalService.deleteSignal(email, senderId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SIGNAL_NOT_FOUND);
    }

    @Test
    @DisplayName("deleteSignal - 정상 케이스")
    void deleteSignal_validRequest_returnsOk() {
        // given
        String email = "test@example.com";
        String senderId = "user2";
        User receiver = User.builder().id("user1").email(email).build();
        User sender = User.builder().id("user2").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(signalRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(true);
        doNothing().when(signalRepository).deleteBySenderAndReceiver(sender, receiver);

        // when
        ResponseEntity<Map<String, String>> response = signalService.deleteSignal(email, senderId);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("message", "Signal received and deleted");
        verify(signalRepository).deleteBySenderAndReceiver(sender, receiver);
    }
}

