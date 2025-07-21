package com.example.review.api.Response

data class RestaurantDetailResponse(
    var restore_name: String,
    var category: String,
    var total_reviews_num: Int,
    var review_short: String?
)
