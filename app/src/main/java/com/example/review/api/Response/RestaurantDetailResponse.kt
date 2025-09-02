package com.example.review.api.Response

// 메인 액티비티의 바텀 시트와 상세 프래그먼트
data class RestaurantDetailResponse(
    var store_name: String?,
    var category: String?,
    var total_review_num: Int?,
    var review_summary: String?,
    var img_urls: String?
)