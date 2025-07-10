package com.pleiades.entity.character;

import com.pleiades.strings.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "the_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    private Long price = 0L;

    private boolean isRequired = false;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterItem> usedByCharacters = new ArrayList<>();

}


