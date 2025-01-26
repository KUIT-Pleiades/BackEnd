package com.pleiades.entity.face;

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
@Table(name = "faces")
public class Face {
    @Id
    private String id;

    private String skin;

    private String hair;

    private String expression;

    //todo: 요청 dto랑 응답 dto랑 분리
    public void setFace(CharacterFaceDto faceDto) {
        this.setSkin(faceDto.getSkinImgs().toString());
        this.setHair(faceDto.getHairImgs().toString());
        this.setExpression(faceDto.getExpressionImgs().toString());
    }
}