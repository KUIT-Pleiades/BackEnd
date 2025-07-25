package com.pleiades.entity.character;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "character_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"character", "item"})
public class CharacterItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Characters character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private TheItem item;

    public CharacterItem(Characters character, TheItem item) {
        this.character = character;
        this.item = item;
    }
}
