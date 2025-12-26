package com.pleiades.service.store;

import com.pleiades.dto.store.ItemDto;
import com.pleiades.dto.store.OfficialAndRestoreThemesDto;
import com.pleiades.dto.store.OwnershipDto;
import com.pleiades.dto.store.ThemesDto;
import com.pleiades.entity.store.Ownership;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
//        themesDto.setResaleThemes(getResaleThemes());2

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

        return ownershipsToOwnershipDtos(items.stream());
    }

    public List<OwnershipDto> getAvailableToSaleItems(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Ownership> items = ownershipRepository.findByUserId(userId);

        return ownershipsToOwnershipDtos(items.stream()
                .filter((o) -> !resaleListingRepository.existsBySourceOwnershipId(o.getId())));
    }

    private List<OwnershipDto> ownershipsToOwnershipDtos(Stream<Ownership> ownerships) {
        return ownerships
                .map( (o) -> {
                    ItemType type = o.getItem().getType();
                    ItemCategory category = type.getCategory();

                    return new OwnershipDto(
                            o.getId(),
                            new ItemDto(o.getItem(), category, type)
                    );
                }).toList();
    }

}
