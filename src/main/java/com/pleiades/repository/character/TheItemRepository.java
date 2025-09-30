package com.pleiades.repository.character;

import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TheItemRepository extends JpaRepository<TheItem, Long> {
    boolean existsByNameAndType(String name, ItemType type);

    Optional<TheItem> findByNameAndType(String name, ItemType type);

    // @Query 없어도 동작
    List<TheItem> findByType(ItemType type);

    @Query("SELECT i FROM TheItem i WHERE i.type IN :types")
    List<TheItem> findByTypes(List<ItemType> types);

    Optional<TheItem> findByTypeAndName(ItemType type, String name);

    Optional<TheItem> findByName(String itemName);
}
