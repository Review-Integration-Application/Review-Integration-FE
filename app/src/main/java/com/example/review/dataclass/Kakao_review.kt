package com.example.review.dataclass

data class Kakao_review(
    var rating: Float,
    var date: String,
    var content: String,
    var imageUrls: List<String> = emptyList()
)
