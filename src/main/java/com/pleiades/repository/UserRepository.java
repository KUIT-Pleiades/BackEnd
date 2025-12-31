package com.pleiades.repository;

import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String userId);
    Optional<User> findByEmail(String email);
    List<User> findByIdContainingIgnoreCase(String userId);

    @Modifying
    @Query("UPDATE User u SET u.stoneCharge = false")
    void resetStoneChargeToFalse();
}
