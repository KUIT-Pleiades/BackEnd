package com.pleiades.service.store;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.dto.store.ResaleItemDto;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.ResaleListing;
import com.pleiades.entity.store.ResaleWishlist;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.repository.store.ResaleListingRepository;
import com.pleiades.repository.store.ResaleWishlistRepository;
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
public class ResaleStoreService {
    private final ResaleListingRepository resaleListingRepository;
//    private final StarBackgroundRepository starBackgroundRepository;
//    private final StationBackgroundRepository stationBackgroundRepository;
    private final ResaleWishlistRepository resaleWishlistRepository;
    private final ItemThemeRepository itemThemeRepository;

    public List<ResaleItemDto> getFaceItems() {
        List<ItemType> types = List.of(ItemType.SKIN_COLOR, ItemType.HAIR, ItemType.EYES, ItemType.NOSE, ItemType.MOUTH, ItemType.MOLE);

        List<ResaleListing> items = resaleListingRepository.findByTypes(types);

        List<ResaleItemDto> dtos = new ArrayList<>();

        for (ResaleListing item : items) dtos.add(itemToResaleItemDto(item));

        return dtos;
    }

    public List<ResaleItemDto> getFashionItems() {
        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);

        List<ResaleListing> items = resaleListingRepository.findByTypes(types);

        List<ResaleItemDto> dtos = new ArrayList<>();

        for (ResaleListing item : items) dtos.add(itemToResaleItemDto(item));

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

        List<ResaleWishlist> wishlist = resaleWishlistRepository.findByTypesInWishlist(types, userid);

        List<Long> listingIds = new ArrayList<>();

        for (ResaleWishlist w : wishlist) {
            ResaleListing item = w.getResaleListing();
            if (item == null) continue;
            listingIds.add(item.getId());
        }

        return listingIds;
    }

    public List<Long> getFashionWishlistItems(String userid) {
        List<ItemType> types = List.of(ItemType.TOP, ItemType.BOTTOM, ItemType.SET, ItemType.SHOES);

        List<ResaleWishlist> wishlist = resaleWishlistRepository.findByTypesInWishlist(types, userid);

        List<Long> listingIds = new ArrayList<>();

        for (ResaleWishlist w : wishlist) {
            ResaleListing item = w.getResaleListing();
            if (item == null) continue;
            listingIds.add(item.getId());
        }

        return listingIds;
    }

    public ResaleItemDto itemToResaleItemDto(ResaleListing listing) {
        TheItem item = listing.getOwnership().getItem();
        List<ItemTheme> themes = itemThemeRepository.findByItemId(item.getId());

        return new ResaleItemDto(
                listing.getId(),
                item.getName(),
                item.getDescription(),
                item.getType(),
                item.getPrice(),
                listing.getPrice(),
                themes
        );
    }
}
