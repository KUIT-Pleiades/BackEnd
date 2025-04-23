package com.pleiades.config;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportListDto;
import com.pleiades.entity.Report;
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
                .setMatchingStrategy(MatchingStrategies.STRICT)
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
