package com.example.potatoservice.model

import android.content.Context
import com.example.potatoservice.model.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://0293199f-5857-447a-b461-97d6303a1fba.mock.pstmn.io/"

    // Retrofit 인스턴스를 생성하는 메서드
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // APIService 인스턴스를 반환하는 메서드
    fun apiService(): APIService {
        return retrofit.create(APIService::class.java)
    }
}
