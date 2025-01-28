package com.pleiades.repository.character;

import com.pleiades.entity.character.CharacterItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {
}
