package com.pleiades.repository.character;

import com.pleiades.entity.User;
import com.pleiades.entity.character.Characters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Characters, Long> {
    Optional<Characters> findByUser(User user);
}
