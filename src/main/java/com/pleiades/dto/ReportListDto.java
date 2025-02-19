package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportListDto extends ReportDto {
    @JsonProperty
    Boolean isTodayReport;

    public ReportListDto(ReportDto reportDto) {
        this.setReportId(reportDto.getReportId());
        this.setQuestionId(reportDto.getQuestionId());
        this.setQuestion(reportDto.getQuestion());
        this.setAnswer(reportDto.getAnswer());
        this.setCreatedAt(reportDto.getCreatedAt());
        this.setModifiedAt(reportDto.getModifiedAt());
    }
}
