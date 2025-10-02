package com.pleiades.service.store;

import com.pleiades.dto.store.ListingPriceDto;
import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.dto.store.ResaleItemDto;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.ResaleListing;
import com.pleiades.entity.store.ResaleWishlist;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.repository.store.ResaleListingRepository;
import com.pleiades.repository.store.ResaleWishlistRepository;
import com.pleiades.repository.store.search.ItemThemeRepository;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.SaleStatus;
import com.pleiades.strings.ValidationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResaleStoreService {
    private final ResaleListingRepository resaleListingRepository;
    private final ResaleWishlistRepository resaleWishlistRepository;
    private final ItemThemeRepository itemThemeRepository;
    private final UserRepository userRepository;
    private final TheItemRepository itemRepository;

    public List<ResaleItemDto> getItems(List<ItemType> types) {
        List<ResaleListing> items = resaleListingRepository.findByTypes(types);

        List<ResaleItemDto> dtos = new ArrayList<>();

        for (ResaleListing item : items) dtos.add(itemToResaleItemDto(item));

        return dtos;
    }

    public List<Long> getWishlistItems(List<ItemType> types, String userid) {
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
        TheItem item = listing.getSourceOwnership().getItem();
        List<ItemTheme> themes = itemThemeRepository.findByItemId(item.getId());

        return new ResaleItemDto(
                listing.getId(),
                item.getName(),
                item.getDescription(),
                item.getType(),
                item.getPrice(),
                listing.getPrice(),
                listing.getStatus(),
                themes
        );
    }

    public ValidationStatus addWishlist(String userId, Long itemId) {
        log.info("itemId: " + itemId + " userId: " + userId);

        if (resaleWishlistRepository.existsByUserIdAndResaleListingId(userId, itemId)) return ValidationStatus.DUPLICATE;

        try {
            ResaleWishlist newWishlist = new ResaleWishlist();

            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            ResaleListing item = resaleListingRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            newWishlist.setResaleListing(item);
            newWishlist.setUser(user);
            resaleWishlistRepository.save(newWishlist);
        } catch (CustomException e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }
        return ValidationStatus.VALID;
    }

    public ValidationStatus removeWishlist(String userId, Long itemId) {
        log.info("itemId: " + itemId + " userId: " + userId);

        Optional<ResaleWishlist> officialWishlist = resaleWishlistRepository.findByUserIdAndResaleListingId(userId, itemId);
        if (officialWishlist.isEmpty()) return ValidationStatus.DUPLICATE;

        try {
            resaleWishlistRepository.delete(officialWishlist.get());
        } catch (CustomException e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }
        return ValidationStatus.VALID;
    }

    public List<ListingPriceDto> getListingsPrice(Long itemId) {
        TheItem item = itemRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        ItemType type = item.getType();
        List<ResaleListing> resaleListings = resaleListingRepository.findByTypes(List.of(type));

        List<ListingPriceDto> dtos = new ArrayList<>();

        for (ResaleListing listing : resaleListings) {
            ListingPriceDto priceDto = new ListingPriceDto();
            priceDto.setId(listing.getId());
            priceDto.setPrice(item.getPrice());
            priceDto.setDiscountedPrice(listing.getPrice());

            dtos.add(priceDto);
        }

        return dtos;
    }
}
