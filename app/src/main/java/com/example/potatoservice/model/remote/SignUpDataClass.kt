package com.example.potatoservice.model.remote

data class UserInfo(
    val nickname: String,
    val ageGroup: String,
    val experience: String
)

data class UserInterest(
    val interests: List<String>
)

data class AccessTokenRequest(
    val accessToken: String
)

data class JwtResponse(
    val jwtToken: String
)
