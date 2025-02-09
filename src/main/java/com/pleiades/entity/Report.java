package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Column
    User user;

    @Column
    Long reportId;

    @ManyToOne
    @Column(nullable = false)
    Question question;

//    @ManyToOne
//    @Column(nullable = false)
//    Station station;

    @Column(nullable = false)
    String answer;

    @Column
    boolean written = false;

    @Column
    LocalDateTime createdAt = LocalDateTime.now();

    @Column
    LocalDateTime modifiedAt = LocalDateTime.now();
}
