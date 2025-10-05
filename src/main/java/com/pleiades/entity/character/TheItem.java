package com.pleiades.entity.character;

import com.pleiades.entity.store.search.ItemColor;
import com.pleiades.entity.store.search.ItemKeyword;
import com.pleiades.entity.store.search.ItemTheme;
import com.pleiades.strings.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "the_items")
public class TheItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = false)
    private Long price = 0L;

    private String description;

    @Column(nullable = false)
    private boolean isRequired = false;

    @Column(nullable = false)
    private boolean isBasic = true;


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterItem> usedByCharacters = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemColor> itemColors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTheme> itemThemes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemKeyword> itemKeywords = new ArrayList<>();

}


