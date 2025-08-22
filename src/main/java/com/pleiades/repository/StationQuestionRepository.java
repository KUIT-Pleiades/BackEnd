package com.pleiades.repository;

import com.pleiades.entity.StationQuestion;
import com.pleiades.entity.StationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationQuestionRepository extends JpaRepository<StationQuestion, Long> {
    public List<StationQuestion> findByStationId(Long stationId);
    List<StationQuestion> findByStationPublicId(UUID stationPublicId);
    public List<StationQuestion> findByStationCode(String stationCode);
    public List<StationQuestion> findByQuestionId(Long questionId);

    @Query("SELECT sr FROM StationQuestion sr WHERE sr.station.id = :stationId AND sr.question.id = :questionId")
    Optional<StationQuestion> findByStationIdAndQuestionId(@Param("stationId") Long stationId, @Param("questionId") Long questionId);
}
