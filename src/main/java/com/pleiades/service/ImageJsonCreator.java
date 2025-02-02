package com.pleiades.service;

import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.dto.character.StarBackgroundDto;
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
import org.springframework.stereotype.Component;

import java.util.List;

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


    public CharacterFaceDto makeCharacterFaceJson(Skin skin, Expression expression, Hair hair) {
        CharacterFaceDto characterFaceDto = new CharacterFaceDto();

        characterFaceDto.setSkinImg(skin.getName());
        characterFaceDto.setExpressionImg(expression.getName());
        characterFaceDto.setHairImg(hair.getName());

        return characterFaceDto;
    }

    public CharacterOutfitDto makeCharacterOutfitJson(Top top, Bottom bottom, Shoes shoe) {
        CharacterOutfitDto characterOutfitDto = new CharacterOutfitDto();

        characterOutfitDto.setTopImg(top.getName());
        characterOutfitDto.setBottomImg(bottom.getName());
        characterOutfitDto.setShoesImg(shoe.getName());

        return characterOutfitDto;
    }

    public CharacterItemDto makeCharacterItemJson(List<Item> items) {
        CharacterItemDto characterItemDto = new CharacterItemDto();
        for (Item item : items) {
            characterItemDto.getItemImgs().add(item.getName());
        }
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