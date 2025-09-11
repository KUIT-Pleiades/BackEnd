package com.pleiades.entity.store.search;

import jakarta.persistence.*;

@Entity
public class ColorSynonyms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String synonyms;

    @ManyToOne
    private Color color;
}
