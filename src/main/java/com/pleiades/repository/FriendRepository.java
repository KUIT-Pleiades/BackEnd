package com.pleiades.repository;

import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import com.pleiades.strings.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findById(Long friendId);
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);

    // 받은 친구 요청 목록
    List<Friend> findByReceiverAndStatus(User receiver, FriendStatus status);

    // 친구 목록
    List<Friend> findBySenderAndStatusOrReceiverAndStatus(User sender, FriendStatus status1, User receiver, FriendStatus status2);

    // 보낸 친구 요청 목록
    List<Friend> findBySenderAndStatus(User sender, FriendStatus status);
}
