package com.pleiades.service.store;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.StationBackground;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.repository.StarBackgroundRepository;
import com.pleiades.repository.StationBackgroundRepository;
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
    private final StarBackgroundRepository starBackgroundRepository;
    private final StationBackgroundRepository stationBackgroundRepository;
    private final OfficialWishlistRepository officialWishlistRepository;
    private final ItemThemeRepository itemThemeRepository;

    public List<TheItem> getFaceItems() {
        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);

        return itemRepository.findByTypes(types);
    }

    public List<TheItem> getFashionItems() {
        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);

        return itemRepository.findByTypes(types);
    }

//    public List<> getBgItems() {
//        List<StarBackground> backgrounds = starBackgroundRepository.findAll();
//        List<StationBackground> stations = stationBackgroundRepository.findAll();
//
//    }

    public List<TheItem> getFaceWishlistItems(String userid) {
        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);

        List<OfficialWishlist> wishlist = officialWishlistRepository.findByTypesInWishlist(types, userid);

        List<TheItem> items = new ArrayList<>();

        for (OfficialWishlist w : wishlist) {
            TheItem item = w.getItem();
            if (item == null) continue;
            items.add(item);
        }

        return items;
    }

    public OfficialItemDto itemToOfficialItemDto(TheItem item) {
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
