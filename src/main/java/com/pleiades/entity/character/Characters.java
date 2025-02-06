package com.pleiades.entity.character;

import com.pleiades.entity.User;
import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Face;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.outfit.Bottom;
import com.pleiades.entity.character.outfit.Outfit;
import com.pleiades.entity.character.outfit.Shoes;
import com.pleiades.entity.character.outfit.Top;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="face_id")
    private Face face;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="item_id")
    private Item item;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="outfit_id")
    private Outfit outfit;

}