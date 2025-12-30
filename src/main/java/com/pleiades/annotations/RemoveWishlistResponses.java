package com.pleiades.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200 OK",
                description = "성공",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                            {
                                "message": "Wishlist Removed"
                            }
                            """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "404 NOT_FOUND",
                description = "실패: 사용자를 또는 아이템을 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                            {
                                "message": "Item or User Not Found"
                            }
                            """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "404 NOT_FOUND",
                description = "실패: 찜이 존재하지 않음",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                            {
                                "message": "Wishlist Not Found"
                            }
                            """
                        )
                )
        )
})
public @interface RemoveWishlistResponses {
}
