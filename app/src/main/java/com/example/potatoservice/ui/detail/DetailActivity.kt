package com.example.potatoservice.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.potatoservice.R
import com.example.potatoservice.databinding.ActivityDetailBinding
import com.example.potatoservice.ui.share.Volunteer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class DetailActivity : AppCompatActivity() {
	//봉사 활동 id
	private var id = 0
	private lateinit var binding: ActivityDetailBinding
	var institution = ""
	private lateinit var mapView: MapView
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private var curLat: Double = 0.0
	private var curLon: Double = 0.0
	private lateinit var kakaoMap: KakaoMap
	//리뷰 퍼센트지 값
	var review1 = "50"
	var review2 = "40"
	var review3 = "30"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val intent = intent
		//인텐트로 id 받아옴
		id = intent.getIntExtra("id", 0)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
		binding.volunteer = getVolunteer(id)
		binding.detail = this
		setProgress()
		//지도 기능들
		initKakaoMap()
		setMapView()
		binding.buttonCurrentLocation.setOnClickListener {
			moveToCurrentLocation()
		}
	}
	//받아온 id로 봉사 활동 데이터 얻음
	//테스트용 데이터
	private fun getVolunteer(id: Int):Volunteer {
		institution = "조직111111111111111111끝"
		return Volunteer("봉사 활동 제목 12345678912345678911111111끝",
			"조직11111111111111111111111끝", "category111111111111111111끝",
			"recruitmentPeriod",
			"recruitmentNumber11111111111111111111111111끝", "time",
			"serviceRecognitionTime", "location", "확정"
		)
	}
	//리뷰 표시
	private fun setProgress(){
		binding.progressBar1.progress = review1.toInt()
		binding.progressBar2.progress = review2.toInt()
		binding.progressBar3.progress = review3.toInt()
	}
	//지도 기능들
	override fun onResume() {
		super.onResume()
		mapView.resume()
	}

	override fun onPause() {
		super.onPause()
		mapView.pause()
	}

	private fun initKakaoMap() {
		KakaoMapSdk.init(this, getString(R.string.kakao_api_key))
	}
	//카카오맵 초기 설정
	private fun setMapView(){
		mapView = binding.detailMapView
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		mapView.start(object : MapLifeCycleCallback() {
			override fun onMapDestroy() {
				Log.d("testt", "onMapDestroy")
			}

			override fun onMapError(error: Exception) {
				val errorMessage = when ((error as? MapAuthException)?.errorCode) {
					401 -> getString(R.string.error_auth_failure)
					499 -> getString(R.string.error_server_communication_failure)
					else -> getString(R.string.error_unknown)
				}
				Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
			}
		}, object : KakaoMapReadyCallback() {
			override fun onMapReady(kakaoMap: KakaoMap) {
				this@DetailActivity.kakaoMap = kakaoMap
				setInitialCameraPosition()
				setMarker()
			}
		})
	}
	//봉사 활동 장소 마커로 표시
	private fun setMarker() {
		currentLocation {latLng ->
			val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_map_marker).setZoomLevel(5))
			//일단은 현재 위치에 마커를 생성
			val labelOptions = LabelOptions.from(latLng).setStyles(styles)
			// 라벨 추가
			Log.d("testt", "위도: $curLat, 경도: $curLon")
			kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
		}

	}
	//현재 위치 계산
	private fun currentLocation(onLocationRetrieved: (LatLng) -> Unit) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
			ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
			return
		}

		fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
			location?.let {
				curLat = it.latitude
				curLon = it.longitude
				val latLng = LatLng.from(curLat, curLon)
				Log.d("CurrentLocation", "위도: $curLat, 경도: $curLon")
				onLocationRetrieved(latLng) // 위치를 찾았을 때 콜백 호출
			} ?: run {
				Toast.makeText(this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
			}
		}
	}

	// 버튼 클릭 시 현재 위치로 이동하는 함수
	private fun moveToCurrentLocation() {
		currentLocation { latLng ->
			val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
			kakaoMap.moveCamera(cameraUpdate) // 카메라를 현재 위치로 이동
			Toast.makeText(this, "현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
		}
	}
	// 현재 위치로 이동
	private fun setInitialCameraPosition() {
		currentLocation { latLng ->
			val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
			kakaoMap.moveCamera(cameraUpdate)
		}
	}
}