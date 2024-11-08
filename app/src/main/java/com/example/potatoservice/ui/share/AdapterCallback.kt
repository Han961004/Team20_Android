package com.example.potatoservice.ui.share

import android.os.Parcelable

interface AdapterCallback {
	fun onClicked(id: Int)

	fun loadMoreActivities(recyclerViewState: Parcelable?)
}
