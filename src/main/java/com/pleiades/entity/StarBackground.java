package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "background")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StarBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
