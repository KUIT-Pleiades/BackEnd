package com.pleiades.dto.station;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationSettingDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("intro")
    private String intro;
    @JsonProperty("reportNoticeTime")
    private Time reportNoticeTime;
}
