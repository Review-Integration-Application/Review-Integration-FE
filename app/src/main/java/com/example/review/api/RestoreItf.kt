package com.example.review.api

import retrofit2.Call
import com.example.review.api.Response.RestaurantResponse
import retrofit2.http.GET

interface RestoreItf {

    @GET("/restaurants")
    fun getRestaurants(): Call<List<RestaurantResponse>>
}