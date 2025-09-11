package com.pleiades.repository.character;

import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TheItemRepository extends JpaRepository<TheItem, Long> {
    boolean existsByNameAndType(String name, ItemType type);

    Optional<TheItem> findByNameAndType(String name, ItemType type);

    @Query("SELECT i FROM TheItem i WHERE i.type=:type")
    List<TheItem> findByType(@Param("type") ItemType type);

    @Query("SELECT i FROM TheItem i WHERE i.type IN :types")
    List<TheItem> findByTypes(@Param("types") List<ItemType> types);

    @Query("SELECT i FROM TheItem i WHERE i.type=:type AND i.name=:name")
    Optional<TheItem> findByTypeAndName(@Param("type") ItemType type, @Param("name") String name);
}
