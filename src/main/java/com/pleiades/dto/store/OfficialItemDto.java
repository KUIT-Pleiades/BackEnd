package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.strings.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficialItemDto {
    private Long id;
    private String name;
    private String description;
    private ItemType type;
    private Long price;
    private List<ItemTheme> theme;
}
