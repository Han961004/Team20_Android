package com.example.potatoservice.ui.share

data class Volunteer(
    val id: Int, //historyId
    val title: String,  //봉사활동 제목
    val institution: String, //기관
    val Category: String,  //분야
    val recruitmentPeriod: String, //모집기간
    val recruitmentCount: Int,   //모집 인원
    val activityPeriod: String, //봉사활동 기간
    val volunteerHours: String, //봉사 인정 시간
    val address: String,    //주소
    val status: String,  //기록 상태 (신청완료, 확정대기, 수행완료)
)

//"actId" id
//"actTitle" 이름
//"actLocation" 활동장소
//"noticeStartDate" 모집기간 (시작)
//"noticeEndDate" 모집기간 (마감)
//"actStartDate" 봉사기간 (시작)
//"actEndDate"  봉사기간 (마감)
//"actStartTime" 봉사시간(시작시간)
//"actEndTime" 봉사시간(마감시간)
//"recruitTotalNum" 모집인원