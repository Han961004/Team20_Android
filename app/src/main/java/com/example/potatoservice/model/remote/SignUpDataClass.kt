package com.example.potatoservice.model.remote

// 기존 회원 여부 response
data class SignUpResponse(
    val userInfo: UserInfo
)

// 로그인 response
data class LoginRequest(
    val userInfo: UserInfo
)

// 유저 정보
data class UserInfo(
    val nickname: String,
    val ageGroup: String,
    val experience: String,
    val level: Int
)

// 회원 가입 시 보낼 유저 객체
data class SendSignUpUserInfo(
    val nickName: String,
    val ageGroup: String,
    val experience: String
)

// 회원 가입 후 메시지 return
data class SignUpRequest(
    val messaage: String
)







data class AccessToken(
    val accessToken: String
)


