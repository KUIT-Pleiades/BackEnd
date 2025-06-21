package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Neck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NeckRepository extends JpaRepository<Neck, String> {
    Optional<Neck> findByName(String name);
}
