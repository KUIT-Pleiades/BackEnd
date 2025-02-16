package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "station_question", uniqueConstraints = @UniqueConstraint(columnNames = {"station", "question"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    Station station;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    @Column
    LocalDate createdAt;
}
