package com.pleiades.entity.store.search;

import jakarta.persistence.*;

@Entity
public class Color_Synonyms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String synonyms;

    @ManyToOne
    private Color color;
}
