package com.pleiades.repository;

import com.pleiades.entity.StationQuestion;
import com.pleiades.entity.StationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationQuestionRepository extends JpaRepository<StationQuestion, Long> {
    public List<StationQuestion> findByStationId(String stationId);
    public List<StationQuestion> findByQuestionId(Long questionId);

    @Query("SELECT sr FROM StationQuestion sr WHERE sr.station.id = :stationId AND sr.question.id = :questionId")
    Optional<StationQuestion> findByStationIdAndQuestiontId(@Param("stationId") String stationId, @Param("questionId") Long questionId);
}
