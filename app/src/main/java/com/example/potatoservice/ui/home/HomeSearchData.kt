package com.example.potatoservice.ui.home

import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.share.Request

interface HomeSearchData {
	fun search(request: Request, callback: LoadCallback)

	interface LoadCallback {
		fun onLoaded(activities: List<Activity>, numberOfElements:Int)
		fun onFailed()
	}

}