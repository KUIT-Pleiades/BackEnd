package com.pleiades.entity.character.Item;

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
@Table(name = "neck")
public class Neck {
    @Id
    private String name;
    private Long price = 0L;
}
