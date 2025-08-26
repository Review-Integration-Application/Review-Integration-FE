package com.example.review.api.Response

// 메인 액티비티의 바텀 시트와 상세 프래그먼트
data class RestaurantDetailResponse(
    var restore_name: String,
    var category: String,
    var total_reviews_num: Int,
    var review_short: String?,
    var image: String
)