package com.pleiades.service;

import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.dto.character.StarBackgroundDto;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Face;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.outfit.Outfit;
import com.pleiades.repository.StarBackgroundRepository;
import com.pleiades.repository.character.face.ExpressionRepository;
import com.pleiades.repository.character.face.HairRepository;
import com.pleiades.repository.character.face.SkinRepository;
import com.pleiades.repository.character.item.ItemRepository;
import com.pleiades.repository.character.outfit.BottomRepository;
import com.pleiades.repository.character.outfit.ShoesRepository;
import com.pleiades.repository.character.outfit.TopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    public CharacterFaceDto makeCharacterFaceJson(Face face) {
        CharacterFaceDto characterFaceDto = new CharacterFaceDto();

        characterFaceDto.setSkinImg(face.getSkin().getName());
        characterFaceDto.setExpressionImg(face.getExpression().getName());
        characterFaceDto.setHairImg(face.getHair().getName());

        return characterFaceDto;
    }

    public CharacterOutfitDto makeCharacterOutfitJson(Outfit outfit) {
        CharacterOutfitDto characterOutfitDto = new CharacterOutfitDto();

        characterOutfitDto.setTopImg(outfit.getTop().getName());
        characterOutfitDto.setBottomImg(outfit.getBottom().getName());
        characterOutfitDto.setShoesImg(outfit.getShoes().getName());

        return characterOutfitDto;
    }

    public CharacterItemDto makeCharacterItemJson(Item item) {
        CharacterItemDto characterItemDto = new CharacterItemDto();

        characterItemDto.setHeadImg(item.getHead().getName());
        characterItemDto.setNeckImg(item.getNeck().getName());
        characterItemDto.setEarsImg(item.getEars().getName());
        characterItemDto.setEyesImg(item.getEyes().getName());
        characterItemDto.setLeftHandImg(item.getLeftHand().getName());
        characterItemDto.setRightHandImg(item.getRightHand().getName());
        characterItemDto.setLeftWristImg(item.getLeftWrist().getName());
        characterItemDto.setRightWristImg(item.getRightWrist().getName());

        return characterItemDto;
    }

    public StarBackgroundDto makeStarBackgroundJson(Skin skin, Expression expression, Hair hair) {
        StarBackgroundDto starBgsDto = new StarBackgroundDto();
        for (StarBackground starBackground : starBackgroundRepository.findAll()) {
            starBgsDto.setBgImg(starBackground.getName());
        }
        return starBgsDto;
    }
}