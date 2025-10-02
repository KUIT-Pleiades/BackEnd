package com.pleiades.service.store;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.store.OfficialWishlistRepository;
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.repository.store.search.ItemThemeRepository;
import com.pleiades.strings.ItemSource;
import com.pleiades.strings.ItemType;
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
public class OfficialStoreService {
    private final TheItemRepository itemRepository;
    private final OfficialWishlistRepository officialWishlistRepository;
    private final ItemThemeRepository itemThemeRepository;
    private final UserRepository userRepository;
    private final OwnershipRepository ownershipRepository;

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

    public ValidationStatus addWishlist(String userId, Long itemId) {
        log.info("itemId: " + itemId + " userId: " + userId);

        if (officialWishlistRepository.existsByUserIdAndItemId(userId, itemId)) return ValidationStatus.DUPLICATE;

        try {
            OfficialWishlist newWishlist = new OfficialWishlist();

            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            TheItem item = itemRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            newWishlist.setItem(item);
            newWishlist.setUser(user);
            officialWishlistRepository.save(newWishlist);
        } catch (CustomException e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }
        return ValidationStatus.VALID;
    }

    public ValidationStatus removeWishlist(String userId, Long itemId) {
        log.info("itemId: " + itemId + " userId: " + userId);

        Optional<OfficialWishlist> officialWishlist = officialWishlistRepository.findByUserIdAndItemId(userId, itemId);
        if (officialWishlist.isEmpty()) return ValidationStatus.DUPLICATE;

        try {
            officialWishlistRepository.delete(officialWishlist.get());
        } catch (CustomException e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }
        return ValidationStatus.VALID;
    }

    public ValidationStatus buyItem(String userId, Long itemId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            TheItem item = itemRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            ownershipRepository.f

            Ownership newOwnership = new Ownership();
            newOwnership.setUser(user);
            newOwnership.setItem(item);
            newOwnership.setSource(ItemSource.OFFICIAL);

            ownershipRepository.save(newOwnership);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }

        return ValidationStatus.VALID;
    }
}
