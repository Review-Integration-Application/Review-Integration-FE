package com.example.review.api.Response

data class NaverReviewResponse(
    var store_id: Int,
    var store_name: String,
    var content: String?,
    var date: String?,
    var img_url: String?
)
