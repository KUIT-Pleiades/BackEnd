package com.pleiades.entity;

import com.pleiades.dto.character.CharacterFaceDto;
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
@Table(name = "character")
public class Face {
    @Id
    private String id;

    private String skin;

    private String hair;

    private String expression;

    public void setFace(CharacterFaceDto faceDto) {
        this.setSkin(faceDto.getSkinImg().getUrl());
        this.setHair(faceDto.getHairImg().getUrl());
        this.setExpression(faceDto.getExpressionImg().getUrl());
    }
}