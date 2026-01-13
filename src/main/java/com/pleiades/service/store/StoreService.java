package com.pleiades.service.store;

import com.pleiades.dto.store.*;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.ResaleListing;
import com.pleiades.entity.store.search.Theme;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.ThemeRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.repository.store.ResaleListingRepository;
import com.pleiades.repository.store.search.ItemThemeRepository;
import com.pleiades.strings.ItemCategory;
import com.pleiades.strings.ItemSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {
    private final UserRepository userRepository;
    private final OwnershipRepository ownershipRepository;
    private final ThemeRepository themeRepository;
    private final ResaleListingRepository resaleListingRepository;
    private final ItemThemeRepository itemThemeRepository;
    private final TheItemRepository theItemRepository;

    public OfficialAndRestoreThemesDto getThemes() {
        OfficialAndRestoreThemesDto themesDto = new OfficialAndRestoreThemesDto();
        themesDto.setOfficialThemes(getOfficialThemes());
        themesDto.setResaleThemes(getResaleThemes());

        return themesDto;
    }

    public ThemesDto getOfficialThemes() {
        List<Theme> themes = themeRepository.findAll();

        List<String> face = new ArrayList<>();
        List<String> fashion = new ArrayList<>();
        List<String> background = new ArrayList<>();

        for (Theme theme : themes) {
            String name = theme.getName();
            if (!itemThemeRepository.existsByThemeId(theme.getId())) continue;

            if (name.startsWith("face ")) face.add(name.replace("face ", ""));
            else if (name.startsWith("fashion ")) fashion.add(name.replace("fashion ", ""));
            else if (name.startsWith("bg ")) background.add(name.replace("bg ", ""));
        }

        return new ThemesDto(face, fashion, background);
    }

    public ThemesDto getResaleThemes() {
        List<Theme> themes = themeRepository.findThemesWithResaleListings();

        List<String> face = new ArrayList<>();
        List<String> fashion = new ArrayList<>();
        List<String> background = new ArrayList<>();

        for (Theme theme : themes) {
            String name = theme.getName();

            if (name.startsWith("face ")) face.add(name.replace("face ", ""));
            else if (name.startsWith("fashion ")) fashion.add(name.replace("fashion ", ""));
            else if (name.startsWith("bg ")) background.add(name.replace("bg ", ""));
        }

        return new ThemesDto(face, fashion, background);
    }

    public MyItemsDto getMySellableItems(User user) {
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        List<Ownership> ownerships = ownershipRepository.findSellableItemsByUserId(user.getId());

        List<MyItemOwnershipDto> myItem = ownerships
                .stream()
                .map(o -> new MyItemOwnershipDto(o.getId(), o.getItem()))
                .toList();

        return new MyItemsDto(myItem);
    }

    public CharacterWearableItemsDto getWearableItems(User user) {
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        List<TheItem> items = theItemRepository.findWearableItems(user.getId());

        List<WearableItemDto> face = new ArrayList<>();
        List<WearableItemDto> fashion = new ArrayList<>();

        items.forEach(i -> {
                    if (i.getType().getCategory().equals(ItemCategory.FACE)) face.add(new WearableItemDto(i));
                    else if (i.getType().getCategory().equals(ItemCategory.FASHION)) fashion.add(new WearableItemDto(i));
                });

        CharacterWearableItemsDto characterWearableItemsDto = new CharacterWearableItemsDto();
        characterWearableItemsDto.setFaceItems(face);
        characterWearableItemsDto.setFashionItems(fashion);

        return characterWearableItemsDto;
    }

    public PurchasesCountResponseDto getMyItemsByDate(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Ownership> ownerships = ownershipRepository.findOwnershipByUserIdAndIsActiveGroupedByCreatedDate(userId, true);
        Long count = (long) ownerships.size();

        Map<LocalDate, List<PurchaseOwnershipDto>> groupedByDate = ownerships
                .stream()
                .collect(Collectors.groupingBy(
                        o -> o.getPurchasedAt().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                StoreService::ownershipToPurchaseOwnershipDto,
                                toList()
                        )
                ));

        return new PurchasesCountResponseDto(count,
                    groupedByDate
                            .entrySet()
                            .stream()
                            .map(e -> new PurchasesResponseDto(e.getKey(), e.getValue()))
                            .toList()
                );
    }

    public SalesCountResponseDto getSoldItems(User user) {
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        // 비활성화된 소유권을 찾을까 했는데, 다른 이유로 비활성화되는 경우도 있을 것 같아서 매물 기준으로 함
        List<ResaleListing> listings = resaleListingRepository.findSoldListingsBySourceOwnershipUserIdWithResultOwnershipAndItem(user.getId());
        Long count = (long) listings.size();

        Map<LocalDate, List<SaleOwnershipDto>> groupedByDate = listings
                .stream()
                .collect(Collectors.groupingBy(
                        l -> l.getResultOwnership().getPurchasedAt().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                StoreService::listingToSaleOwnershipDto,
                                toList()
                        )
                ));

        return new SalesCountResponseDto(count,
                groupedByDate
                        .entrySet()
                        .stream()
                        .map(e -> new SalesResponseDto(e.getKey(), e.getValue()))
                        .toList()
        );
    }

    private static PurchaseOwnershipDto ownershipToPurchaseOwnershipDto(Ownership o) {
        TheItem item = o.getItem();

        return new PurchaseOwnershipDto(
                o.getId(),
                o.getPurchasedPrice(),
                o.getSource() == ItemSource.OFFICIAL,
                new ItemBasicInfoDto(item)
        );
    }

    private static SaleOwnershipDto listingToSaleOwnershipDto(ResaleListing l) {
        TheItem item = l.getSourceOwnership().getItem();

        return new SaleOwnershipDto(
                l.getSourceOwnership().getId(),
                l.getPrice(),
                new ItemBasicInfoDto(item)
        );
    }
}
