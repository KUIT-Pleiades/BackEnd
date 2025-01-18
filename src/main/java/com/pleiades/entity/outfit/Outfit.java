package com.pleiades.entity.outfit;

import com.pleiades.dto.character.CharacterOutfitDto;
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
@Table(name = "outfits")
public class Outfit {
    @Id
    private String id;

//    @JoinColumn(name = "top_url")
    private String top;

//    @JoinColumn(name = "bottom_url")
    private String bottom;

//    @JoinColumn(name = "shoes_url")
    private String shoes;

    public void setOutfit(CharacterOutfitDto outfitDto) {
        this.setTop(outfitDto.getTopImg());
        this.setBottom(outfitDto.getBottomImg());
        this.setShoes(outfitDto.getShoesImg());
    }
}
