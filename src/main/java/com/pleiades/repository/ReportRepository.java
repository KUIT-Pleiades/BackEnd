package com.pleiades.repository;

import com.pleiades.entity.Question;
import com.pleiades.entity.Report;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findById(Long id);
    Optional<Report> findByQuestion(Question question);
    List<Report> findByUser(User user);
    List<Report> findByUserOrderByCreatedAtDesc(User user);

}
