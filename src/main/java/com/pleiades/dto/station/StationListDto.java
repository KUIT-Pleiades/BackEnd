package com.pleiades.dto.station;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StationListDto {
    private List<StationDto> stations;
}