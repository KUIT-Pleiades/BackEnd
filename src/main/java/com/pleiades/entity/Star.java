package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "star")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Star {
    @Id
    private Long id; // User의 기본 키를 그대로 사용

    @OneToOne
    @MapsId // User의 기본 키를 Star의 기본 키로 매핑
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "starbackground_id")
    private StarBackground background;
}
