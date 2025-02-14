package com.pleiades.repository;

import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStationRepository extends JpaRepository<UserStation, UserStationId> {

}
