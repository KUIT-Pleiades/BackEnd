package com.pleiades.repository.store;

import com.pleiades.entity.store.ResaleListing;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResaleListingRepository extends JpaRepository<ResaleListing, Long> {

    @Query("SELECT i FROM ResaleListing i WHERE i.sourceOwnership.item.type IN :types AND i.status = 'ONSALE'")
    List<ResaleListing> findListingsOnSaleByTypes(@Param("types") List<ItemType> types);

    Optional<ResaleListing> findBySourceOwnershipId(Long id);

    Boolean existsBySourceOwnershipId(Long id);

    @Query("SELECT i FROM ResaleListing i WHERE i.sourceOwnership.user.id = :userId")
    List<ResaleListing> findBySourceOwnershipUserId(@Param("userId") String userId);

    @Query("SELECT i FROM ResaleListing i " +
            "WHERE i.sourceOwnership.user.id = :userId AND i.status = :status")
    List<ResaleListing> findBySourceOwnershipUserIdAndSaleStatus(@Param("userId") String userId,  @Param("status") SaleStatus status);

    @Query("SELECT i FROM ResaleListing i " +
            "JOIN FETCH i.resultOwnership ro " +
            "JOIN FETCH ro.item " +
            "WHERE i.sourceOwnership.user.id = :userId AND i.status = 'SOLDOUT'")
    List<ResaleListing> findSoldListingsBySourceOwnershipUserIdWithResultOwnershipAndItem(@Param("userId") String userId);

    // store 내 검색 query (resale)
    @Query("""
        SELECT DISTINCT rl
        FROM ResaleListing rl
        JOIN rl.sourceOwnership so
        JOIN so.item it
        LEFT JOIN ItemTheme ith ON ith.item.id = it.id
        LEFT JOIN ith.theme th
        WHERE rl.status = 'ONSALE'
        AND (
            LOWER(it.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(it.description) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(th.name) LIKE LOWER(CONCAT('%', :query, '%'))
        )
    """)
    List<ResaleListing> searchOnSale(@Param("query") String query);
}