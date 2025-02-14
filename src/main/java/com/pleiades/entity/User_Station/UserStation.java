package com.pleiades.entity.User_Station;

import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_station")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStation {

    @EmbeddedId
    private UserStationId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("stationId")
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean todayReport; // 오늘 리포트 작성 여부

    @Column(nullable = false)
    private float positionX;

    @Column(nullable = false)
    private float positionY;
}
