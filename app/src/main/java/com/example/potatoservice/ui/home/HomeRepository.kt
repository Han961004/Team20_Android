package com.example.potatoservice.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.model.remote.Activity
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
	fun search(request: Request) {
		_loading.value = true
		homeSearchData.search(request, object : HomeSearchData.LoadCallback {
			override fun onLoaded(activities: List<Activity>, numberOfElements:Int) {
				_activityList.value = activities
				this@HomeRepository.numberOfElements.value = numberOfElements
				_loading.value = false
			}

			override fun onFailed() {
				_activityList.value = emptyList()
				_loading.value = false
			}

		})
	}
}