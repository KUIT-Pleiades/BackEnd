package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stars")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Star {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id; // User의 기본 키를 그대로 사용 (X)

    // @MapsId // User의 기본 키를 Star의 기본 키로 매핑
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "background_id")
    private StarBackground background;
}
