package com.pleiades.service.report;

import com.pleiades.dto.ReportHistoryDto;
import com.pleiades.entity.ReportHistory;
import com.pleiades.entity.User;
import com.pleiades.repository.ReportHistoryRepository;
import com.pleiades.repository.ReportRepository;
import com.pleiades.util.LocalDateTimeUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ReportHistoryService.class);
    ReportRepository reportRepository;
    ReportHistoryRepository reportHistoryRepository;
    ModelMapper modelMapper;

    @Autowired
    public ReportHistoryService(ReportRepository reportRepository, ReportHistoryRepository reportHistoryRepository, ModelMapper modelMapper) {
        this.reportRepository = reportRepository;
        this.reportHistoryRepository = reportHistoryRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void saveReportHistory(String query, User user) {
        log.info("Saving report history");
        String noSpaceQuery = query.trim();
        Optional<ReportHistory> history = reportHistoryRepository.findByQuery(noSpaceQuery);
        if (history.isPresent()) {
            log.info("history exists");
            history.get().setCreatedAt(LocalDateTimeUtil.now());
            reportHistoryRepository.save(history.get());
            return;
        }
        ReportHistory newReportHistory = new ReportHistory();
        newReportHistory.setUser(user);
        newReportHistory.setQuery(query);
        newReportHistory.setCreatedAt(LocalDateTimeUtil.now());
        reportHistoryRepository.save(newReportHistory);
        log.info("report history: {}", newReportHistory);
    }

    @Transactional
    public void deleteIfOverTen(User user) {
        log.info("deleteIfOverTen");
        if (reportHistoryRepository.findByUser(user).size() > 10) deleteOldReportHistory(user);
    }

    protected void deleteOldReportHistory(User user) {
        log.info("deleteOldReportHistory");
        List<ReportHistory> reportHistories = reportHistoryRepository.findByUser(user);
        LocalDateTime oldest = LocalDateTimeUtil.now();
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

        ReportHistoryDto reportHistoryDto = modelMapper.map(reportHistory.get(), ReportHistoryDto.class);

        body.put("history", reportHistoryDto);
        body.put("message", "Report history deleted");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
