package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchUserDto {
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String userId;

    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String userName;

    @Pattern(regexp = "^https://gateway\\.pinata\\.cloud/ipfs/.+$")
    private String profile;

    private String status;
}

