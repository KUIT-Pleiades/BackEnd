package com.pleiades.service.store;

import com.pleiades.dto.store.OfficialItemDto;
import com.pleiades.dto.store.OfficialStoreDto;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.OfficialWishlist;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.entity.store.search.Theme;
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
import org.springframework.transaction.annotation.Transactional;

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
        List<TheItem> items = itemRepository.findByTypes(types);

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
        List<ItemTheme> itemThemes = itemThemeRepository.findByItemId(item.getId());
        List<String> themes = new ArrayList<>();

        for (ItemTheme iT : itemThemes) {
            String theme = iT.getTheme().getName();
            int idx = theme.indexOf(" ");

            if (idx == -1) themes.add(theme);
            else themes.add(theme.substring(idx + 1));
        }

        return new OfficialItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getType(),
                item.getPrice(),
                themes
        );
    }

    @Transactional
    public ValidationStatus addWishlist(String userId, Long itemId) {
        log.info("itemId: " + itemId + " userId: " + userId);

        if (officialWishlistRepository.existsByUserIdAndItemId(userId, itemId)) return ValidationStatus.DUPLICATE;

        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            TheItem item = itemRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            OfficialWishlist newWishlist = OfficialWishlist.of(user, item);

            officialWishlistRepository.save(newWishlist);
        } catch (CustomException e) {
            log.error(e.getMessage());
            return ValidationStatus.NOT_VALID;
        }
        return ValidationStatus.VALID;
    }

    @Transactional
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

    @Transactional
    public Long buyItem(String userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        TheItem item = itemRepository.findById(itemId).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (ownershipRepository.existsByUserIdAndItemId(user.getId(), item.getId())) throw new CustomException(ErrorCode.ALREADY_EXISTS);

        user.purchaseByStone(item.getPrice());

        Ownership newOwnership = Ownership.officialOf(user, item);

        ownershipRepository.save(newOwnership);

        return newOwnership.getId();
    }

    // Official Item&Store Dto, itemToOfficialItemDto 기존꺼 재사용함
    @Transactional
    public OfficialStoreDto searchOfficialStore(String query, String userId) {

        List<TheItem> items = itemRepository.searchOfficialItems(query);

        List<OfficialItemDto> dtos = items.stream()
                .map(this::itemToOfficialItemDto)
                .toList();

        List<Long> wishlist = officialWishlistRepository.findAllWishlistItemIds(userId);

        return new OfficialStoreDto(dtos, wishlist);
    }

}
