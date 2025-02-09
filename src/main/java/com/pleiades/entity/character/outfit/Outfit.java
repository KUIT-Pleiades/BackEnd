package com.pleiades.entity.character.outfit;

import com.pleiades.entity.character.Characters;
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
@Table(name = "outfit")
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "top_name")
    private Top top;

    @ManyToOne
    @JoinColumn(name = "bottom_name")
    private Bottom bottom;

    @ManyToOne
    @JoinColumn(name = "shoes_name")
    private Shoes shoes;

    @OneToOne(mappedBy = "outfit")
    Characters characters;
}
