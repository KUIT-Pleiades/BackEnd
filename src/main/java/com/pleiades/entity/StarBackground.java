package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "star_backgrounds")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StarBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Long price = 0L;
}
