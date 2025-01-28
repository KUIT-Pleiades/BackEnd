package com.pleiades.repository.character.outfit;

import com.pleiades.entity.character.outfit.Shoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoesRepository extends JpaRepository<Shoes, String> {
    Optional<Shoes> findById(Long id);
    Optional<Shoes> findByName(String name);
}
