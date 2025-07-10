package com.pleiades.config;

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
}
