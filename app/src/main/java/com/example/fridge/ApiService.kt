package com.example.fridge

import com.kakao.sdk.user.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/login")
    fun login(@Body user: User?): Call<LoginResponse?>?
}

