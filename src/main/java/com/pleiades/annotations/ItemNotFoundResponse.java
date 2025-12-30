package com.pleiades.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(
        responseCode = "404 NOT_FOUND",
        description = "실패: 아이템을 찾을 수 없음",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = """
                            {
                                "message": "Item not found"
                            }
                            """
                )
        )
)
public @interface ItemNotFoundResponse {
}
