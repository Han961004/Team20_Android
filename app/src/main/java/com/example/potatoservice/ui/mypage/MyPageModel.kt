package com.example.potatoservice.ui.mypage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.R
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.AvatarInfo
import com.example.potatoservice.model.remote.HistoryItem
import com.example.potatoservice.model.remote.VolunteerHistoryResponse
import com.example.potatoservice.ui.share.Volunteer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MyPageModel {

    // DialogModel 배열을 생성
    // todo 서버로 부터 리뷰 질문 받기
    val dialogArray = arrayOf(
        DialogModel(
            title = "리뷰 요청",
            content = "테스트1",
            imageBackground = R.drawable.ic_hotgamja_main_character,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = "테스트2",
            imageBackground = R.drawable.ic_interest_cultural_event,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = "테스트3",
            imageBackground = R.drawable.ic_interest_education,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = "테스트4",
            imageBackground = R.drawable.ic_interest_international_event,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = "테스트5",
            imageBackground = R.drawable.ic_interest_support,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        )
        )

    //mypage 보기방식 spinner item
    val spinnerItems : Array<String> = arrayOf("전체보기", "신청완료", "확정 대기", "수행완료됨")

    //봉사 시간 데이터
    val volunteerHours = MutableLiveData<Int>()
    val volunteerCount = MutableLiveData<Int>()
    val ninkname = MutableLiveData<String>()

    //리사이클러뷰 count
    val recyclerViewCount = MutableLiveData<Int>()

    //봉사 내역 리스트
    val volunteerList = MutableLiveData<List<Volunteer>>()

    //todo 봉사 내역 서버로부터 받기
    fun getMyPageList(jwtToken: String) {
        RetrofitClient.apiService().getHistory("Bearer $jwtToken").enqueue(object : Callback<VolunteerHistoryResponse> {
            override fun onResponse(
                call: Call<VolunteerHistoryResponse>,
                response: Response<VolunteerHistoryResponse>
            ) {
                Log.d("seyoung", "getMyPageList response : ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("seyoung", "getMyPageList response.isSuccessful")
                    // 서버에서 받은 VolunteerHistoryResponse에서 content를 가져옴
                    val historyItems = response.body()?.content ?: emptyList()

                    Log.d("seyoung", "historyItems = ${historyItems}")

                    // HistoryItem을 Volunteer로 변환
                    volunteerList.value = historyItems.map { historyItem ->
                        Volunteer(
                            id = historyItem.historyId,
                            title = historyItem.activity.actTitle ?: "제목 없음",
                            institution = "일단 모름", // 나중에 수정
                            Category = historyItem.activity.category,
                            recruitmentPeriod = "${historyItem.activity.noticeStartDate} ~ ${historyItem.activity.noticeEndDate}",
                            recruitmentCount = historyItem.activity.recruitTotalNum,
                            activityPeriod = "${historyItem.activity.actStartDate} ~ ${historyItem.activity.actEndDate}",
                            volunteerHours = "일단 모름", // 봉사시간 계산 필요
                            address = historyItem.activity.actLocation ?: "주소 없음",
                            status = historyItem.activityStatus
                        )
                    }

                    Log.d("seyoung", "getMyPageList결과 = ${volunteerList}")
                    Log.d("seyoung", "getMyPageList.value결과 = ${volunteerList.value}")
                }
                else{
                    Log.d("seyoung","getMyPageList response.isSuccessful 실패")
                }
            }

            override fun onFailure(call: Call<VolunteerHistoryResponse>, t: Throwable) {
                // 실패 처리
                Log.d("seyoung","MyPageModel에서 getMyPageList가 실패함 ㅠ")
            }
        })
    }


    fun setMyPageModel(userInfo: AvatarInfo){
        volunteerHours.value = userInfo.avatarExp
//        volunteerCount.value = userInfo.avatarExp
        ninkname.value = userInfo.nickName
    }



}