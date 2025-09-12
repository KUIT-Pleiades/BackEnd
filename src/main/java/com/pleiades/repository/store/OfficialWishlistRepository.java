package com.pleiades.repository.store;

import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficialWishlistRepository extends JpaRepository<OfficialWishlist, Long> {

    @Query("SELECT i FROM OfficialWishlist i WHERE i.item.type IN :types AND i.user.id=:userid")
    List<OfficialWishlist> findByTypesInWishlist(@Param("types") List<ItemType> types, @Param("userid") String userid);
}
