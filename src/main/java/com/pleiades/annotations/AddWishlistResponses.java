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
                                "message": "Wishlist Added"
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
                responseCode = "409 CONFLICT",
                description = "실패: 이미 존재하는 찜",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                            {
                                "message": "Wishlist Already Existing"
                            }
                            """
                        )
                )
        )
})
public @interface AddWishlistResponses {
}
