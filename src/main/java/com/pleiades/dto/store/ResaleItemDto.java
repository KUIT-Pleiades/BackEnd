package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.entity.store.search.Theme;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.SaleStatus;
import lombok.*;

import java.util.List;

@Setter
@Getter
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
    private SaleStatus status;
    private List<String> theme;
}