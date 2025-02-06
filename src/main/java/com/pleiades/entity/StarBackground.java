package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "background")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StarBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "background", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<Star> stars;
}
