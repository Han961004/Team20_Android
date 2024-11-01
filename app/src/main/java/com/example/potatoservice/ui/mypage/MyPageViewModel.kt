package com.example.potatoservice.ui.mypage

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.potatoservice.R

class MyPageViewModel(private val context: Context, private val myPageModel: MyPageModel) : ViewModel() {



    //봉사시간
    private val _vmVolunteerHours = MutableLiveData<Int>()
    val vmVolunteerHours: LiveData<Int> get() = _vmVolunteerHours

    //봉사 건수
    private val _vmVolunteerCount = MutableLiveData<Int>()
    val vmVolunteerCount: LiveData<Int> get() = _vmVolunteerCount

    //경험치바 값
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    //경험치바 퍼센트
    private val _progressPercent = MutableLiveData<Int>()
    val progressPercent : LiveData<Int> get() = _progressPercent

    //레벨
    private val _vmLevel = MutableLiveData<Int>()
    val vmLevel : MutableLiveData<Int> get() = _vmLevel

    //다이얼로그
    private val _vmDialogArray: Array<DialogModel> = myPageModel.dialogArray
    val vmDialogArray: Array<DialogModel> get() = _vmDialogArray

    //스피너
    private val vmSpinnerItems: Array<String> = myPageModel.spinnerItems
    var vmSpinnerAdapter: ArrayAdapter<String>

    //리사이클러뷰 count
    private val _vmRecyclerViewCount = MutableLiveData<Int>()
    val vmRecyclerViewCount: LiveData<Int> get() = _vmRecyclerViewCount

    // 다이얼로그 표시 횟수
    private val _dialogShowCount = MutableLiveData<Int>(0)
    val dialogShowCount: LiveData<Int> get() = _dialogShowCount

    // 긍정 응답 횟수
    private val _positiveCount = MutableLiveData<Int>(0)
    val positiveCount: LiveData<Int> get() = _positiveCount

    // 부정 응답 횟수
    private val _negativeCount = MutableLiveData<Int>(0)
    val negativeCount: LiveData<Int> get() = _negativeCount

    // 현재 다이얼로그 모델
    private val _currentDialogModel = MutableLiveData<DialogModel?>()
    val currentDialogModel: LiveData<DialogModel?> get() = _currentDialogModel

    // 다이얼로그 최대 표시 횟수
    private val maxDialogCount = 5

    // 초기화 시점에 다이얼로그 배열 로드
    private val dialogArray: Array<DialogModel> = myPageModel.dialogArray


    //초기 설정
    init {
        //mypage 스피너 설정
        vmSpinnerAdapter = ArrayAdapter(context, R.layout.spinner_item, vmSpinnerItems)
        vmSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)

        //봉사시간
        myPageModel.volunteerHousr.observeForever {
            _vmVolunteerHours.value = it
            calculateEx(it)
        }

        //봉사 횟수
        myPageModel.volunteerCount.observeForever {
            _vmVolunteerCount.value = it
        }

        //리사이클러뷰 횟수
        myPageModel.recyclerViewCount.observeForever {
            _vmRecyclerViewCount.value = it
        }

    }


    //봉사시간에 따라 레벨과 경험치 값 조정
    private fun calculateEx(hours: Int) {
        //봉사시간 10시간마다 레벨 업
        val level = hours / 10
        val progressValue = (hours % 10)*10

        _vmLevel.value = level
        _progress.value = progressValue
        _progressPercent.value = progressValue
    }

    fun setVolunteerHours(){
        myPageModel.setVolunteerHours()
    }

    fun setVolunteerCount(){
        myPageModel.setVolunteerCount()
    }

    fun setRecyclerViewCount(){
        myPageModel.setRecyclerViewCount()
    }

    // 다이얼로그 표시 상태 업데이트
    fun showNextDialog() {
        val currentCount = _dialogShowCount.value ?: 0
        if (currentCount < maxDialogCount) {
            _currentDialogModel.value = dialogArray[currentCount] // 현재 다이얼로그 모델 설정
            _dialogShowCount.value = currentCount + 1 // 표시 횟수 증가
        } else {
            _currentDialogModel.value = null // 더 이상 다이얼로그 없음
        }
    }

    // 긍정 버튼 클릭 시 호출되는 함수
    fun onPositiveButtonClick() {
        _positiveCount.value = (_positiveCount.value ?: 0) + 1
        showNextDialog() // 다음 다이얼로그 표시
    }

    // 부정 버튼 클릭 시 호출되는 함수
    fun onNegativeButtonClick() {
        _negativeCount.value = (_negativeCount.value ?: 0) + 1
        showNextDialog() // 다음 다이얼로그 표시
    }
//=======

    /* 김동한
    * SignIn 로그인 Activity 에서 로그인을 하면, SharedPreferences에 jwtToken과 userInfo 객체를 담습니다.
    * 이제 SharedPreferences에서 꺼내서 userInfo(아바타) 에는 현재 <닉네임, 나이대, 경험(횟수), 레벨(경험치?)> 이 담겨져 있습니다.
     */

//    private val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
//    private val _jwtToken = MutableLiveData<String>()
//    val jwtToken: LiveData<String> get() = _jwtToken
//
//    private val _userInfo = MutableLiveData<String?>() // userInfo를 JSON 문자열로 가정
//    val userInfo: LiveData<String?> get() = _userInfo
//
//    init {
//        _jwtToken.value = sharedPref.getString("jwt_token", null)
//        _userInfo.value = sharedPref.getString("user_info", null)
//    }

}
