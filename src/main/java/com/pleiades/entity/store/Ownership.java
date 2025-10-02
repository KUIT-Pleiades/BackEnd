package com.pleiades.entity.store;

import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemSource;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime purchasedAt = LocalDateTime.now();

    @Column(nullable = false, updatable = false)
    private Long purchasedPrice;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ItemSource source;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean active = true;

    private Ownership(User user, TheItem item, ItemSource source, Long purchasedPrice) {
        this.user = user;
        this.item = item;
        this.source = source;
        this.purchasedPrice = purchasedPrice;
        this.purchasedAt = LocalDateTime.now();
    }

    public static Ownership officialOf(User user, TheItem item) {
        return new Ownership(user, item, ItemSource.OFFICIAL, item.getPrice());
    }

    public static Ownership officialOf(User user, TheItem item, Long price) {
        return new Ownership(user, item, ItemSource.OFFICIAL, price);
    }

    public static Ownership resaleOf(User user, TheItem item, Long price) {
        return new Ownership(user, item, ItemSource.RESALE, price);
    }
}
