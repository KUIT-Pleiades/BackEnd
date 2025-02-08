package com.pleiades.service;

import com.pleiades.dto.SearchUserDto;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_EMAIL, "login token expired"));
    }

    public List<SearchUserDto> searchUser(String userId, String email) {
        List<User> users = userRepository.findByIdContainingIgnoreCase(userId);
        User currentUser = getUserByEmail(email);

        return users.stream()
                .map(user -> {
                    boolean isFriend = friendRepository
                            .isFriend(currentUser, user, FriendStatus.ACCEPTED);

                    return new SearchUserDto(
                            user.getId(),
                            user.getUserName(),
                            user.getImgPath(),
                            isFriend
                    );
                })
                .toList();
    }
}
