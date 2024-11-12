package com.example.potatoservice.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potatoservice.R
import com.example.potatoservice.model.KakaoRetrofitClient
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.model.remote.AddressResponse
import com.example.potatoservice.model.remote.MarkerData
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapViewModel : ViewModel() {

    private val _cameraPosition = MutableLiveData<LatLng>()
    private val _zoomLevel = MutableLiveData<Int>()

    private val _markerDataList = MutableLiveData<List<MarkerData>>(listOf())
    val markerDataList: LiveData<List<MarkerData>> get() = _markerDataList

    private val _selectedMarker = MutableLiveData<MarkerData?>()
    val selectedMarker: LiveData<MarkerData?> get() = _selectedMarker


    fun saveLastLocation(latitude: Double, longitude: Double, zoom: Int) {
        _cameraPosition.value = LatLng.from(latitude, longitude)
        _zoomLevel.value = zoom
    }

    /*
     * 마지막 저장된 카메라 위치와 줌 레벨을 반환합니다.
     * Triple 위도, 경도, 줌 레벨이 포함된 Triple
     */
    fun getLastLocation(): Triple<Double, Double, Int> {
        val lat = _cameraPosition.value?.latitude ?: 37.402005
        val lon = _cameraPosition.value?.longitude ?: 127.108621
        val zoom = _zoomLevel.value ?: 15
        return Triple(lat, lon, zoom)
    }

    fun clearMarkerDataList() {
        _markerDataList.value = listOf()
    }

    fun selectMarker(markerData: MarkerData) {
        _selectedMarker.value = markerData
    }


    /*
     * 마커 데이터를 기반으로 KakaoMap 객체에 마커들을 추가하고 클릭 리스너를 설정합니다.
     * kakaoMap 마커를 추가할 KakaoMap 객체
     */
    fun addMarkersToMap(kakaoMap: KakaoMap) {
        kakaoMap.labelManager?.removeAllLabelLayer()

        val markerDataList = _markerDataList.value ?: return

        for (markerData in markerDataList) {
            val latLng = LatLng.from(markerData.lat, markerData.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_map_marker).setZoomLevel(5))
            val labelOptions = LabelOptions.from(latLng).setStyles(styles)
            val label = kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
            label.tag = markerData
        }

        kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kakaoMap: KakaoMap, layer: LabelLayer, clickedLabel: com.kakao.vectormap.label.Label) {
                val markerData = clickedLabel.tag as? MarkerData
                markerData?.let {
                    selectMarker(it)
                }
            }
        })
    }

    fun addInstituteMarker(kakaoMap: KakaoMap, latLng: LatLng, instituteName: String) {
        val style = LabelStyle.from(R.drawable.ic_map_marker_institute).setZoomLevel(5)
            .setTextStyles(LabelTextStyle.from(40, R.color.point_brown_2))
        val labelOptions = LabelOptions.from(latLng).setStyles(style).setTexts(instituteName)
        kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
    }


    /*
     * 지도 카메라를 특정 위치로 이동합니다.
     * kakaoMap 카메라 이동을 수행할 KakaoMap 객체
     * latLng 이동할 위치의 위도와 경도를 나타내는 LatLng 객체
     */
    fun moveInstitute(kakaoMap: KakaoMap, latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        kakaoMap.moveCamera(cameraUpdate)
    }

    /*
     * 여러 Activity 객체의 위치 정보를 비동기적으로 가져와 마커 데이터 리스트에 저장합니다.
     * activities 위치 정보를 가져올 Activity 객체 리스트
     */
    fun fetchCoordinatesList(activities: List<Activity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredCoordinates = activities.map { activity ->
                async {
                    fetchCoordinates(activity)
                }
            }

            val results = deferredCoordinates.awaitAll().filterNotNull()
            _markerDataList.postValue(results)
        }
    }


    /*
    * 개별 Activity 객체의 위치 정보를 가져와 MarkerData로 변환합니다.
    * @param activity 위치 정보를 가져올 Activity 객체
    * @return 위치가 성공적으로 조회되면 MarkerData 객체, 그렇지 않으면 null
     */
    private suspend fun fetchCoordinates(activity: Activity): MarkerData? {
        val apiKey = "KakaoAK aa23edc0dd8f4cc31ed3c9245040e78d"
        val apiService = KakaoRetrofitClient.apiService()

        return suspendCoroutine { continuation ->
            apiService.searchAddress(apiKey, activity.actLocation.toString()).enqueue(object : Callback<AddressResponse> {
                override fun onResponse(call: Call<AddressResponse>, response: Response<AddressResponse>) {
                    if (response.isSuccessful) {
                        val documents = response.body()?.documents
                        if (!documents.isNullOrEmpty()) {
                            val firstResult = documents[0]
                            val lat = firstResult.y.toDoubleOrNull()
                            val lng = firstResult.x.toDoubleOrNull()
                            continuation.resume(
                                MarkerData(
                                    lat = lat ?: 0.0,
                                    lng = lng ?: 0.0,
                                    title = activity.actTitle.toString(),
                                    address = activity.actLocation.toString(),
                                    description = "활동 설명: ${activity.category}",
                                    organization = "",
                                    recruitmentPeriod = "${activity.noticeStartDate} ~ ${activity.noticeEndDate}",
                                    recruitmentCount = "${activity.recruitTotalNum}",
                                    activityTime = "${activity.actStartTime} ~ ${activity.actEndTime}",
                                    activityPeriod = "${activity.actStartDate} ~ ${activity.actEndDate}"
                                )
                            )
                        } else {
                            continuation.resume(null)
                        }
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
                    continuation.resume(null)
                }
            })
        }
    }
}
