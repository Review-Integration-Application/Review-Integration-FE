package com.example.review.api.Response

import com.google.gson.annotations.SerializedName

data class ReviewSummaryResponse(
    @SerializedName("naver_hashtag")
    var naver_hashtag: String?, // "#태그1 #태그2" 형태의 문자열로 예상

    @SerializedName("kakao_hashtag")
    var kakao_hashtag: String?, // "#태그1 #태그2" 형태의 문자열로 예상

    @SerializedName("good_points")
    var good_points: String?,

    @SerializedName("bad_points")
    var bad_points: String?
)
