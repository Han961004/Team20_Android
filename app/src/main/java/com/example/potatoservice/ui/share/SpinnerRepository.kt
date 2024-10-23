package com.example.potatoservice.ui.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.model.remote.SidoGungu
import javax.inject.Inject

class SpinnerRepository @Inject constructor(private val spinnerDataSource: SpinnerDataSource) {
	val sidoList : LiveData<List<SidoGungu>> = spinnerDataSource.sidoList
	//시도 데이터를 다 받아 왔는지 확인 하는 변수
	val sidoLoading : LiveData<Boolean> = spinnerDataSource.sidoLoading
	fun searchSidoList(){
		spinnerDataSource.getSidoList()
	}
}