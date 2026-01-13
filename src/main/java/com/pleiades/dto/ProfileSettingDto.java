package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileSettingDto {
    @JsonProperty("userName")
    private String userName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("birthDate")
    private LocalDate birthDate;
}

