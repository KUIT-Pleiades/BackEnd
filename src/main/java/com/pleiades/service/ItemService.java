package com.pleiades.service;

import com.pleiades.dto.store.ItemBasicInfoDto;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.strings.ItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final TheItemRepository theItemRepository;
    private final UserRepository userRepository;

    public List<ItemBasicInfoDto> getBackgroundsByType(String email, String type) {
        Optional<User> user = userRepository.findByEmail(email);

        String userId = user.isPresent() ? user.get().getId() : "";

        if (type.equals("station")) return getStationBackgrounds(userId);
        else if (type.equals("star")) return getStarBackgrounds(userId);
        else return List.of();
    }

    private List<ItemBasicInfoDto> getStationBackgrounds(String userId) {
        return theItemRepository
                .findByUserIdAndType(userId, ItemType.STATION_BG)
                .stream()
                .map(ItemBasicInfoDto::new)
                .toList();
    }

    private List<ItemBasicInfoDto> getStarBackgrounds(String userId) {
        return theItemRepository
                .findByUserIdAndType(userId, ItemType.STAR_BG)
                .stream()
                .map(ItemBasicInfoDto::new)
                .toList();
    }
}
