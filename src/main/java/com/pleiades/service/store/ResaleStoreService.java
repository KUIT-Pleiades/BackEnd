package com.pleiades.service.store;

import com.pleiades.dto.store.*;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final OwnershipRepository ownershipRepository;
    private final TheItemRepository theItemRepository;

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
        List<ItemTheme> itemThemes = itemThemeRepository.findByItemId(item.getId());
        List<String> themes = new ArrayList<>();

        for (ItemTheme itemTheme : itemThemes) themes.add(itemTheme.getTheme().getName());

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

    @Transactional
    public ValidationStatus addWishlist(String userId, Long itemId) {
        log.info("itemId: " + itemId + " userId: " + userId);

        if (resaleWishlistRepository.existsByUserIdAndResaleListingId(userId, itemId)) return ValidationStatus.DUPLICATE;

        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            ResaleListing item = resaleListingRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            ResaleWishlist newWishlist = ResaleWishlist.of(user, item);

            resaleWishlistRepository.save(newWishlist);
        } catch (CustomException e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }
        return ValidationStatus.VALID;
    }

    @Transactional
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

    @Transactional
    public Long buyItem(String userId, Long listingId) {
        // listing이 존재하는지
        // listing 상태가 onsale인지
        // 해당 Listing의 source 소유권의 주인이 현재 user가 아닌지
        // 돈 있는지 ㅋ
        ResaleListing listing = resaleListingRepository.findById(listingId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        if (listing.getStatus() != SaleStatus.ONSALE) throw new CustomException(ErrorCode.NOT_ONSALE);

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.equals(listing.getSourceOwnership().getUser())) throw new CustomException(ErrorCode.ALREADY_EXISTS);

        // 구매
        user.purchaseByStone(listing.getPrice());

        // 구매 후
        // source 소유권 active = false
        // listing 상태 sold
        listing.getSourceOwnership().sold();
        Ownership ownership = Ownership.resaleOf(user, listing);
        listing.sale(ownership);

        ownershipRepository.save(ownership);

        // 상대 자금 추가
        listing.getSourceOwnership().getUser().addStone(listing.getPrice());

        return ownership.getId();
    }

    public List<ListingDto> getListings(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<ResaleListing> listings = resaleListingRepository.findBySourceOwnershipUserIdAndSaleStatus(userId, SaleStatus.ONSALE);

        List<ListingDto> listingDtos = new ArrayList<>();

        for (ResaleListing lst : listings) {
            TheItem item = theItemRepository.findById(
                    lst.getSourceOwnership().getItem().getId()).orElseThrow(
                    () -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
            ItemType type = item.getType();
            listingDtos.add(
                    new ListingDto(
                            lst.getId(),
                            lst.getPrice(),
                            new ItemDto(item, type.getCategory(), type)));
        }

        return listingDtos;
    }

    @Transactional
    public Long addListing(String userId, Long ownershipId, Long price) {
        Ownership ownership = ownershipRepository.findById(ownershipId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!ownership.getUser().equals(user)) throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);

        resaleListingRepository.findBySourceOwnershipId(ownershipId).ifPresent((l) -> { throw new CustomException(ErrorCode.ALREADY_EXISTS); });

        ResaleListing listing = new ResaleListing(ownership, price);
        resaleListingRepository.save(listing);

        return listing.getId();
    }

    @Transactional
    public Long updateListing(String userId, Long ownershipId, Long price) {
        Ownership ownership = ownershipRepository.findById(ownershipId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!ownership.getUser().equals(user)) throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);

        ResaleListing listing = resaleListingRepository.findBySourceOwnershipId(ownershipId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        listing.updatePrice(price);

        return listing.getId();
    }

    @Transactional
    public void deleteListing(String userId, Long listingId) {
        // listing이 onsale일 때만 삭제
        // 내 listing인지 확인
        ResaleListing listing = resaleListingRepository.findById(listingId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!listing.getStatus().equals(SaleStatus.ONSALE)) throw new CustomException(ErrorCode.NOT_ONSALE);
        if (!listing.getSourceOwnership().getUser().equals(user)) throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);

        resaleListingRepository.delete(listing);
    }
}
