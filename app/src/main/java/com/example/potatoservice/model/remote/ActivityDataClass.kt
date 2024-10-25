package com.example.potatoservice.model.remote

data class Activity(
    val actId: Int,
    val actTitle: String?,
    val actLocation: String?,
    val noticeStartDate: String?,
    val noticeEndDate: String,
    val actStartDate: String?,
    val actEndDate: String,
    val actStartTime: Int,
    val actEndTime: Int,
    val recruitTotalNum: Int,
    val category: String
)

data class ActivityResponse(
    val content: List<Activity>,
    val pageable: Pageable,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val empty: Boolean
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: Sort,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class ActivityDetail(
    val actId: Int,
    val actTitle: String?,
    val actLocation: String?,
    val description: String?,
    val noticeStartDate: String?,
    val noticeEndDate: String,
    val actStartDate: String?,
    val actEndDate: String,
    val actStartTime: Int,
    val actEndTime: Int,
    val recruitTotalNum: Int,
    val adultPossible: Boolean,
    val teenPossible: Boolean,
    val groupPossible: Boolean,
    val actWeek: Int,
    val actManager: String,
    val actPhone: String,
    val url: String,
    val category: String,
    val institute: Institute
){
    companion object{
        val nullActivityDetail = ActivityDetail(
            0,
            "봉사 활동 제목 정보 없음",
            "봉사 활동 장소 정보 없음",
            "봉사 활동 설명 정보 없음",
            "공지 시작 날짜 정보 없음",
            "공지 종료 날짜 정보 없음",
            "봉사 활동 시작 날짜 정보 없음",
            "봉사 활동 종료 날짜 정보 없음",
            0,
            0,
            0,
            false,
            false,
            false,
            0,
            "봉사 활동 담당자 정보 없음",
            "봉사 활동 전화 번호 정보 없음",
            "봉사 활동 url 정보 없음",
            "카테고리 정보 없음",
            Institute(
                0,
                "기관 정보 없음",
                "기관 주소 정보 없음",
                0.0,
                0.0,
                "기관 전화 번호 정보 없음"
            )
        )
    }
}

data class Institute(
    val instituteId:Int,
    val name:String,
    val location:String,
    val latitude:Double?,
    val longitude:Double?,
    val phone:String
)
