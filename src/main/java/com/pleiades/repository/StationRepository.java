package com.pleiades.repository;

import com.pleiades.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    public Optional<Station> findById(Long id);
    public Optional<Station> findByPublicId(UUID id);

    boolean existsById(Long id);
    boolean existsByPublicId(UUID publicId);
    boolean existsByCode(String code);
}