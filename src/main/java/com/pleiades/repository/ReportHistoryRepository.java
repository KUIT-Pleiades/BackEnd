package com.pleiades.repository;

import com.pleiades.entity.ReportHistory;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
    List<ReportHistory> findByUser(User user);
    Optional<ReportHistory> findById(Long id);
    Optional<ReportHistory> findByQuery(String query);
}
