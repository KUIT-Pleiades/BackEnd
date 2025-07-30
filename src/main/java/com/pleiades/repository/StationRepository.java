package com.pleiades.repository;

import com.pleiades.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, String> {
    public Optional<Station> findById(String id);

    boolean existsById(String stationId);
    boolean existsByCode(String code);

}
