package com.example.potatoservice.ui.share

import androidx.lifecycle.LiveData
import com.example.potatoservice.model.remote.SidoGungu
import javax.inject.Inject

class SpinnerRepository @Inject constructor(private val spinnerDataSource: SpinnerDataSource) {
	val sidoList : LiveData<List<SidoGungu>> = spinnerDataSource.sidoList
	fun searchSidoList(){
		spinnerDataSource.getSidoList()
	}
}