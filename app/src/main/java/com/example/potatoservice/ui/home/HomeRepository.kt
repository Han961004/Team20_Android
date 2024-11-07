package com.example.potatoservice.ui.home


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.model.remote.ActivityDetail.Companion.nullActivity
import com.example.potatoservice.ui.share.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class HomeRepository @Inject constructor(
	private val homeSearchData: HomeSearchData
){
	//홈 페이지 부분
	private val _activityList = MutableStateFlow<List<Activity>>(emptyList())
	val activityList: StateFlow<List<Activity>> = _activityList.asStateFlow()
	//로딩 중인지 알려주는 변수
	private val _loading = MutableLiveData<Boolean>()
	val loading: LiveData<Boolean> get() = _loading
	var numberOfElements = MutableLiveData<Int>()
	//검색 결과 마지막 페이지인지 알려주는 변수
	private val _lastPage = MutableLiveData<Boolean>()
	val lastPage: LiveData<Boolean> get() = _lastPage

	fun search(request: Request) {
		_loading.value = true
		homeSearchData.search(request, object : HomeSearchData.LoadCallback {
			override fun onLoaded(activities: List<Activity>, numberOfElements: Int, last: Boolean) {
				_activityList.value = activities
				this@HomeRepository.numberOfElements.value = numberOfElements
				_lastPage.value = last
				//마지막 페이지가 아니라면 로딩용 액티비티를 추가
				if (!last){
					_activityList.value += nullActivity
				}
				_loading.value = false
			}

			override fun onFailed() {
				_activityList.value = emptyList()
				_lastPage.value = true
				_loading.value = false
			}

		})
	}

	//무한 스크롤 함수
	//다음 페이지 정보를 리턴함
	fun loadMoreActivities(request: Request){
		homeSearchData.search(request, object : HomeSearchData.LoadCallback {
			override fun onLoaded(activities: List<Activity>, numberOfElements: Int, last: Boolean) {
				if (_activityList.value.lastOrNull()?.actId == -1){
					_activityList.value = _activityList.value.dropLast(1)
				}
				_activityList.value += activities
				this@HomeRepository.numberOfElements.value = this@HomeRepository.numberOfElements.value?.plus(
					numberOfElements
				)
				_lastPage.value = last
				//마지막 페이지가 아니라면 로딩용 액티비티를 추가
				if (!last){
					_activityList.value += nullActivity
				}
			}

			override fun onFailed() {
				_lastPage.value = true
			}

		})
	}
}