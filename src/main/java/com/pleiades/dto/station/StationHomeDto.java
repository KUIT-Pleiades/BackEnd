package com.pleiades.dto.station;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;
import java.util.List;

@Data
@AllArgsConstructor
public class StationHomeDto {
    private String stationId;
    private String adminUserId;
    private String name;
    private String intro;
    private int numOfUsers;
    private String backgroundImg;
    private Time reportNoticeTime;
    private boolean reportWritten;
    private List<StationMemberDto> stationMembers;
}