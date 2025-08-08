package com.pleiades.repository;

import com.pleiades.entity.Friend;
import com.pleiades.entity.User;
import com.pleiades.strings.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findById(Long friendId);
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);

    @Query("""
        SELECT COUNT(f) > 0 
        FROM Friend f 
        WHERE (f.sender = :currentUser AND f.receiver = :user AND f.status = :status)
           OR (f.sender = :user AND f.receiver = :currentUser AND f.status = :status)
    """)
    boolean isFriend(@Param("currentUser") User currentUser,
                     @Param("user") User user,
                     @Param("status") FriendStatus status);


    @Query("""
    SELECT f FROM Friend f
    WHERE (f.sender = :currentUser AND (:searchedUsers IS NULL OR f.receiver IN :searchedUsers))
       OR (f.receiver = :currentUser AND (:searchedUsers IS NULL OR f.sender IN :searchedUsers))
""")
    List<Friend> findAllByUsersIn(@Param("currentUser") User currentUser, @Param("searchedUsers") List<User> searchedUsers);

    // 받은 친구 요청 목록
    List<Friend> findByReceiverAndStatus(User receiver, FriendStatus status);

    // 친구 목록
    List<Friend> findBySenderAndStatusOrReceiverAndStatusOrderByCreatedAtDesc(User sender, FriendStatus status1, User receiver, FriendStatus status2);

    // 보낸 친구 요청 목록
    List<Friend> findBySenderAndStatus(User sender, FriendStatus status);

    void deleteAllBySenderOrReceiver(User sender, User receiver);
}
