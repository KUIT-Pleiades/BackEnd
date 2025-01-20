package com.pleiades.repository;

import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NaverTokenRepository extends JpaRepository<NaverToken, Long> {
    @Query("SELECT nt FROM NaverToken nt WHERE nt.user.email = :email")
    Optional<NaverToken> findByUserEmail(
            @Param("email") String email);

    Optional<NaverToken> findByRefreshToken(String refreshToken);
    Optional<NaverToken> findByEmail(String email);
}

