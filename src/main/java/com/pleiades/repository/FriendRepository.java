package com.pleiades.repository;

import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findById(Long friendId);
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);
    boolean existsBySenderAndReceiverOrReceiverAndSender(User sender, User receiver, User receiver2, User sender2);

}
