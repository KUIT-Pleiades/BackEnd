package com.pleiades.service.store;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.entity.Star;
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

    public List<OfficialItemDto> getFaceItems() {
        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);

        List<TheItem> items = itemRepository.findByTypes(types);

        List<OfficialItemDto> dtos = new ArrayList<>();

        for (TheItem item : items) dtos.add(itemToOfficialItemDto(item));

        return dtos;
    }

    public List<OfficialItemDto> getFashionItems() {
        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);

        List<TheItem> items = itemRepository.findByTypes(types);

        List<OfficialItemDto> dtos = new ArrayList<>();

        for (TheItem item : items) dtos.add(itemToOfficialItemDto(item));

        return dtos;
    }

//    public List<StarBackground> getStarBgItems() {
//        List<StarBackground> backgrounds = starBackgroundRepository.findAll();
//
//
//    }
//
//    public List<StationBackground> getStationBgItems() {
//        List<StationBackground> stations = stationBackgroundRepository.findAll();
//
//
//
//    }

    public List<Long> getFaceWishlistItems(String userid) {
        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);

        List<OfficialWishlist> wishlist = officialWishlistRepository.findByTypesInWishlist(types, userid);

        List<TheItem> items = new ArrayList<>();

        for (OfficialWishlist w : wishlist) {
            TheItem item = w.getItem();
            if (item == null) continue;
            items.add(item);
        }

        return items.stream().map(TheItem::getId).toList();
    }

    public List<Long> getFashionWishlistItems(String userid) {
        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);

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
