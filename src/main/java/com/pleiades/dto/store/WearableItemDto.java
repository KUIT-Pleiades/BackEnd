package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pleiades.entity.character.TheItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WearableItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean isBasic;

    public WearableItemDto(TheItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.isBasic = true;
    }
}
