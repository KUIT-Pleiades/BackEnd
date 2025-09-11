package com.pleiades.entity.store;

import com.pleiades.strings.SaleStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
public class ResaleListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Column(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Ownership ownership;

    private Long price;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SaleStatus status;
}
