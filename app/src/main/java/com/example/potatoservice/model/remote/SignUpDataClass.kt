package com.example.potatoservice.model.remote

/* 로그인 요청
* 유저 정보를 반환
 */
data class LoginRequest(
    val userInfo: UserInfo
)

/* 유저 정보
* 유저 정보의 <객체> 닉네임, 나이대, 경험, 레벨 등
 */
data class UserInfo(
    val nickname: String,
    val ageGroup: String,
    val experience: String,
    val level: Int
)

/* 회원 가입 요청
* 회원 가입 요청 <객체>
 */
data class SendSignUpUserInfo(
    val nickName: String,
    val ageGroup: String,
    val experience: String
)