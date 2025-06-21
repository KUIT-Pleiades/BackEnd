package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchUserDto {
    @Pattern(regexp = "^\\w+$", message = "영문자와 숫자만 입력 가능합니다.")
    private String userId;

    @Pattern(regexp = "^[\\w가-힣]+$", message = "한글, 영문, 숫자만 입력 가능합니다.")
    private String userName;

    @Pattern(regexp = "^https://gateway\\.pinata\\.cloud/ipfs/.+$")
    private String profile;

    private String status;
}

