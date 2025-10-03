package com.pleiades.repository.store;

import com.pleiades.entity.store.ResaleWishlist;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResaleWishlistRepository extends JpaRepository<ResaleWishlist, Long> {
    @Query("SELECT i FROM ResaleWishlist i WHERE i.resaleListing.sourceOwnership.item.type IN :types AND i.user.id=:userid")
    List<ResaleWishlist> findByTypesInWishlist(@Param("types") List<ItemType> types, @Param("userid") String userid);
    Optional<ResaleWishlist> findByUserIdAndResaleListingId(String userId, Long resaleListingId);
    boolean existsByUserIdAndResaleListingId(String userId, Long resaleListingId);
}
