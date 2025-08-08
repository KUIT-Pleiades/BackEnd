package com.pleiades.repository;

import com.pleiades.entity.KakaoToken;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {
    Optional<KakaoToken> findByUser_Id(String userId);
    Optional<KakaoToken> findByEmail(String email);
    void deleteByUser(User user);
}
