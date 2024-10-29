package com.example.potatoservice.model

import com.example.potatoservice.model.remote.AccessToken
import com.example.potatoservice.model.remote.ActivityDetail
import com.example.potatoservice.model.remote.ActivityResponse
import com.example.potatoservice.model.remote.LoginRequest
import com.example.potatoservice.model.remote.MarkerData
import com.example.potatoservice.model.remote.SendSignUpUserInfo
import com.example.potatoservice.model.remote.SignUpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    /* 로그인
    * AccessToken 넣고, 요청을 보내면 userInfo 및 jwt 반환
     */
    @POST("/api/login")
    fun kakaoLogin(
        @Body accessToken: AccessToken
    ): Call<LoginRequest>

    /* 회원 가입
    * jwtToken 넣고, sendSignUpUser 객체에 담아서 보내고, 가입 여부 메시지 반환
     */
    @POST("/api/signup")
    fun sendUserInfo(
        @Header("Authorization") jwtToken: String,
        @Body signUpInfo: SendSignUpUserInfo
    ): Call<SignUpRequest>



    // 지도 맵 마커
    @GET("/api/markers")
    fun getMarkers(
    ): Call<List<MarkerData>>



    @GET("/api/v1/activities")
    fun getActivities(
        @Query("page") page: Int,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("beforeDeadlineOnly") beforeDeadlineOnly: Boolean? = null,
        @Query("teenPossibleOnly") teenPossibleOnly: Boolean? = null,
        @Query("category") category: String? = null
    ): Call<ActivityResponse>

    @GET("/api/v1/activities/{activity_id}")
    fun getActivityDetail(
        @Path("activity_id") activityId: Int
    ): Call<ActivityDetail>
}
