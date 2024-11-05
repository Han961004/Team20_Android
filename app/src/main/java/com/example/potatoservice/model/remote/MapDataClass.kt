package com.example.potatoservice.model.remote

import com.kakao.vectormap.LatLng

data class MarkerData(
    val lat: Double,        // 위도
    val lng: Double,        // 경도
    val title: String,      // 마커 제목
    val address: String,    // 마커 주소
    val description: String, // 마커 설명
    val organization: String,
    val recruitmentPeriod: String,
    val recruitmentCount: String,
    val activityTime: String,
    val activityPeriod: String,
) {
    // lat와 lng 값을 사용하여 LatLng 객체를 반환
    val latLng: LatLng
        get() = LatLng.from(lat, lng)
}

data class HomeData(
    val id: Long,
    val title: String,
    val location: String,
    val recruitmentCount: String
    // 필요한 필드를 추가
)

data class MapData(
    val id: Long,
    val location: String,
    val title: String,
    val address: String
// 필요한 필드를 추가
)