package com.example.review.api

import com.example.review.api.Response.NaverReviewResponse
import com.example.review.api.Response.RestaurantDetailResponse
import retrofit2.Call
import com.example.review.api.Response.RestaurantResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RestoreItf {

    // 식당 전체 조회
    @GET("restaurants")
    fun getRestaurants(): Call<List<RestaurantResponse>>

    // 식당 상세 조회
    @GET("restaurant/{store_id}")
    fun getRestaurant(@Path("store_id") storeId: Int): Call<RestaurantDetailResponse>

    // 네이버 리뷰 조회
    @GET("naver_review/{store_id}")
    fun getNaverReview(@Path("store_id") storeId: Int): Call<List<NaverReviewResponse>>

}