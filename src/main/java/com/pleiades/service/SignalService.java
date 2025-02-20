package com.pleiades.service;

import com.pleiades.entity.Signal;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.SignalRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignalService {

    private final SignalRepository signalRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> sendSignal(String email, String receiverId) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!friendRepository.isFriend(sender, receiver, FriendStatus.ACCEPTED)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (signalRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new CustomException(ErrorCode.ALREADY_SENT_SIGNAL);
        }

        Signal signal = Signal.builder()
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .build();

        signalRepository.save(signal);
        return ResponseEntity.ok(Map.of("message", "Signal sent successfully"));
    }

    @Transactional
    public List<Map<String, String>> getReceivedSignals(String email) {
        User receiver = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_EMAIL));
        return signalRepository.findByReceiver(receiver).stream()
                .map(signal -> Map.of(
                        "userId", signal.getSender().getId(),
                        "userName", signal.getSender().getUserName()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<Map<String, String>> deleteSignal(String email, String senderId) {
        User receiver = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_EMAIL));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!signalRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new CustomException(ErrorCode.SIGNAL_NOT_FOUND);
        }

        signalRepository.deleteBySenderAndReceiver(sender, receiver);
        return ResponseEntity.ok(Map.of("message", "Signal deleted"));
    }
}