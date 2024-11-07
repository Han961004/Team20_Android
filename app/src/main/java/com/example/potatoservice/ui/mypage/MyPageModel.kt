package com.example.potatoservice.ui.mypage

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.MainViewModel
import com.example.potatoservice.R
import com.example.potatoservice.model.remote.AvatarInfo
import dagger.hilt.android.qualifiers.ApplicationContext

object MyPageModel {

    // DialogModel 배열을 생성
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
    val volunteerHousr = MutableLiveData<Int>()
    val volunteerCount = MutableLiveData<Int>()
    val ninkname = MutableLiveData<String>()

    //리사이클러뷰 count
    val recyclerViewCount = MutableLiveData<Int>()

    fun setMyPageModel(userInfo: AvatarInfo){
        volunteerHousr.value = userInfo.avatarExp
        volunteerCount.value = userInfo.avatarExp
        ninkname.value = userInfo.nickName
    }

    //봉사 시간 설정
    fun setVolunteerHours(){
        volunteerHousr.value = 199
    }

    //봉사 횟수 설정
    fun setVolunteerCount(){
        volunteerCount.value = 190
    }

    //MyPage 리사이클러뷰 횟수
    fun setRecyclerViewCount(){
        recyclerViewCount.value = 100
    }

    //개인 봉사내역 데이터

}