package com.example.potatoservice.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.potatoservice.R
import com.example.potatoservice.model.remote.MarkerData
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import androidx.lifecycle.viewModelScope
import com.example.potatoservice.model.RetrofitClient
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelTextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback


class MapViewModel : ViewModel() {
    private val _cameraPosition = MutableLiveData<LatLng>()
    private val _zoomLevel = MutableLiveData<Int>()

    fun saveLastLocation(latitude: Double, longitude: Double, zoom: Int) {
        _cameraPosition.value = LatLng.from(latitude, longitude)
        _zoomLevel.value = zoom
    }

    fun getLastLocation(): Triple<Double, Double, Int> {
        val lat = _cameraPosition.value?.latitude ?: 37.402005
        val lon = _cameraPosition.value?.longitude ?: 127.108621
        val zoom = _zoomLevel.value ?: 15
        return Triple(lat, lon, zoom)
    }









    private val _markerDataList = MutableLiveData<List<MarkerData>>()
    val markerDataList: LiveData<List<MarkerData>> get() = _markerDataList

    private val _selectedMarker = MutableLiveData<MarkerData?>()
    val selectedMarker: LiveData<MarkerData?> get() = _selectedMarker

    // 서버에서 마커 데이터를 가져와 LiveData에 저장
    fun setMarkerData() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.apiService.getMarkers()

            response.enqueue(object : Callback<List<MarkerData>> {
                override fun onResponse(
                    call: Call<List<MarkerData>>,
                    response: Response<List<MarkerData>>
                ) {
                    if (response.isSuccessful) {
                        // 서버에서 받은 데이터를 LiveData에 저장
                        _markerDataList.postValue(response.body())
                        Log.d("testt", "Markers fetched successfully: ${response.body()}")
                    } else {
                        Log.d("testt", "Response unsuccessful: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<MarkerData>>, t: Throwable) {
                    // 에러 처리
                    Log.e("testt", "Failed to fetch markers", t)
                }
            })
        }
    }

    // 특정 마커를 선택하여 CardView 데이터 업데이트
    fun selectMarker(markerData: MarkerData) {
        _selectedMarker.value = markerData
    }

    // Clear selected marker
    fun clearSelectedMarker() {
        _selectedMarker.value = null
    }

    // 지도에 라벨 추가
    fun addMarkersToMap(kakaoMap: KakaoMap) {
        val markerDataList = _markerDataList.value ?: return

        for (markerData in markerDataList) {
            // lat과 lng 값을 사용하여 LatLng 객체 생성
            val latLng = LatLng.from(markerData.lat, markerData.lng)

            // 마커 스타일 설정 (원하는 스타일로 변경 가능)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_map_marker).setZoomLevel(5))
            val labelOptions = LabelOptions.from(latLng).setStyles(styles)

            // 라벨 추가
            val label = kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
            Log.d("testt", "Marker added at: ${latLng.latitude}, ${latLng.longitude}")

            // 라벨에 MarkerData 연결
            label.tag = markerData
        }

        // 라벨 클릭 리스너 설정
        kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kakaoMap: KakaoMap, layer: LabelLayer, clickedLabel: com.kakao.vectormap.label.Label) {
                val markerData = clickedLabel.tag as? MarkerData
                markerData?.let {
                    selectMarker(it)
                }
            }
        })
    }
    // 지도에 기관 마커 추가
    fun addInstituteMarker(kakaoMap: KakaoMap, latLng: LatLng, instituteName: String) {
        val style = LabelStyle.from(R.drawable.ic_map_marker_institute).setZoomLevel(5).setTextStyles(LabelTextStyle.from(40, R.color.point_brown_2))
        val labelOptions = LabelOptions.from(latLng).setStyles(style).setTexts(instituteName)
        kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
    }
    // 기관 위치로 이동
    fun moveInstitute(kakaoMap: KakaoMap, latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        kakaoMap.moveCamera(cameraUpdate)
    }

}
