package com.pleiades.repository;

import com.pleiades.entity.FcmToken;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    List<FcmToken> findAllByUser(User user);
    Optional<FcmToken> findByToken(String token);
    void deleteByToken(String token);
}
