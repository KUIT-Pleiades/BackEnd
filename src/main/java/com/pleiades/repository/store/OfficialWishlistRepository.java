package com.pleiades.repository.store;

import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.strings.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficialWishlistRepository extends JpaRepository<OfficialWishlist, Long> {

    // user ID, type (TOP, BOTTOM, SET ...) 으로 찾기
    // TheItemRepository: item 찾을 때 type 으로 찾아야 함
    @Query("SELECT i FROM OfficialWishlist i WHERE i.item.type IN :types AND i.user.id=:userid")
    List<OfficialWishlist> findByTypesInWishlist(@Param("types") List<ItemType> types, @Param("userid") String userid);

    Optional<OfficialWishlist> findByUserIdAndItemId(String userId, Long itemId);

    boolean existsByUserIdAndItemId(String userId, Long itemId);

    // store 내 검색 query
    @Query("""
select ow.item.id
from OfficialWishlist ow
where ow.user.id = :userId
""")
    List<Long> findAllWishlistItemIds(@Param("userId") String userId);

}
