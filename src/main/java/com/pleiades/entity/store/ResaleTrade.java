package com.pleiades.entity.store;

import com.pleiades.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
public class ResaleTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private ResaleListing resaleListing;

    @OneToOne
//    @OnDelete(action = OnDeleteAction.CASCADE): 판매자 측에 기록해둬야할 것 같아, 구매자가 탈퇴해도 남아있음
    private User buyer;

    private LocalDateTime createdAt = LocalDateTime.now();
}
