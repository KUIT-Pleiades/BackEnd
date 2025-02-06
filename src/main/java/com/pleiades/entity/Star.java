package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="stars")
@Builder
public class Star {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // User의 기본 키를 그대로 사용 (X)

    // @MapsId // User의 기본 키를 Star의 기본 키로 매핑
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "background_id")
    private StarBackground background;
}
