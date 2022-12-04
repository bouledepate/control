package com.amir.ctrl.data.retrofit

import com.amir.ctrl.data.TimeZone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiService {
    fun getTimezone(latitude: String, longitude: String, onResult: (TimeZone?) -> Unit) {
        val retrofit = Builder.buildService(ApiInterface::class.java)
        retrofit.getTimezone(latitude, longitude).enqueue(object : Callback<TimeZone> {
            override fun onFailure(call: Call<TimeZone>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<TimeZone>, response: Response<TimeZone>) {
                val result = response.body()
                onResult(result)
            }
        })
    }
}