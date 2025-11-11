package com.pleiades.service.store;

import com.pleiades.dto.store.OwnershipDto;
import com.pleiades.dto.store.ThemesDto;
import com.pleiades.entity.User;
import com.pleiades.entity.store.Ownership;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.entity.store.search.Theme;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.ThemeRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.repository.store.search.ItemThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {
    private final TheItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OwnershipRepository ownershipRepository;
    private final ItemThemeRepository itemThemeRepository;

    public ThemesDto getThemes() {
        List<ItemTheme> themes = itemThemeRepository.findAll();
        ThemesDto themesDto = new ThemesDto();

        themesDto.setThemes(themes.stream().map((theme) -> theme.getTheme().getName()).toList());

        return themesDto;
    }

    public List<OwnershipDto> getMyItems(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Ownership> items = ownershipRepository.findByUserId(userId);

        return items.stream().map( (o) -> new OwnershipDto(o.getId(), o.getItem().getId()) ).toList();
    }
}
