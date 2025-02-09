package com.pleiades.entity.character.outfit;

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
@Table(name = "shoes")
public class Shoes {
    @Id
    private String name;
    private Long price = 0L;

//    @OneToOne(mappedBy = "shoes")
//    private Outfit outfit;
}
