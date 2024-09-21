package com.example.potatoservice.ui.map

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.databinding.ActivityMapBinding
import com.example.potatoservice.R

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initKakaoMap()
        initMapView()
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(this, getString(R.string.kakao_api_key))
    }

    private fun initMapView() {
        mapView = binding.mapView
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("testt", "onMapDestroy")
            }

            override fun onMapError(error: Exception) {
                val errorMessage = when ((error as? MapAuthException)?.errorCode) {
                    401 -> "API 인증에 실패"
                    499 -> "서버 통신 실패"
                    else -> "알 수 없는 오류"
                }
                Toast.makeText(this@MapActivity, errorMessage, Toast.LENGTH_SHORT).show()
                finish()
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@MapActivity.kakaoMap = kakaoMap
                setInitialCameraPosition()
            }
        })
    }

    private fun setInitialCameraPosition() {
        val defaultLatitude = 37.5665
        val defaultLongitude = 126.978

        val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(defaultLatitude, defaultLongitude))
        kakaoMap.moveCamera(cameraUpdate)
    }

    override fun onResume() {
        super.onResume()
        Log.d("testt", "onResume")
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("testt", "onPause")
        mapView.pause()
    }
}