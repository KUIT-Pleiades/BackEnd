package com.pleiades.repository.character.outfit;

import com.pleiades.entity.character.outfit.Top;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopRepository extends JpaRepository<Top, String> {
    Optional<Top> findByName(String name);
}
