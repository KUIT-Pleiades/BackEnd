package com.pleiades.repository.character;

import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.Characters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {
    void deleteAllByCharacter(Characters character);
}
