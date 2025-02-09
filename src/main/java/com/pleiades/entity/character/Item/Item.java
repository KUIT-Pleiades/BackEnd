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
    @JoinColumn(name = "ears_name")
    private Ears ears;

    @ManyToOne
    @JoinColumn(name = "eyes_name")
    private Eyes eyes;

    @ManyToOne
    @JoinColumn(name = "head_name")
    private Head head;

    @ManyToOne
    @JoinColumn(name = "leftHand_name")
    private LeftHand leftHand;

    @ManyToOne
    @JoinColumn(name = "leftWrist_name")
    private LeftWrist leftWrist;

    @ManyToOne
    @JoinColumn(name = "neck_name")
    private Neck neck;

    @ManyToOne
    @JoinColumn(name = "rightHand_name")
    private RightHand rightHand;

    @ManyToOne
    @JoinColumn(name = "rightWrist_name")
    private RightWrist rightWrist;
}

