package com.example.review.dataclass

data class Kakao_review(
    var rating: Float,
    var date: String,
    var content: String,
    var image1: Int? = null,
    var image2: Int? = null
)
