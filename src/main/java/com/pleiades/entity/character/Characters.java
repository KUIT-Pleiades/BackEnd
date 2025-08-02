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

    // mappedBy: CharacterItem(Entity)의 field 이름 => 양방향 연관 관계
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterItem> characterItems = new ArrayList<>();

    public void addCharacterItem(CharacterItem characterItem) {
        this.characterItems.add(characterItem);
        characterItem.setCharacter(this); // 양방향 연관관계 동기화
    }
}