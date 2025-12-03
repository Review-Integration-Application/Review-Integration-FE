package com.example.review.api.Response

import com.google.gson.annotations.SerializedName

data class ReviewSummaryResponse(
    @SerializedName("naver_hashtag")
    var naver_hashtag: String?,

    @SerializedName("kakao_hashtag")
    var kakao_hashtag: String?,

    @SerializedName("good_points")
    var good_points: String?,

    @SerializedName("bad_points")
    var bad_points: String?
)
