package com.pleiades.repository;

import com.pleiades.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, String> {

    boolean existsById(String stationId);

}
