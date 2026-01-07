package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)@JsonPropertyOrder({"id", "purchasedPrice", "isOfficial", "item"})

public class PurchaseOwnershipDto {
    private Long id;

    private Long purchasedPrice;

    @JsonProperty("isOfficial")
    private boolean isOfficial;

    private ItemBasicInfoDto item;
}
