package com.pleiades.dto.station;

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
    private boolean isFriend;
}
