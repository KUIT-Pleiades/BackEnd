package com.pleiades.service.store;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.store.OfficialWishlistRepository;
import com.pleiades.repository.store.search.ItemThemeRepository;
import com.pleiades.strings.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfficialStoreService {
    private final TheItemRepository itemRepository;
    private final OfficialWishlistRepository officialWishlistRepository;
    private final ItemThemeRepository itemThemeRepository;

    public List<OfficialItemDto> getOfficialItems(List<ItemType> types) {
        List<TheItem> items = itemRepository.findByTypeIn(types);

        List<OfficialItemDto> dtos = new ArrayList<>();

        for (TheItem item : items) dtos.add(itemToOfficialItemDto(item));

        return dtos;
    }

    public List<Long> getWishlistItems(List<ItemType> types, String userid) {
        List<OfficialWishlist> wishlist = officialWishlistRepository.findByTypesInWishlist(types, userid);

        List<TheItem> items = new ArrayList<>();

        for (OfficialWishlist w : wishlist) {
            TheItem item = w.getItem();
            if (item == null) continue;
            items.add(item);
        }

        return items.stream().map(TheItem::getId).toList();
    }

    private OfficialItemDto itemToOfficialItemDto(TheItem item) {
        List<ItemTheme> themes = itemThemeRepository.findByItemId(item.getId());

        return new OfficialItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getType(),
                item.getPrice(),
                themes
        );
    }
}
