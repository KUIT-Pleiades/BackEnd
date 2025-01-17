package com.pleiades.entity;

import com.pleiades.entity.face.Face;
import com.pleiades.entity.item.Item;
import com.pleiades.entity.outfit.Outfit;
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
@Table(name = "characters")
public class Characters {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="face_id")
    private Face face;

    @ManyToOne
    @JoinColumn(name="outfit_id")
    private Outfit outfit;

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;

    public static class SkinColor {
    }
}