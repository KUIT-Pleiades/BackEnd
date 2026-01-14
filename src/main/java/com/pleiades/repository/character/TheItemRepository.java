package com.pleiades.repository.character;

import com.pleiades.entity.User;
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

    // @Query 없어도 동작
    List<TheItem> findByType(ItemType type);

    @Query("SELECT i " +
            "FROM TheItem i " +
            "WHERE (i.isBasic = true OR EXISTS (SELECT 1 FROM Ownership o WHERE o.user.id = :userId AND o.item = i AND o.active = true)) " +
            "AND i.type = :type")
    List<TheItem> findUsableBgs(@Param("userId") String userId, @Param("type") ItemType type);

    @Query("SELECT i " +
            "FROM TheItem i " +
            "WHERE i.isBasic = false " +
            "AND i.type IN :types ")
    List<TheItem> findNotBasicItemsByTypes(@Param("types") List<ItemType> types);

    Optional<TheItem> findByTypeAndName(ItemType type, String name);

    Optional<TheItem> findByName(String itemName);

    @Query("SELECT i " +
            "FROM TheItem i " +
            "WHERE (i.isBasic = true OR EXISTS (SELECT 1 FROM Ownership o WHERE o.user.id = :userId AND o.item = i AND o.active = true)) " +
            "AND i.type NOT IN ('STAR_BG', 'STATION_BG')")
    List<TheItem> findWearableItems(@Param("userId") String userId);

    // store 내 검색 query
    @Query("""
select distinct i
from TheItem i
left join i.itemThemes it
left join it.theme t
left join i.itemKeywords ik
left join ik.keyword k
where
    (:query is null or :query = '' or
     lower(i.name) like lower(concat('%', :query, '%'))
     or lower(i.description) like lower(concat('%', :query, '%'))
     or lower(t.name) like lower(concat('%', :query, '%'))
     or lower(k.name) like lower(concat('%', :query, '%'))
    )
""")
    List<TheItem> searchOfficialItems(@Param("query") String query);
}
