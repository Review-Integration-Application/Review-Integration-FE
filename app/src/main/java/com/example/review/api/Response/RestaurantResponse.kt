package com.example.review.api.Response

data class RestaurantResponse(
    var restore_ID: Int,
    var restore_name: String,
    var restore_score: Double,
    var category: String,
    var review_short: String?,
    var image: String?, // 이미지는 여러 개면 리스트로!
    var total_reviews_num: Int,
    var lat: Double,
    var lng: Double,
    var location: String
)
