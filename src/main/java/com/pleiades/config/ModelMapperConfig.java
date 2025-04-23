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
            mapper.map(src -> src.getSkin().getName(), CharacterFaceDto::setSkinImg);
            mapper.map(src -> src.getHair().getName(), CharacterFaceDto::setHairImg);
            mapper.map(src -> src.getExpression().getName(), CharacterFaceDto::setExpressionImg);
        });

        modelMapper.typeMap(Outfit.class, CharacterOutfitDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getTop().getName(), CharacterOutfitDto::setTopImg);
            mapper.map(src -> src.getBottom().getName(), CharacterOutfitDto::setBottomImg);
            mapper.map(src -> src.getShoes().getName(), CharacterOutfitDto::setShoesImg);
        });

        modelMapper.typeMap(Item.class, CharacterItemDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getEars().getName(), CharacterItemDto::setEarsImg);
            mapper.map(src -> src.getEyes().getName(), CharacterItemDto::setEyesImg);
            mapper.map(src -> src.getHead().getName(), CharacterItemDto::setHeadImg);
            mapper.map(src -> src.getNeck().getName(), CharacterItemDto::setNeckImg);
            mapper.map(src -> src.getLeftHand().getName(), CharacterItemDto::setLeftHandImg);
            mapper.map(src -> src.getRightHand().getName(), CharacterItemDto::setRightHandImg);
            mapper.map(src -> src.getLeftWrist().getName(), CharacterItemDto::setLeftWristImg);
            mapper.map(src -> src.getRightWrist().getName(), CharacterItemDto::setRightWristImg);
        });
    }
}
