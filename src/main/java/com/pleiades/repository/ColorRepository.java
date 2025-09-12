package com.pleiades.repository;

import com.pleiades.entity.store.search.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findByName(String name);
}
