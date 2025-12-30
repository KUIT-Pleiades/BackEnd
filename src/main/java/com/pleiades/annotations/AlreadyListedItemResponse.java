package com.pleiades.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(
        responseCode = "409 CONFLICT",
        description = "실패: 이미 매물로 등록된 아이템",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = """
                            {
                                "message": "Already exists"
                            }
                            """
                )
        )
)
public @interface AlreadyListedItemResponse {
}
