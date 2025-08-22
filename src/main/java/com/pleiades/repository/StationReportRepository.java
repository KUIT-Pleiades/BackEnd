package com.pleiades.repository;

import com.pleiades.entity.StationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationReportRepository extends JpaRepository<StationReport, Long> {
    List<StationReport> findByStationId(Long stationId);
    List<StationReport> findByStationPublicId(UUID stationPublicId);
    List<StationReport> findByStationCode(String stationCode);
    List<StationReport> findByReportId(Long reportId);

    @Query("SELECT sr FROM StationReport sr WHERE sr.station.id = :stationId AND sr.report.id = :reportId")
    Optional<StationReport> findByStationIdAndReportId(@Param("stationId") Long stationId, @Param("reportId") Long reportId);
}
