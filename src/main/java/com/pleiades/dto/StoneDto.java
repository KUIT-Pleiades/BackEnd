package com.pleiades.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
public class StoneDto {
    @Positive
    private Long addAmount;
}
