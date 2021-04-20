package com.risingstar.androidchallenge.api

import com.risingstar.androidchallenge.models.MyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface myapi {
    @GET("search")
    fun getmodels(@Query("term") term: String?):Call<MyResponse>
}