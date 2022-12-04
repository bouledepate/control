package com.amir.ctrl.data.retrofit

import com.amir.ctrl.data.TimeZone
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/v2.1/get-time-zone?key=TTKRFPCK1UTN&format=json&by=position")
    fun getTimezone(@Query("lat") latitude: String, @Query("lng") longitude: String): Call<TimeZone>
}