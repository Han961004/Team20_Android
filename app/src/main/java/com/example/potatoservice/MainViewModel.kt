package com.example.potatoservice

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.home.HomeRepository
import com.example.potatoservice.ui.share.Request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    //봉사 활동 검색 결과 리스트
    private val _searchResults = MutableLiveData<List<Activity>>()
    val searchResults: LiveData<List<Activity>> get() = _searchResults

    private val _jwtToken = MutableLiveData<String>()
    val jwtToken: LiveData<String> get() = _jwtToken

    private val _userInfo = MutableLiveData<String>()
    val userInfo: LiveData<String> get() = _userInfo

    /*
    * 굳이 없어도 되는 건지 나중에 확인 -> 없어도 된다 지운다.
     */
    fun searchHomeData(request: Request) {
        homeRepository.search(request)
        Log.d("testt", "검색결과: $request")
    }

    /* 아바타 정보 공유
    * SignInActivity 에서 ViewModel에 넣었던 값 받아오기
     */
    fun setLoginData(token: String, userInfo: String?) {
        _jwtToken.value = token
        _userInfo.value = userInfo.toString()
        Log.d("testt", "뷰모델 로그인 저장 : ${_userInfo.value}, ${_jwtToken.value}")
    }
    //다음 페이지 검색 함수
    fun loadMoreActivities(request: Request) {
        viewModelScope.launch {
            homeRepository.loadMoreActivities(request)
        }
    }

    /* 봉사 활동 정보 공유
    * 홈 프레그먼트에서 검색한 활동을 받아오고 
    * 그 후에 맵 프레그먼트에서 관찰할 것
     */
    init {
        homeRepository.activityList.asLiveData().observeForever { activities ->
            _searchResults.value = activities
        }
    }
}
