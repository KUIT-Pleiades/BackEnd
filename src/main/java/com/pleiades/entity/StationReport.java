package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "station_report")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Station station;

    @ManyToOne
    @JoinColumn(name = "report_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Report report;
}
