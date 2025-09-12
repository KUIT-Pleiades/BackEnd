package com.pleiades.repository;

import com.pleiades.entity.store.search.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {

    Optional<Theme> findByName(String name);
}
