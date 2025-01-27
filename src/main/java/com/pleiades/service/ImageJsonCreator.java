package com.pleiades.service;

import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterImageDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.face.Expression;
import com.pleiades.entity.face.Hair;
import com.pleiades.entity.face.Skin;
import com.pleiades.entity.item.Item;
import com.pleiades.entity.outfit.Bottom;
import com.pleiades.entity.outfit.Shoes;
import com.pleiades.entity.outfit.Top;
import com.pleiades.repository.face.ExpressionRepository;
import com.pleiades.repository.face.HairRepository;
import com.pleiades.repository.face.SkinRepository;
import com.pleiades.repository.item.ItemRepository;
import com.pleiades.repository.outfit.BottomRepository;
import com.pleiades.repository.outfit.ShoesRepository;
import com.pleiades.repository.outfit.TopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.awt.*;

@Component
public class ImageJsonCreator {

    private SkinRepository skinRepository;
    private ExpressionRepository expressionRepository;
    private HairRepository hairRepository;

    private ItemRepository itemRepository;

    private TopRepository topRepository;
    private BottomRepository bottomRepository;
    private ShoesRepository shoesRepository;

    @Autowired
    public ImageJsonCreator(SkinRepository skinRepository, ExpressionRepository expressionRepository, HairRepository hairRepository,
                            ItemRepository itemRepository,
                            TopRepository topRepository, BottomRepository bottomRepository, ShoesRepository shoesRepository) {
        this.skinRepository = skinRepository;
        this.expressionRepository = expressionRepository;
        this.hairRepository = hairRepository;
        this.itemRepository = itemRepository;
        this.topRepository = topRepository;
        this.bottomRepository = bottomRepository;
        this.shoesRepository = shoesRepository;
    }

    public CharacterFaceDto makeCharacterFaceJson() {
        CharacterFaceDto characterFaceDto = new CharacterFaceDto();
        for (Skin skin : skinRepository.findAll()) {
            CharacterImageDto skinDto = new CharacterImageDto();
            skinDto.setName(skin.getName());
            skinDto.setUrl(skin.getImageUrl());
            characterFaceDto.getSkinImgs().add(skinDto);
        }
        for (Expression expression : expressionRepository.findAll()) {
            CharacterImageDto expressionDto = new CharacterImageDto();
            expressionDto.setName(expression.getName());
            expressionDto.setUrl(expression.getImageUrl());
            characterFaceDto.getExpressionImgs().add(expressionDto);
        }
        for (Hair hair : hairRepository.findAll()) {
            CharacterImageDto hairDto = new CharacterImageDto();
            hairDto.setName(hair.getName());
            hairDto.setUrl(hair.getImageUrl());
            characterFaceDto.getHairImgs().add(hairDto);
        }
        return characterFaceDto;
    }

    public CharacterItemDto makeCharacterItemJson() {
        CharacterItemDto characterItemDto = new CharacterItemDto();
        for (Item item : itemRepository.findAll()) {
            CharacterImageDto itemDto = new CharacterImageDto();
            itemDto.setName(item.getName());
            itemDto.setUrl(item.getImageUrl());
            characterItemDto.getItemImgs().add(itemDto);
        }
        return characterItemDto;
    }

    public CharacterOutfitDto makeCharacterOutfitJson() {
        CharacterOutfitDto characterOutfitDto = new CharacterOutfitDto();
        for (Top top : topRepository.findAll()) {
            CharacterImageDto topDto = new CharacterImageDto();
            topDto.setName(top.getName());
            topDto.setUrl(top.getImageUrl());
            characterOutfitDto.getTopImg().add(topDto);
        }
        for (Bottom bottom : bottomRepository.findAll()) {
            CharacterImageDto bottomDto = new CharacterImageDto();
            bottomDto.setName(bottom.getName());
            bottomDto.setUrl(bottom.getImageUrl());
            characterOutfitDto.getBottomImg().add(bottomDto);
        }
        for (Shoes shoe : shoesRepository.findAll()) {
            CharacterImageDto shoeDto = new CharacterImageDto();
            shoeDto.setName(shoe.getName());
            shoeDto.setUrl(shoe.getImageUrl());
            characterOutfitDto.getShoesImg().add(shoeDto);
        }
        return characterOutfitDto;
    }
}
