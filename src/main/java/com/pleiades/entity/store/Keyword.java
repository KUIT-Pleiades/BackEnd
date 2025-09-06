package com.pleiades.entity.store;

import com.pleiades.entity.character.TheItem;
import jakarta.persistence.*;

@Entity
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @ManyToOne
    private TheItem item;
}
