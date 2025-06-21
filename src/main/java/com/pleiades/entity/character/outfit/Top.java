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
@Table(name = "top")
public class Top {
    @Id
    private String name;
    private Long price = 0L;

//    @OneToOne(mappedBy = "top")
//    private Outfit outfit;
}
