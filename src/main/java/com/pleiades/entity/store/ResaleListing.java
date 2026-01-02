package com.pleiades.entity.store;

import com.pleiades.strings.SaleStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "resale_listings")
public class ResaleListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "source_ownership_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Ownership sourceOwnership;

    @OneToOne
    @JoinColumn(name = "result_ownership_id", nullable = true)
    private Ownership resultOwnership;

    private Long price;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    public void sale(Ownership resultOwnership) {
        this.status = SaleStatus.SOLDOUT;
        this.resultOwnership = resultOwnership;
    }

    public ResaleListing(Ownership sourceOwnership) {
        this.sourceOwnership = sourceOwnership;
        this.price = sourceOwnership.getItem().getPrice();
        this.status = SaleStatus.ONSALE;
    }

    public ResaleListing(Ownership sourceOwnership, Long price) {
        this.sourceOwnership = sourceOwnership;
        this.price = price;
        this.status = SaleStatus.ONSALE;
    }
}
