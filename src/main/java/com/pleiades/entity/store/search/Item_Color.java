package com.pleiades.entity.store.search;

import com.pleiades.entity.character.TheItem;
import jakarta.persistence.*;

@Entity
public class Item_Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TheItem item;

    @ManyToOne
    private Color color;
}
