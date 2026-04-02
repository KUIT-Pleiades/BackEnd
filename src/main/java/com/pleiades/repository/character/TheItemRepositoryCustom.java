package com.pleiades.repository.character;

import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemType;

import java.util.List;

public interface TheItemRepositoryCustom {
    List<TheItem> findNotBasicItemsByTypes(List<ItemType> types);
}
