package com.pleiades.repository;

import com.pleiades.entity.Station;
import com.pleiades.entity.StationReport;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStationRepository extends JpaRepository<UserStation, UserStationId> {

    boolean existsById(UserStationId id);
    List<UserStation> findByStationId(Long stationId);
    List<UserStation> findByStationPublicId(UUID stationPublicId);
    List<UserStation> findByStationCode(String stationCode);

    int countByStationId(Long stationId);
    int countByStationPublicId(UUID station_publicId);

    @Query("SELECT us FROM UserStation us " +
            "JOIN FETCH us.station s " +
            "WHERE us.user.id = :userId " +
            "ORDER BY s.createdAt DESC")
    List<UserStation> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM UserStation us WHERE us.station.id = :stationId")
    void deleteAllByStationId(@Param("stationId") Long stationId);

    @Modifying
    @Query("DELETE FROM UserStation us WHERE us.station.publicId = :stationPublicId")
    void deleteAllByStationPublicId(@Param("stationPublicId") UUID stationId);

    @Query("SELECT sr FROM UserStation sr WHERE sr.station.id = :stationId AND sr.user.id = :userId")
    Optional<UserStation> findByStationIdAndUserId(@Param("stationId") Long stationId, @Param("userId") String userId);

    @Query("SELECT sr FROM UserStation sr WHERE sr.station.publicId = :stationPublicId AND sr.user.id = :userId")
    Optional<UserStation> findByStationPublicIdAndUserId(@Param("stationPublicId") UUID stationPublicId, @Param("userId") String userId);

    @Query("SELECT us.station FROM UserStation us WHERE us.user = :user AND us.isAdmin = true")
    List<Station> findStationsWhereUserIsAdmin(@Param("user") User user);

    void deleteAllByUser(User user);
}
