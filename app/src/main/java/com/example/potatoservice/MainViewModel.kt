package com.example.potatoservice

import androidx.lifecycle.ViewModel
import com.example.potatoservice.model.remote.HomeData
import com.example.potatoservice.model.remote.MapData
import com.example.potatoservice.ui.home.HomeRepository
import com.example.potatoservice.ui.share.Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    val activityList = homeRepository.activityList
    val loading = homeRepository.loading

    fun searchHomeData(request: Request) {
        homeRepository.search(request)
    }


}

// HomeFragment용 UseCase
class GetHomeDataUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    fun getHomeData(): Flow<List<HomeData>> {
        return homeRepository.activityList.map { activities ->
            activities.map { activity ->
                HomeData(
                    id = activity.actId.toLong(),
                    title = activity.actTitle ?: "제목 없음",
                    location = activity.actLocation ?: "장소 정보 없음",
                    recruitmentCount = activity.recruitTotalNum.toString() // recruitTotalNum 사용
                )
            }
        }
    }
}

// MapFragment용 UseCase
class GetMapDataUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    fun getMapData(): Flow<List<MapData>> {
        return homeRepository.activityList.map { activities ->
            activities.map { activity ->
                MapData(
                    id = activity.actId.toLong(),
                    location = activity.actLocation.toString(),
                    title = activity.actTitle ?: "제목 없음",
                    address = activity.actLocation ?: "장소 정보 없음"
                )
            }
        }
    }
}
