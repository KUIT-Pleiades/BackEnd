package com.pleiades.service.store;

import com.pleiades.dto.store.ItemDto;
import com.pleiades.dto.store.OfficialAndRestoreThemesDto;
import com.pleiades.dto.store.OwnershipDto;
import com.pleiades.dto.store.ThemesDto;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.ResaleListing;
import com.pleiades.entity.store.search.Theme;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.ThemeRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.repository.store.ResaleListingRepository;
import com.pleiades.repository.store.search.ItemThemeRepository;
import com.pleiades.strings.ItemCategory;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.SaleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {
    private final UserRepository userRepository;
    private final OwnershipRepository ownershipRepository;
    private final ThemeRepository themeRepository;
    private final ResaleListingRepository resaleListingRepository;
    private final ItemThemeRepository itemThemeRepository;

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

    public List<OwnershipDto> getMyItems(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Ownership> items = ownershipRepository.findByUserId(userId);

        return items
                .stream()
                .map(StoreService::ownershipToOwnershipDto)
                .toList();
    }

    public List<OwnershipDto> getAvailableToSaleItems(User user) {
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        List<Ownership> items = ownershipRepository.findByUserId(user.getId());

        return items.stream()
                .filter((o) -> !resaleListingRepository.existsBySourceOwnershipId(o.getId()))
                .map(StoreService::ownershipToOwnershipDto)
                .toList();
    }

    public List<OwnershipDto> getSoldItems(User user) {
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        List<ResaleListing> listings = resaleListingRepository.findBySourceOwnershipUserIdAndSaleStatus(user.getId(), SaleStatus.SOLDOUT);

        return listings
                .stream()
                .map(ResaleListing::getSourceOwnership)
                .map(StoreService::ownershipToOwnershipDto)
                .toList();
    }

    private static OwnershipDto ownershipToOwnershipDto(Ownership o) {
        TheItem item = o.getItem();
        ItemType type = item.getType();
        ItemCategory category = type.getCategory();

        return new OwnershipDto(
                o.getId(),
                new ItemDto(item, category, type)
        );
    }

}
