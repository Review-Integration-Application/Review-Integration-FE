package com.example.review.api.Response

data class KakaoReviewResponse (
    var store_id: Int,
    var store_name: String,
    var review_content: String?,
    var date: String?,
    var image_url: String?
)