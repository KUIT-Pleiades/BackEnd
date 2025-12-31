package com.pleiades.repository;

import com.pleiades.entity.store.search.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {

    Optional<Theme> findByName(String name);

    @Query("SELECT DISTINCT th FROM ResaleListing rl " +
            "JOIN rl.sourceOwnership o " +
            "JOIN o.item i " +
            "JOIN i.itemThemes it " +
            "JOIN it.theme th " +
            "WHERE rl.status = 'ONSALE'")
    List<Theme> findThemesWithResaleListings();

}
