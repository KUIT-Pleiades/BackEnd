package com.pleiades.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(
        responseCode = "403 FORBIDDEN",
        description = "실패: 내 것이 아닌 매물",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = """
                            {
                                "message": "Access denied"
                            }
                            """
                )
        )
)
public @interface NotMyListingResponse {
}
