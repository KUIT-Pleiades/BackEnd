package com.pleiades.repository.character;

import com.pleiades.entity.User;
import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.TheItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {
    void deleteAllByCharacter(Characters character);

    @Query("SELECT CASE WHEN EXISTS " +
            "(SELECT 1 " +
            "FROM CharacterItem ci " +
            "JOIN Characters c ON ci.character = c " +
            "WHERE c.user = :user " +
            "AND ci.item = :item) " +
            "THEN true ELSE false END")
    boolean existsByUserAndItem(@Param("user") User user, @Param("item") TheItem theItem);
}
