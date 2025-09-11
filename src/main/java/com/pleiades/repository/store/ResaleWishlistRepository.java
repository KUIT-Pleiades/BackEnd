package com.pleiades.repository.store;

import com.pleiades.entity.store.ResaleWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResaleWishlistRepository extends JpaRepository<ResaleWishlist, Long> {
}
