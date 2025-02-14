package com.pleiades.service;

import com.pleiades.dto.station.StationDto;
import com.pleiades.dto.station.StationListDto;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.UserStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStationService {

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;

    @Transactional
    public StationListDto getStationList(String email) {
        User currentUser = getUserByEmail(email);

        List<UserStation> userStations = userStationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        List<StationDto> stationDtos = userStations.stream()
                .map(userStation -> {
                    Station station = userStation.getStation();
                    return new StationDto(
                            station.getId(),
                            station.getName(),
                            station.getNumberOfUsers(),
                            station.getBackgroundName()
                    );
                })
                .collect(Collectors.toList());

        return new StationListDto(stationDtos);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_EMAIL, "login token expired"));
    }
}
