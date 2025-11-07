package com.example.review.api.Response

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("error")
    val error: String?
)
