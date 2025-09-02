package com.example.review.api.Response

// 식당 DB 응답 클래스
data class RestaurantResponse(
    var store_id: Int,
    var store_name: String,
    //var restore_score: Double,
    var category: String,
    var address: String,
    var img_urls: String?, // 이미지는 여러 개면 리스트로!
    var naver_review_num: Int,
    var kakao_review_num: Int,
    var total_review_num: Int,
    var naver_review_summary: String?,
    var kakao_review_summary: String?,
    var review_summary: String?
)
