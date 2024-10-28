package com.example.potatoservice.model

import com.example.potatoservice.model.remote.AccessTokenRequest
import com.example.potatoservice.model.remote.ActivityDetail
import com.example.potatoservice.model.remote.ActivityResponse
import com.example.potatoservice.model.remote.Category
import com.example.potatoservice.model.remote.JwtResponse
import com.example.potatoservice.model.remote.MarkerData
import com.example.potatoservice.model.remote.SidoGungu
import com.example.potatoservice.model.remote.UserInfo
import com.example.potatoservice.model.remote.UserInterest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @POST("/api/signup")
    fun sendUserInfo(
        @Body userInfo: UserInfo
    ): Call<Void>

    @POST("/api/interests")
    fun sendUserInterest(
        @Body userInterest: UserInterest
    ): Call<ResponseBody>

    @GET("/api/markers")
    fun getMarkers(

    ): Call<List<MarkerData>>

    @POST("auth/kakao")
    @Headers("Content-Type: application/json")
    fun sendKakaoAccessToken(
        @Body request: AccessTokenRequest
    ): Call<JwtResponse>

    @GET("/api/isUserRegistered")
    fun isUserRegistered(
        @Query("accessToken") kakaoAccessToken: String
    ): Call<Boolean>



    // API method to get activities
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
    @GET("/api/v1/districts/category")
    fun getCategory(): Call<List<Category>>
}
