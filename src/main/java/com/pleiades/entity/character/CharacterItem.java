package com.pleiades.entity.character;

import com.pleiades.entity.character.Item.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Character_Item")
public class CharacterItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "characters_id")
    private Characters characters;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
