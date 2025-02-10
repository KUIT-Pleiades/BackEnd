package com.pleiades.entity.character.Item;

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
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "item")
    Characters characters;

    @ManyToOne
    @JoinColumn(name = "ears_name", unique = false)
    private Ears ears;

    @ManyToOne
    @JoinColumn(name = "eyes_name", unique = false)
    private Eyes eyes;

    @ManyToOne
    @JoinColumn(name = "head_name", unique = false)
    private Head head;

    @ManyToOne
    @JoinColumn(name = "leftHand_name", unique = false)
    private LeftHand leftHand;

    @ManyToOne
    @JoinColumn(name = "leftWrist_name", unique = false)
    private LeftWrist leftWrist;

    @ManyToOne
    @JoinColumn(name = "neck_name", unique = false)
    private Neck neck;

    @ManyToOne
    @JoinColumn(name = "rightHand_name", unique = false)
    private RightHand rightHand;

    @ManyToOne
    @JoinColumn(name = "rightWrist_name", unique = false)
    private RightWrist rightWrist;
}

