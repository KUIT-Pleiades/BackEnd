package com.pleiades.entity.character;

import com.pleiades.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "characters")
public class Characters {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterItem> characterItems = new ArrayList<>();

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name="face_id")
//    private Face face;
//    // Item, Outfit

}