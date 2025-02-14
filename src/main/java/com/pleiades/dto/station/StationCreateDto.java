package com.pleiades.dto.station;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;

@Data
@AllArgsConstructor
public class StationCreateDto {
    private String backgroundName;
    private String name;
    private String intro;
    private Time reportNoticeTime;
}
