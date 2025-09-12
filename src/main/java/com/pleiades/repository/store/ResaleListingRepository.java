package com.pleiades.repository.store;

import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.ResaleListing;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResaleListingRepository extends JpaRepository<ResaleListing, Long> {

    @Query("SELECT i FROM ResaleListing i WHERE i.ownership.item.type IN :types")
    List<ResaleListing> findByTypes(@Param("types") List<ItemType> types);
}
