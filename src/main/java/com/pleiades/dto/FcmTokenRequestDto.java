package com.pleiades.dto;

import com.pleiades.strings.DeviceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmTokenRequestDto {
    private String token;
    private DeviceType deviceType;
}
