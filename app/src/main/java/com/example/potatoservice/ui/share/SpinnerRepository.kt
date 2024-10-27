package com.example.potatoservice.ui.share

import androidx.lifecycle.LiveData
import com.example.potatoservice.model.remote.SidoGungu
import javax.inject.Inject

class SpinnerRepository @Inject constructor(private val spinnerDataSource: SpinnerDataSource) {
	val sidoList : LiveData<List<SidoGungu>> = spinnerDataSource.sidoList
	//시도 데이터를 다 받아 왔는지 확인 하는 변수
	val sidoLoading : LiveData<Boolean> = spinnerDataSource.sidoLoading
	fun searchSidoList(){
		spinnerDataSource.getSidoList()
	}
	//군구 데이터 받기
	val gunguList : LiveData<List<SidoGungu>> = spinnerDataSource.gunguList
	val gunguLoading : LiveData<Boolean> = spinnerDataSource.gunguLoading
	fun searchGunguList(){
		spinnerDataSource.getGunguList()
	}
}