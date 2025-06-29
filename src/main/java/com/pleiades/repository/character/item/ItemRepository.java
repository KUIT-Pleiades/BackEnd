package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    Optional<Item> findById(String id);
}
