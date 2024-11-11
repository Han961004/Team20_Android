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
import com.example.potatoservice.model.KakaoRetrofitClient
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.AddressResponse
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









    private val _markerDataList = MutableLiveData<List<MarkerData>>(listOf())
    val markerDataList: LiveData<List<MarkerData>> get() = _markerDataList
    // 기존 마커 데이터를 지우는 함수 추가
    fun clearMarkerDataList() {
        _markerDataList.value = listOf()
    }

    // 마커 데이터를 추가하는 함수
    fun addMarkerData(lat: Double, lng: Double, title: String, address: String) {
        val currentList = _markerDataList.value.orEmpty().toMutableList()
        val newMarker = MarkerData(
            lat = lat,
            lng = lng,
            title = title,
            address = address,
            description = "설명", // 필요에 따라 수정 가능
            organization = "기관", // 필요에 따라 수정 가능
            recruitmentPeriod = "모집기간",
            recruitmentCount = "모집인원",
            activityTime = "활동 시간",
            activityPeriod = "활동 기간"
        )
        currentList.add(newMarker)
        _markerDataList.value = currentList
    }


    private val _selectedMarker = MutableLiveData<MarkerData?>()
    val selectedMarker: LiveData<MarkerData?> get() = _selectedMarker

    // 서버에서 마커 데이터를 가져와 LiveData에 저장
    fun setMarkerData() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.apiService().getMarkers()

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
        // 기존 라벨 모두 제거?????
        kakaoMap.labelManager?.removeAllLabelLayer()

        val markerDataList = _markerDataList.value ?: return

        for (markerData in markerDataList) {
            val latLng = LatLng.from(markerData.lat, markerData.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_map_marker).setZoomLevel(5))
            val labelOptions = LabelOptions.from(latLng).setStyles(styles)
            val label = kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
            Log.d("testt", "Marker added at: ${latLng.latitude}, ${latLng.longitude}")
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






    /* 주소를 좌표로 변환
    * 레트로핏을 사용, 스프링 서버로 부터 actLocation 을 받으면 카카오 주소 검색 API 사용하여 좌표를 받습니다.
     */
    private val _coordinates = MutableLiveData<Pair<Double, Double>?>()
    val coordinates: LiveData<Pair<Double, Double>?> get() = _coordinates
    fun fetchCoordinates(address: String) {
        val apiKey = "KakaoAK aa23edc0dd8f4cc31ed3c9245040e78d"  // Kakao REST API 키를 설정하세요
        val apiService = KakaoRetrofitClient.apiService()  // Retrofit을 통해 API 서비스 호출

        apiService.searchAddress(apiKey, address).enqueue(object : Callback<AddressResponse> {
            override fun onResponse(call: Call<AddressResponse>, response: Response<AddressResponse>) {
                if (response.isSuccessful) {
                    val documents = response.body()?.documents
                    if (!documents.isNullOrEmpty()) {
                        val firstResult = documents[0]
                        val longitude = firstResult.x.toDoubleOrNull()
                        val latitude = firstResult.y.toDoubleOrNull()
                        if (longitude != null && latitude != null) {
                            _coordinates.postValue(Pair(latitude, longitude))
                            Log.d("testt", "주소 변환 성공: $latitude, $longitude")
                        } else {
                            Log.e("testt", "좌표 변환 실패: 좌표 값이 null입니다.")
                        }
                    } else {
                        Log.e("testt", "검색 결과 없음")
                        _coordinates.postValue(null)
                    }
                } else {
                    Log.e("testt", "API 응답 실패: ${response.message()}")
                    _coordinates.postValue(null)
                }
            }

            override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
                Log.e("testt", "API 요청 실패", t)
                _coordinates.postValue(null)
            }
        })
    }




}
