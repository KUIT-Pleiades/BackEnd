package com.pleiades.dto.station;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StationDto {
    private String stationId;
    private String name;
    private int numOfUsers;
    private String stationBackground;
    private LocalDateTime createdAt;
    private LocalDateTime lastActive;
    private boolean isFavorite;
}