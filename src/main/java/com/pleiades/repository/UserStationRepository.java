package com.pleiades.repository;

import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStationRepository extends JpaRepository<UserStation, UserStationId> {

    boolean existsById(UserStationId id);
    List<UserStation> findByStationId(String stationId);

    @Query("SELECT us FROM UserStation us " +
            "JOIN FETCH us.station s " +
            "WHERE us.user.id = :userId " +
            "ORDER BY s.createdAt DESC")
    List<UserStation> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

}
