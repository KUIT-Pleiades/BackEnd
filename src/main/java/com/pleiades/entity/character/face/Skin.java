package com.pleiades.entity.character.face;

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
@Table(name = "skin")
public class Skin {

    @Id
    private String name;
    private Long price = 0L;

    @OneToOne(mappedBy = "skin")
    private Face face;
}
