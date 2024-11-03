package com.example.potatoservice.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.potatoservice.R
import com.example.potatoservice.databinding.FragmentMapBinding
import com.example.potatoservice.model.remote.MarkerData
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import android.location.Location as AndroidLocation

class MapFragment : Fragment() {
    private var kakaoMap: KakaoMap? = null
    private val mapViewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        binding.buttonCurrentLocation.setOnClickListener { moveToCurrentLocation() }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false).apply {
            viewModel = this@MapFragment.mapViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private fun initMap() {
        KakaoMapSdk.init(requireContext(), getString(R.string.kakao_api_key))
        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() { }
            override fun onMapError(error: Exception) {
                val errorMessage = when ((error as? MapAuthException)?.errorCode) {
                    401 -> getString(R.string.error_auth_failure)
                    499 -> getString(R.string.error_server_communication_failure)
                    else -> getString(R.string.error_unknown)
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@MapFragment.kakaoMap = kakaoMap

                // 마지막 저장된 카메라 위치 설정
                val lastLocation = mapViewModel.getLastLocation()
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(
                    LatLng.from(lastLocation.first, lastLocation.second), lastLocation.third
                )
                kakaoMap.moveCamera(cameraUpdate)

                // 카메라 이동 종료 리스너
                kakaoMap.setOnCameraMoveEndListener { _, _, _ ->
                    val position = kakaoMap.cameraPosition!!.position
                    val currentZoom = kakaoMap.cameraPosition!!.zoomLevel
                    mapViewModel.saveLastLocation(
                        position.latitude,
                        position.longitude,
                        currentZoom
                    )
                }

                // 지도 클릭 리스너 추가
                kakaoMap.setOnMapClickListener { _, _, _, _ ->
                    hideCardView()
                }

//                mapViewModel.setMarkerData()
                mapViewModel.markerDataList.observe(viewLifecycleOwner) { markerDataList ->
                    markerDataList?.let {
                        mapViewModel.addMarkersToMap(kakaoMap)
                    }
                }

                mapViewModel.selectedMarker.observe(viewLifecycleOwner) { markerData ->
                    markerData?.let {
                        updateCardView(it)
                        showCardView()
                    } ?: hideCardView()
                }
                // 디테일에서 기관 정보 얻음
                getInstituteLocation(kakaoMap)

            }
        })
    }

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener { location: AndroidLocation? ->
            location?.let {
                val curLat = it.latitude
                val curLon = it.longitude
                val latLng = LatLng.from(curLat, curLon)
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
                kakaoMap?.moveCamera(cameraUpdate)
            } ?: run {
                Toast.makeText(requireContext(), "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 카드뷰에 정보를 업데이트하는 함수
    private fun updateCardView(markerData: MarkerData) {
        binding.titleText.text = markerData.title
        binding.serviceOrganizationServiceCategory.text = "${markerData.organization}"
        binding.serviceRecruitment.text = "${markerData.recruitmentPeriod} | 모집 인원: ${markerData.recruitmentCount}"
        binding.serviceTime.text = "${markerData.activityPeriod} | 활동 시간: ${markerData.activityTime}"
        binding.descriptionText.text = "${markerData.address} \n${markerData.description}"
    }

    // CardView를 보이게 설정
    private fun showCardView() {
        binding.infoCardView.visibility = View.VISIBLE
    }

    // CardView를 숨김
    private fun hideCardView() {
        binding.infoCardView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //디테일에서 기관 정보 얻음
    private fun getInstituteLocation(kakaoMap: KakaoMap) {
        val name = this.arguments?.getString("name") ?: "기관명"
        val latitude = this.arguments?.getDouble("latitude")
        val longitude = this.arguments?.getDouble("longitude")
        //기관 정보가 있으면
        if (latitude != null && longitude != null) {
            val latLng = LatLng.from(latitude, longitude)
            mapViewModel.addInstituteMarker(kakaoMap, latLng, name)
            mapViewModel.moveInstitute(kakaoMap, latLng)
        }
    }


}
