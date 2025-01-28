package com.pleiades.service;

import com.pleiades.dto.character.response.ResponseCharacterFaceDto;
import com.pleiades.dto.character.CharacterImageDto;
import com.pleiades.dto.character.response.ResponseCharacterItemDto;
import com.pleiades.dto.character.response.ResponseCharacterOutfitDto;
import com.pleiades.dto.character.response.ResponseStarBackgroundDto;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.Item;
import com.pleiades.entity.character.outfit.Bottom;
import com.pleiades.entity.character.outfit.Shoes;
import com.pleiades.entity.character.outfit.Top;
import com.pleiades.repository.StarBackgroundRepository;
import com.pleiades.repository.character.face.ExpressionRepository;
import com.pleiades.repository.character.face.HairRepository;
import com.pleiades.repository.character.face.SkinRepository;
import com.pleiades.repository.character.ItemRepository;
import com.pleiades.repository.character.outfit.BottomRepository;
import com.pleiades.repository.character.outfit.ShoesRepository;
import com.pleiades.repository.character.outfit.TopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ImageJsonCreator {

    private SkinRepository skinRepository;
    private ExpressionRepository expressionRepository;
    private HairRepository hairRepository;

    private TopRepository topRepository;
    private BottomRepository bottomRepository;
    private ShoesRepository shoesRepository;

    private ItemRepository itemRepository;

    private StarBackgroundRepository starBackgroundRepository;

    @Autowired
    public ImageJsonCreator(SkinRepository skinRepository, ExpressionRepository expressionRepository, HairRepository hairRepository,
                            TopRepository topRepository, BottomRepository bottomRepository, ShoesRepository shoesRepository,
                            ItemRepository itemRepository, StarBackgroundRepository starBackgroundRepository) {
        this.skinRepository = skinRepository;
        this.expressionRepository = expressionRepository;
        this.hairRepository = hairRepository;
        this.topRepository = topRepository;
        this.bottomRepository = bottomRepository;
        this.shoesRepository = shoesRepository;
        this.itemRepository = itemRepository;
        this.starBackgroundRepository = starBackgroundRepository;
    }

    public ResponseCharacterFaceDto makeAllCharacterFaceJson() {
        ResponseCharacterFaceDto characterFaceDto = new ResponseCharacterFaceDto();
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

    public ResponseCharacterOutfitDto makeAllCharacterOutfitJson() {
        ResponseCharacterOutfitDto characterOutfitDto = new ResponseCharacterOutfitDto();
        for (Top top : topRepository.findAll()) {
            CharacterImageDto topDto = new CharacterImageDto();
            topDto.setName(top.getName());
            topDto.setUrl(top.getImageUrl());
            characterOutfitDto.getTopImgs().add(topDto);
        }
        for (Bottom bottom : bottomRepository.findAll()) {
            CharacterImageDto bottomDto = new CharacterImageDto();
            bottomDto.setName(bottom.getName());
            bottomDto.setUrl(bottom.getImageUrl());
            characterOutfitDto.getBottomImgs().add(bottomDto);
        }
        for (Shoes shoe : shoesRepository.findAll()) {
            CharacterImageDto shoeDto = new CharacterImageDto();
            shoeDto.setName(shoe.getName());
            shoeDto.setUrl(shoe.getImageUrl());
            characterOutfitDto.getShoesImgs().add(shoeDto);
        }
        return characterOutfitDto;
    }

    public ResponseCharacterItemDto makeAllCharacterItemJson() {
        ResponseCharacterItemDto characterItemDto = new ResponseCharacterItemDto();
        for (Item item : itemRepository.findAll()) {
            CharacterImageDto itemDto = new CharacterImageDto();
            itemDto.setName(item.getName());
            itemDto.setUrl(item.getImageUrl());
            characterItemDto.getItemImgs().add(itemDto);
        }
        return characterItemDto;
    }

    public ResponseStarBackgroundDto makeAllStarBackgroundJson() {
        ResponseStarBackgroundDto starBgsDto = new ResponseStarBackgroundDto();
        for (StarBackground starBackground : starBackgroundRepository.findAll()) {
            CharacterImageDto starBgDto = new CharacterImageDto();
            starBgDto.setName(starBackground.getName());
            starBgDto.setUrl(starBackground.getImageUrl());
            starBgsDto.getBgImgs().add(starBgDto);
        }
        return starBgsDto;
    }

    public ResponseEntity<Map<String, Object>> makeAllJson() {
        Map<String, Object> body = new HashMap<>();

        // 캐릭터 이미지 전송
        ResponseCharacterFaceDto characterFaceDto = makeAllCharacterFaceJson();
        ResponseCharacterOutfitDto characterOutfitDto = makeAllCharacterOutfitJson();
        ResponseCharacterItemDto characterItemDto = makeAllCharacterItemJson();
        ResponseStarBackgroundDto starBackgroundDto = makeAllStarBackgroundJson();

        body.put("face", characterFaceDto);
        body.put("outfit", characterOutfitDto);
        body.put("item", characterItemDto);
        body.put("starBackground", starBackgroundDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    public ResponseCharacterFaceDto makeACharacterFaceJson(Skin skin, Expression expression, Hair hair) {
        ResponseCharacterFaceDto characterFaceDto = new ResponseCharacterFaceDto();

        CharacterImageDto skinDto = new CharacterImageDto();
        skinDto.setName(skin.getName());
        skinDto.setUrl(skin.getImageUrl());
        characterFaceDto.getSkinImgs().add(skinDto);

        CharacterImageDto expressionDto = new CharacterImageDto();
        expressionDto.setName(expression.getName());
        expressionDto.setUrl(expression.getImageUrl());
        characterFaceDto.getExpressionImgs().add(expressionDto);

        CharacterImageDto hairDto = new CharacterImageDto();
        hairDto.setName(hair.getName());
        hairDto.setUrl(hair.getImageUrl());
        characterFaceDto.getHairImgs().add(hairDto);

        return characterFaceDto;
    }

    public ResponseCharacterOutfitDto makeACharacterOutfitJson(Top top, Bottom bottom, Shoes shoe) {
        ResponseCharacterOutfitDto characterOutfitDto = new ResponseCharacterOutfitDto();

        CharacterImageDto topDto = new CharacterImageDto();
        topDto.setName(top.getName());
        topDto.setUrl(top.getImageUrl());
        characterOutfitDto.getTopImgs().add(topDto);

        CharacterImageDto bottomDto = new CharacterImageDto();
        bottomDto.setName(bottom.getName());
        bottomDto.setUrl(bottom.getImageUrl());
        characterOutfitDto.getBottomImgs().add(bottomDto);

        CharacterImageDto shoeDto = new CharacterImageDto();
        shoeDto.setName(shoe.getName());
        shoeDto.setUrl(shoe.getImageUrl());
        characterOutfitDto.getShoesImgs().add(shoeDto);

        return characterOutfitDto;
    }

    public ResponseCharacterItemDto makeACharacterItemJson(List<Item> items) {
        ResponseCharacterItemDto characterItemDto = new ResponseCharacterItemDto();
        for (Item item : items) {
            CharacterImageDto itemDto = new CharacterImageDto();
            itemDto.setName(item.getName());
            itemDto.setUrl(item.getImageUrl());
            characterItemDto.getItemImgs().add(itemDto);
        }
        return characterItemDto;
    }
}