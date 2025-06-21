package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Ears;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EarsRepository extends JpaRepository<Ears, String> {
    Optional<Ears> findByName(String name);
}
