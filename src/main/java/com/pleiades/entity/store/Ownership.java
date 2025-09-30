package com.pleiades.entity.store;

import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemSource;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
public class Ownership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TheItem item;

    private LocalDateTime purchasedAt = LocalDateTime.now();

    private String nft_id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemSource source;
}
