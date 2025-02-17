package com.pleiades.dto.station;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationDto {
    private String stationId;
    private String name;
    private int numOfUsers;
    private String stationBackground;
}