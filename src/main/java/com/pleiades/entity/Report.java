package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    @Column(nullable = true)
    String answer;

    @Column
    boolean written = false;

    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @Column
    LocalDateTime modifiedAt = LocalDateTime.now();
}
