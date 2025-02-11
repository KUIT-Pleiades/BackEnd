package com.pleiades.repository;

import com.pleiades.entity.User;
import com.pleiades.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    // 최신순으로 검색 기록 가져 오기
    List<UserHistory> findByCurrentUserOrderByUpdatedAtDesc(User currentUser);

    // 검색 기록이 존재 하는 지 확인
    Optional<UserHistory> findByCurrentUserAndSearchedUser(User currentUser, User searchedUser);
}
