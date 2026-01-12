package com.pleiades.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pleiades.entity.character.TheItem;
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
public class MyItemOwnershipDto {
    private Long id;
    private ItemDto item;

    public MyItemOwnershipDto(Long id, TheItem item) {
        ItemType type = item.getType();
        this.id = id;
        this.item = new ItemDto(item, type.getCategory(), type);
    }
}
