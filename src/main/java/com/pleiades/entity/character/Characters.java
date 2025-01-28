package com.pleiades.entity.character;

import com.pleiades.entity.User;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.outfit.Bottom;
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

    // todo: cascade 설정
    @OneToMany(mappedBy = "characters")
    private List<CharacterItem> characterItems;
}