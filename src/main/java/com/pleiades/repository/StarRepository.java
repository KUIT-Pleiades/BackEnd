package com.pleiades.repository;

import com.pleiades.entity.Star;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    Optional<Star> findByUserId(String userId);
    void deleteByUser(User user);
}
