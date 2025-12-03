package com.example.review.dataclass

data class Naver_review(
    var date: String,
    var content: String,
    val imageUrls: List<String> = emptyList()
)
