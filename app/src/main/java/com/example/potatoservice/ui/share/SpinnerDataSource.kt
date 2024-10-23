package com.example.potatoservice.ui.share

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.model.APIService
import com.example.potatoservice.model.remote.SidoGungu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SpinnerDataSource @Inject constructor(private val apiService: APIService){
	private val _sidoList = MutableLiveData<List<SidoGungu>>()
	val sidoList get() = _sidoList

	fun getSidoList() {
		apiService.getSido().enqueue(
			object : Callback<List<SidoGungu>>{
				override fun onResponse(
					call: Call<List<SidoGungu>>,
					response: Response<List<SidoGungu>>
				) {
					if (response.isSuccessful){
						_sidoList.value = response.body()?.map { sidoGungu ->
							SidoGungu(
								sidoGungu.sidoGunguCode,
								sidoGungu.sidoCode,
								sidoGungu.sidoName,
								sidoGungu.gunguName,
								sidoGungu.sido
							)
						}
					}else{
						Log.e("testt", "onResponse fail: ${response.code()}")
						_sidoList.value = emptyList()
					}
				}

				override fun onFailure(call: Call<List<SidoGungu>>, t: Throwable) {
					Log.e("testt", "onFailure: ${t.message}")
					_sidoList.value = emptyList()
				}

			}
		)
	}
}