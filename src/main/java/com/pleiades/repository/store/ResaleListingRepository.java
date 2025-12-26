package com.pleiades.repository.store;

import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.ResaleListing;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResaleListingRepository extends JpaRepository<ResaleListing, Long> {

    @Query("SELECT i FROM ResaleListing i WHERE i.sourceOwnership.item.type IN :types")
    List<ResaleListing> findByTypes(@Param("types") List<ItemType> types);

    Optional<ResaleListing> findBySourceOwnershipId(Long id);

    Boolean existsBySourceOwnershipId(Long id);

    @Query("SELECT i FROM ResaleListing i WHERE i.sourceOwnership.user.id = :userId")
    List<ResaleListing> findByUserId(@Param("userId") String userId);

//    @Query("""
//        SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END
//        FROM ResaleListing rl
//        WHERE EXISTS {
//            rl.sourceOwnership.item.itemTheme.theme.name = :theme
//        }
//    """)
//    Boolean existsByItemTheme(@Param("theme") String theme);
}
