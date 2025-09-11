package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResaleStoreDto {
    private List<OfficialItemDto> items;
    private List<Long> wishlist;
}
