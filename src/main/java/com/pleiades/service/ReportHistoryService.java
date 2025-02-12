package com.pleiades.service;

import com.pleiades.dto.ReportHistoryDto;
import com.pleiades.entity.ReportHistory;
import com.pleiades.entity.User;
import com.pleiades.repository.ReportHistoryRepository;
import com.pleiades.repository.ReportRepository;
import com.pleiades.strings.ValidationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReportHistoryService {
    ReportRepository reportRepository;
    ReportHistoryRepository reportHistoryRepository;

    @Autowired
    public ReportHistoryService(ReportRepository reportRepository, ReportHistoryRepository reportHistoryRepository) {
        this.reportRepository = reportRepository;
        this.reportHistoryRepository = reportHistoryRepository;
    }

    @Transactional
    public void saveReportHistory(String query, User user) {
        String noSpaceQuery = query.trim();
        Optional<ReportHistory> history = reportHistoryRepository.findByQuery(noSpaceQuery);
        if (history.isPresent()) {
            history.get().setCreatedAt(LocalDateTime.now());
            reportHistoryRepository.save(history.get());
        }
        ReportHistory newReportHistory = new ReportHistory();
        newReportHistory.setUser(user);
        newReportHistory.setQuery(query);
        newReportHistory.setCreatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteIfOverTen(User user) {
        if (reportHistoryRepository.findByUser(user).size() > 10) deleteOldReportHistory(user);
    }

    protected void deleteOldReportHistory(User user) {
        List<ReportHistory> reportHistories = reportHistoryRepository.findByUser(user);
        LocalDateTime oldest = LocalDateTime.now();
        ReportHistory oldestReportHistory = reportHistories.get(0);
        for (ReportHistory reportHistory : reportHistories) {
            if (reportHistory.getCreatedAt().isBefore(oldest)) {
                oldest = reportHistory.getCreatedAt();
                oldestReportHistory = reportHistory;
            }
        }
        reportHistoryRepository.delete(oldestReportHistory);
    }

    public ResponseEntity<Map<String,Object>> deleteById(Long id) {
        Map<String, Object> body = new HashMap<>();
        Optional<ReportHistory> reportHistory = reportHistoryRepository.findById(id);
        if (reportHistory.isEmpty()) {
            body.put("message", "No history with id " + id + " found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        reportHistoryRepository.delete(reportHistory.get());

        ReportHistoryDto reportHistoryDto = new ReportHistoryDto();
        reportHistoryDto.setId(id);
        reportHistoryDto.setQuery(reportHistory.get().getQuery());
        reportHistoryDto.setCreatedAt(reportHistory.get().getCreatedAt());

        body.put("history", reportHistoryDto);
        body.put("message", "Report history deleted");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
