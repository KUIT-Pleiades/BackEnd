package com.pleiades.repository;

import com.pleiades.entity.StationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationQuestionRepository extends JpaRepository<StationQuestion, Long> {
    public List<StationQuestion> findByStationId(String stationId);
    public List<StationQuestion> findByQuestionId(Long questionId);
}
