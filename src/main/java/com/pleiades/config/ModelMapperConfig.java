package com.pleiades.config;

import com.pleiades.dto.CharacterDto;
import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportListDto;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.Report;
import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.face.Face;
import com.pleiades.entity.character.outfit.Outfit;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper =  new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        reportToDto(modelMapper);
        characterToDto(modelMapper);

        return modelMapper;
    }

    private void reportToDto(ModelMapper modelMapper) {
        modelMapper.typeMap(Report.class, ReportDto.class).addMappings(mapper -> {
            mapper.map(Report::getId, ReportDto::setReportId);
            mapper.map(src -> src.getQuestion().getId(), ReportDto::setQuestionId);
            mapper.map(src -> src.getQuestion().getQuestion(), ReportDto::setQuestion);
        });

        modelMapper.typeMap(Report.class, ReportListDto.class).addMappings(mapper -> {
            mapper.map(Report::getId, ReportListDto::setReportId);
            mapper.map(src -> src.getQuestion().getId(), ReportListDto::setQuestionId);
            mapper.map(src -> src.getQuestion().getQuestion(), ReportListDto::setQuestion);
        });
    }

    private void characterToDto(ModelMapper modelMapper) {
        modelMapper.typeMap(Face.class, CharacterFaceDto.class).addMappings(mapper -> {
            mapper.map(Face::getSkin, CharacterFaceDto::setSkinImg);
            mapper.map(Face::getHair, CharacterFaceDto::setHairImg);
            mapper.map(Face::getExpression, CharacterFaceDto::setExpressionImg);
        });

        modelMapper.typeMap(Outfit.class, CharacterOutfitDto.class).addMappings(mapper -> {
            mapper.map(Outfit::getTop, CharacterOutfitDto::setTopImg);
            mapper.map(Outfit::getBottom, CharacterOutfitDto::setBottomImg);
            mapper.map(Outfit::getShoes, CharacterOutfitDto::setShoesImg);
        });

        modelMapper.typeMap(Item.class, CharacterItemDto.class).addMappings(mapper -> {
            mapper.map(Item::getEars, CharacterItemDto::setEarsImg);
            mapper.map(Item::getEyes, CharacterItemDto::setEyesImg);
            mapper.map(Item::getHead, CharacterItemDto::setHeadImg);
            mapper.map(Item::getNeck, CharacterItemDto::setNeckImg);
            mapper.map(Item::getLeftHand, CharacterItemDto::setLeftHandImg);
            mapper.map(Item::getRightHand, CharacterItemDto::setRightHandImg);
            mapper.map(Item::getLeftWrist, CharacterItemDto::setLeftWristImg);
            mapper.map(Item::getRightWrist, CharacterItemDto::setRightWristImg);
        });
    }
}
