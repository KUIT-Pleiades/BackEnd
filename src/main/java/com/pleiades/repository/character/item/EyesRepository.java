package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Eyes;
import com.pleiades.entity.character.Item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EyesRepository extends JpaRepository<Eyes, String> {
    Optional<Eyes> findByName(String name);
}