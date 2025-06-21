package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.Item.LeftWrist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeftWristRepository extends JpaRepository<LeftWrist, String> {
    Optional<LeftWrist> findByName(String name);
}
