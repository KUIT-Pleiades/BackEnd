package com.pleiades.repository.store;

import com.pleiades.strings.ItemType;
import com.pleiades.entity.store.Ownership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnershipRepository extends JpaRepository<Ownership, Long> {
    @Query("SELECT i FROM Ownership i WHERE i.item.type IN :types")
    List<Ownership> findByTypes(@Param("types") List<ItemType> types);

    boolean existsByUserIdAndItemId(String userId, Long itemId);

    @Query("SELECT i FROM Ownership i WHERE i.user.id = :userId")
    List<Ownership> findByUserId(String userId);
}
