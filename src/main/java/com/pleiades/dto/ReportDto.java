package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDto {
    @JsonProperty
    private Long id;

    @JsonProperty
    private Long questionId;

    @JsonProperty
    private String question;

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime modifiedAt;

    @JsonProperty
    private String answer;
}
