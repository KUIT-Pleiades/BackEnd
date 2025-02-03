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
@Table(name = "right_wrist")
public class RightWrist {
    @Id
    private String name;
    private Long price = 0L;
}
