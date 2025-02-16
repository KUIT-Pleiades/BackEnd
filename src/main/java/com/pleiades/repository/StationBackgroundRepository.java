package com.pleiades.repository;

import com.pleiades.entity.StationBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationBackgroundRepository extends JpaRepository<StationBackground, Long> {
    Optional<StationBackground> findById(Long id);
    Optional<StationBackground> findByName(String name);
}
