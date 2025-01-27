package com.pleiades.repository.outfit;

import com.pleiades.entity.User;
import com.pleiades.entity.outfit.Top;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopRepository extends JpaRepository<Top, String> {
    Optional<Top> findById(Long id);
    Optional<Top> findByName(String name);
}
