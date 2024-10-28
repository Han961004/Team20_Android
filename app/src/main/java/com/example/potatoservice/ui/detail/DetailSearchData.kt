package com.example.potatoservice.ui.detail

import com.example.potatoservice.model.remote.ActivityDetail

interface DetailSearchData {
	fun lookDetail(id: Int, callback: DetailCallback)

	interface DetailCallback {
		fun onLoaded(activityDetail: ActivityDetail)
		fun onFailed()
	}
}