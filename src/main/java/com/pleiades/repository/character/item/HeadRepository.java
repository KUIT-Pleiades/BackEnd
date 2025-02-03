package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Head;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HeadRepository extends JpaRepository<Head, String> {
    Optional<Head> findByName(String name);
}
