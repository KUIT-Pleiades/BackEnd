package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseResponseDto {
    private Long ownershipId;
    private String message;

    public static PurchaseResponseDto successOf(Long ownershipId) {
        return new PurchaseResponseDto(ownershipId, "Purchase Succeed");
    }

    public static PurchaseResponseDto failedOf() {
        return new PurchaseResponseDto(null, "Purchase Failed");
    }
}
