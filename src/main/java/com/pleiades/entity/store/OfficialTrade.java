package com.pleiades.entity.store;

import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
public class OfficialTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TheItem item;

    @OneToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User buyer;

    private LocalDateTime createdAt;

    private Long price;
}