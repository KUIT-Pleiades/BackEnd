package com.pleiades.dto.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationMemberDto {
    private String userId;
    private String userName;
    private String profile;
    private String character;
    private float positionX;
    private float positionY;
    private boolean todayReport;
    @JsonProperty("isFriend")
    private boolean isFriend;
}
