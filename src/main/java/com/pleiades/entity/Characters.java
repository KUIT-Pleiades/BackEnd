package com.pleiades.entity;

import com.pleiades.entity.face.Expression;
import com.pleiades.entity.face.Face;
import com.pleiades.entity.face.Hair;
import com.pleiades.entity.face.Skin;
import com.pleiades.entity.item.Item;
import com.pleiades.entity.outfit.Bottom;
import com.pleiades.entity.outfit.Outfit;
import com.pleiades.entity.outfit.Shoes;
import com.pleiades.entity.outfit.Top;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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
    @JoinColumn(name="skin_id")
    private Skin skin;

    @ManyToOne
    @JoinColumn(name="expression_id")
    private Expression expression;

    @ManyToOne
    @JoinColumn(name="hair_id")
    private Hair hair;

    @ManyToOne
    @JoinColumn(name="top_id")
    private Top top;

    @ManyToOne
    @JoinColumn(name="bottom_id")
    private Bottom bottom;

    @ManyToOne
    @JoinColumn(name="shoes_id")
    private Shoes shoes;

    @ManyToMany
    @JoinColumn(name="item_id")
    private Set<Item> item;

}