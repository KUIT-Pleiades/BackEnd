package com.pleiades.annotations;

import com.pleiades.dto.store.OfficialStoreDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // 메서드에 붙일 수 있음
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 유지
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(schema = @Schema(implementation = OfficialStoreDto.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "실패: 사용자를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                            {
                                "message": "User not found"
                            }
                            """
                        )
                )
        )
})
public @interface OfficialStoreResponses {
}