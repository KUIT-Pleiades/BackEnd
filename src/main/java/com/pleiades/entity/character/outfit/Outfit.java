package com.pleiades.entity.character.outfit;

import com.pleiades.entity.character.Characters;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outfit")
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "top_name", unique = false)
    private Top top;

    @ManyToOne
    @JoinColumn(name = "bottom_name", unique = false)
    private Bottom bottom;

    @ManyToOne
    @JoinColumn(name = "shoes_name", unique = false)
    private Shoes shoes;

    @OneToOne(mappedBy = "outfit")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Characters characters;
}
