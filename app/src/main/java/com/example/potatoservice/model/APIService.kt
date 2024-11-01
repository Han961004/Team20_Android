package com.example.potatoservice.model

import com.example.potatoservice.model.remote.ActivityDetail
import com.example.potatoservice.model.remote.ActivityResponse
import com.example.potatoservice.model.remote.LoginRequest
import com.example.potatoservice.model.remote.MarkerData

import com.example.potatoservice.model.remote.SendSignUpUserInfo

import com.example.potatoservice.model.remote.SidoGungu

import com.example.potatoservice.model.remote.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    /* 로그인
    * AccessToken 헤더에 넣고, 요청을 보내면 userInfo 및 jwt 반환
    * UserInfo -> nickname, ageRange, experience, level 등
     */
    @GET("/api/login")              //  ->  /api/v1/users/login/kakao
    fun kakaoLogin(
        @Header("Authorization") accessToken: String,
    ): Call<LoginRequest>

    /* 회원 가입
    * jwtToken 넣고, sendSignUpUser 객체에 담아서 보냄
    * sendSignUpUser -> nickname, ageRange, experienced 포함
     */
    @POST("/api/signup")            //  ->  /api/v1/avatars POST
    fun sendUserInfo(
        @Header("Authorization") jwtToken: String,
        @Body signUpInfo: SendSignUpUserInfo
    ): Call<Void>









    // 지도 맵 마커
    @GET("/api/markers")
    fun getMarkers(
    ): Call<List<MarkerData>>


    @GET("/api/v1/activities")
    fun getActivities(
        @Query("page") page: Int,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("sidoCode") sidoCode: Int? = null,
        @Query("sidoGunguCode") sidoGunguCode: Int? = null,
        @Query("beforeDeadlineOnly") beforeDeadlineOnly: Boolean? = null,
        @Query("teenPossibleOnly") teenPossibleOnly: Boolean? = null,
        @Query("category") category: String? = null
    ): Call<ActivityResponse>

    @GET("/api/v1/activities/{activity_id}")
    fun getActivityDetail(
        @Path("activity_id") activityId: Int
    ): Call<ActivityDetail>

    //시도 목록 받음
    @GET("/api/v1/districts/sido")
    fun getSido(): Call<List<SidoGungu>>

    //군구 목록 받음
    @GET("/api/v1/districts/gungu")
    fun getGungu(): Call<List<SidoGungu>>
    //카테고리 목록 받음, 임시
    @GET("/api/v1/activities/categories")
    fun getCategory(): Call<List<String>>

}
