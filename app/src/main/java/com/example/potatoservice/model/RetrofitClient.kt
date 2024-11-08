package com.example.potatoservice.model

import android.content.Context
import com.example.potatoservice.model.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://3.37.32.242:8080/"

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


// 카카오 레트로핏 주소가 별개라서 만들었씁니다.
object KakaoRetrofitClient {
    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"

    // Retrofit 인스턴스를 생성하는 메서드
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun apiService(): APIService {
        return retrofit.create(APIService::class.java)
    }
}
