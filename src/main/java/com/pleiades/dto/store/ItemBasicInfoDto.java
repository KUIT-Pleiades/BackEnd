package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemCategory;
import com.pleiades.strings.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemBasicInfoDto {
    private Long id;
    private String name;
    private String description;
    private Long price;

    public ItemBasicInfoDto(TheItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
    }
}
