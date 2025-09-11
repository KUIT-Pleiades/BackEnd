package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.strings.ItemType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResaleItemDto {
    private Long id;
    private String name;
    private String description;
    private ItemType type;
    private Long price;
    private Long discounted_price;
    private List<ItemTheme> theme;
}