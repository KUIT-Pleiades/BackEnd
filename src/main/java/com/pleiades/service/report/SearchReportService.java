package com.pleiades.service.report;

import com.pleiades.dto.ReportDto;
import com.pleiades.entity.Report;
import com.pleiades.entity.User;
import com.pleiades.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Service
@Slf4j
@RequiredArgsConstructor
public class SearchReportService {
    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;

    public Set<ReportDto> searchResult(User user, String query) {
        log.info("Searching result for query {}", query);
        List<ReportDto> questions = searchByQuestion(user, query);
        List<ReportDto> answers = searchByAnswer(user, query);

        Set<ReportDto> result = new HashSet<>(questions);
        result.addAll(answers);

        log.info("result: {}", result);

        return result;
    }

    private List<ReportDto> searchByQuestion(User user, String query) {
        log.info("searchByQuestion");
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getQuestion().getQuestion().contains(query)) {
                ReportDto reportDto = modelMapper.map(report, ReportDto.class);
                reportDtos.add(reportDto);
            }
        }
        return reportDtos;
    }

    private List<ReportDto> searchByAnswer(User user, String query) {
        log.info("searchByAnswer");
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getAnswer() == null) continue;
            if (report.getAnswer().contains(query)) {
                ReportDto reportDto = modelMapper.map(report, ReportDto.class);
                reportDtos.add(reportDto);
            }
        }
        return reportDtos;
    }
}
