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
@Table(name = "hair")
public class Hair {
    @Id
    private String name;
    private Long price = 0L;

//    @OneToMany
//    @JoinColumn(name = "id")
//    private Face face;
}
