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
        if(request.getMethod().equals("OPTIONS")) { return true; }
        
        String email = (String) request.getAttribute("email");

        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String stationPublicId = pathVariables.get("stationId");
        String stationCode = pathVariables.get("stationCode");

        if (stationCode != null) return true;
        if (stationPublicId == null || stationPublicId.isEmpty()) {
            log.error("stationId is null or empty");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid station id");
            return false;
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User is empty or not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return false;
        }

        Optional<Station> station = stationRepository.findByPublicId(UUID.fromString(stationPublicId));
        if (station.isEmpty()) {
            log.error("Station is null or empty");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Station not found");
            return false;
        }

        UserStationId userStationId = new UserStationId(user.get().getId(), station.get().getId());
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);
        if (userStation.isEmpty()) {
            log.error("UserStation is null or not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "UserStation not found");
            return false;
        }

        return true;
    }
}
