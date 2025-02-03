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

    @OneToOne
    @JoinColumn(name = "ears_name")
    private Ears ears;

    @OneToOne
    @JoinColumn(name = "eyes_name")
    private Eyes eyes;

    @OneToOne
    @JoinColumn(name = "head_name")
    private Head head;

    @OneToOne
    @JoinColumn(name = "leftHand_name")
    private LeftHand leftHand;

    @OneToOne
    @JoinColumn(name = "leftWrist_name")
    private LeftWrist leftWrist;

    @OneToOne
    @JoinColumn(name = "neck_name")
    private Neck neck;

    @OneToOne
    @JoinColumn(name = "rightHand_name")
    private RightHand rightHand;

    @OneToOne
    @JoinColumn(name = "rightWrist_name")
    private RightWrist rightWrist;
}

