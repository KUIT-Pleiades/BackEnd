package com.pleiades.interceptor;

import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.UserStationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Order(2)
public class StationAuthInterceptor implements HandlerInterceptor {
    private UserRepository userRepository;
    private StationRepository stationRepository;
    private UserStationRepository userStationRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository, StationRepository stationRepository, UserStationRepository userStationRepository) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.userStationRepository = userStationRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod controller = null;
        if (handler instanceof HandlerMethod) controller = (HandlerMethod) handler;

        String email = (String) request.getAttribute("email");
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String stationPublicId = pathVariables.get("stationId");
        String stationCode = pathVariables.get("stationCode");

        if (stationCode != null) return true;
        if (stationPublicId == null || stationPublicId.isEmpty()) { throw new CustomException(ErrorCode.INVALID_STATION_ID); }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }

        Optional<Station> station = stationRepository.findByPublicId(UUID.fromString(stationPublicId));
        if (station.isEmpty()) { throw new CustomException(ErrorCode.STATION_NOT_FOUND); }

        UserStationId userStationId = new UserStationId(user.get().getId(), station.get().getId());
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);
        if (userStation.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_IN_STATION); }

        return true;
    }
}
