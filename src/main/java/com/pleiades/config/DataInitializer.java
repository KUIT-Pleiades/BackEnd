package com.pleiades.config;

import com.pleiades.entity.StarBackground;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.Item.Item;
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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    SkinRepository skinRepository;
    @Autowired
    ExpressionRepository expressionRepository;
    @Autowired
    HairRepository hairRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TopRepository topRepository;
    @Autowired
    BottomRepository bottomRepository;
    @Autowired
    ShoesRepository shoesRepository;

    @Autowired
    StarBackgroundRepository starBackgroundRepository;

    private final String IPFS_URL = System.getenv("IPFS_URL");

    @PostConstruct
    public void initData() {
        saveSkin(); saveExpression(); saveHair();
        saveItem();
        saveTop(); saveBottom(); saveShoes();
        saveStarBackground();
    }

    private void saveSkin() {
        String[] skins = {"skin_01", "skin_02", "skin_03", "skin_04", "skin_05", "skin_06", "skin_07"};
        for (String name : skins) {
            Skin skin = new Skin();
            skin.setName(name);
            skinRepository.save(skin);
        }
    }
    private void saveExpression() {
        String[] expressions = {"face_01", "face_02", "face_03", "face_04", "face_05", "face_06", "face_07", "face_08", "face_09"};
        for (String name : expressions) {
            Expression expression = new Expression();
            expression.setName(name);
            expressionRepository.save(expression);
        }
    }
    private void saveHair() {
        String[] hairs = {"hair_01", "hair_02", "hair_03", "hair_04", "hair_05", "hair_06", "hair_07", "hair_08", "hair_09"};
        for (String name : hairs) {
            Hair hair = new Hair();
            hair.setName(name);
            hairRepository.save(hair);
        }
    }
    private void saveItem() {
        String[] items = {"acc1_01", "acc2_01", "acc3_01", "acc4_01", "acc5_01", "acc7_01", "acc1_02", "fas1_01", "fas1_02", "fas1_03", "fas4_01"};        // acc6_01이 없음
        for (String name : items) {
            Item item = new Item();
            item.setName(name);
            itemRepository.save(item);
        }
    }
    private void saveTop() {
        String[] tops = {"top_01", "top_02", "top_03", "top_04", "top_05", "top_06", "top_07", "top_08", "top_09"};
        for (String name : tops) {
            Top top = new Top();
            top.setName(name);
            topRepository.save(top);
        }
    }
    private void saveBottom() {
        String[] bottoms = {"bottom_01", "bottom_02", "bottom_03", "bottom_04", "bottom_05", "bottom_06", "bottom_07", "bottom_08"};
        for (String name : bottoms) {
            Bottom bottom = new Bottom();
            bottom.setName(name);
            bottomRepository.save(bottom);
        }
    }
    private void saveShoes() {
        String[] shoess = {"shoes_01", "shoes_02", "shoes_03", "shoes_04", "shoes_05", "shoes_06", "shoes_07", "shoes_08", "shoes_09"};
        for (String name : shoess) {
            Shoes shoes = new Shoes();
            shoes.setName(name);
            shoesRepository.save(shoes);
        }
    }
    private void saveStarBackground() {
        String[] bgimgs = {"background_01", "background_02", "background_03", "background_04", "background_05"};
        for (String name : bgimgs) {
            StarBackground bgimg = new StarBackground();
            bgimg.setName(name);
            starBackgroundRepository.save(bgimg);
        }
    }
    // todo: 배경
}
