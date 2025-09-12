package com.example.review.dataclass

data class Kakao_review(
    var date: String,
    var review_content: String,
    val image_url: List<String> = emptyList()
)
