package com.pleiades.repository;

import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String Id);
    Optional<User> findByEmail(String email);
}

