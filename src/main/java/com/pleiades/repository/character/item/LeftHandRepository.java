package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.Item.LeftHand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeftHandRepository extends JpaRepository<LeftHand, String> {
    Optional<LeftHand> findByName(String name);
}
