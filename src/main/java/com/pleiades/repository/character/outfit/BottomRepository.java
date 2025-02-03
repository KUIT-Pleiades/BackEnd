package com.pleiades.repository.character.outfit;

import com.pleiades.entity.character.outfit.Bottom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BottomRepository extends JpaRepository<Bottom, String> {
    Optional<Bottom> findByName(String name);

}
