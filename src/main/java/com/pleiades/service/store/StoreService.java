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
//        themesDto.setResaleThemes(getResaleThemes());

        return themesDto;
    }

    public ThemesDto getOfficialThemes() {
        List<Theme> themes = themeRepository.findAll();
        ThemesDto themesDto = new ThemesDto();

        List<String> face = themes.stream()
                .filter((theme -> itemThemeRepository.existsByThemeId(theme.getId())))
                .filter((theme) -> theme.getName().startsWith("face"))
                .map((theme) -> theme.getName().replace("face ", "") ).toList();

        List<String> fashion = themes.stream()
                .filter((theme -> itemThemeRepository.existsByThemeId(theme.getId())))
                .filter((theme) -> theme.getName().startsWith("fashion"))
                .map((theme) -> theme.getName().replace("fashion ", "") ).toList();

        List<String> background = themes.stream()
                .filter((theme -> itemThemeRepository.existsByThemeId(theme.getId())))
                .filter((theme) -> theme.getName().startsWith("bg"))
                .map((theme) -> theme.getName().replace("bg ", "") ).toList();

        themesDto.setFace(face);
        themesDto.setFashion(fashion);
        themesDto.setBackground(background);

        return themesDto;
    }

//    public ThemesDto getResaleThemes() {
//        List<Theme> themes = themeRepository.findAll();
//        ThemesDto themesDto = new ThemesDto();
//        List<Theme> existingThemes = themes.stream()
//                .filter((t) -> resaleListingRepository.existsByItemTheme(t.getName()))
//                .toList();
//
//        List<String> face = existingThemes.stream()
//                .filter((theme -> itemThemeRepository.existsByThemeId(theme.getId())))
//                .filter((theme) -> theme.getName().startsWith("face"))
//                .map((theme) -> theme.getName().replace("face ", "") ).toList();
//
//        List<String> fashion = existingThemes.stream()
//                .filter((theme -> itemThemeRepository.existsByThemeId(theme.getId())))
//                .filter((theme) -> theme.getName().startsWith("fashion"))
//                .map((theme) -> theme.getName().replace("fashion ", "") ).toList();
//
//        List<String> background = existingThemes.stream()
//                .filter((theme -> itemThemeRepository.existsByThemeId(theme.getId())))
//                .filter((theme) -> theme.getName().startsWith("bg"))
//                .map((theme) -> theme.getName().replace("bg ", "") ).toList();
//
//        themesDto.setFace(face);
//        themesDto.setFashion(fashion);
//        themesDto.setBackground(background);
//
//        return themesDto;
//    }

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
