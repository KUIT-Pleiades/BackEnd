package com.pleiades.repository.store.search;

import com.pleiades.entity.store.search.ItemTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemThemeRepository extends JpaRepository<ItemTheme, Long> {
    @Query("SELECT i FROM ItemTheme i WHERE i.item.id=:itemId")
    public List<ItemTheme> findByItemId(@Param("itemId")Long itemId);

}
