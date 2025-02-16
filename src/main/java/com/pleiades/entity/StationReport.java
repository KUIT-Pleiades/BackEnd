package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;

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
    Station station;

    @ManyToOne
    @JoinColumn(name = "report_id")
    Report report;
}
