package com.pleiades.service;

import com.pleiades.dto.store.ItemBasicInfoDto;
import com.pleiades.entity.User;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.strings.ItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final TheItemRepository theItemRepository;

    public List<ItemBasicInfoDto> getBackgroundsByType(User user, String type) {
        if (type.equals("station")) return getStationBackgrounds(user);
        else if (type.equals("star")) return getStarBackgrounds(user);
        else return List.of();
    }

    private List<ItemBasicInfoDto> getStationBackgrounds(User user) {
        return theItemRepository
                .findByUserAndType(user, ItemType.STATION_BG)
                .stream()
                .map(ItemBasicInfoDto::new)
                .toList();
    }

    private List<ItemBasicInfoDto> getStarBackgrounds(User user) {
        return theItemRepository
                .findByUserAndType(user, ItemType.STAR_BG)
                .stream()
                .map(ItemBasicInfoDto::new)
                .toList();
    }
}
