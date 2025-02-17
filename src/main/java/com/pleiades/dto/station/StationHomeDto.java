package com.pleiades.dto.station;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationHomeDto {
    private String stationId;
    private String adminUserId;
    private String name;
    private String intro;
    private int numOfUsers;
    private String stationBackground;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime reportNoticeTime;

    private boolean reportWritten;
    private List<StationMemberDto> stationMembers;
}