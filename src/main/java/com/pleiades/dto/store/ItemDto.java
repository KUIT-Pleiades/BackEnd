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
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private ItemCategory category;
    private ItemType type;

    public ItemDto(TheItem item, ItemCategory category, ItemType type) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.category = category;
        this.type = type;
    }
}
