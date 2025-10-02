package com.pleiades.repository.store;

import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.Ownership;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnershipRepository extends JpaRepository<Ownership, Long> {
    @Query("SELECT i FROM Ownership i WHERE i.item.type IN :types")
    List<Ownership> findByTypes(@Param("types") List<ItemType> types);

    boolean existsByUserIdAndItemId(Long userId, ItemType itemId);
}
