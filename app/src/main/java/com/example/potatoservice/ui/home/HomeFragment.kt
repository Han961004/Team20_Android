package com.example.potatoservice.ui.home
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.potatoservice.databinding.FragmentHomeBinding
import com.example.potatoservice.ui.detail.DetailActivity
import com.example.potatoservice.ui.share.AdapterCallback
import com.example.potatoservice.ui.share.Request
import com.example.potatoservice.ui.share.SpinnerHintAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), AdapterCallback {
    //선택된 시도 코드 값
    private var sidoCode: Int? = null
    //선택된 군구 코드 값
    private var gunguCode: Int? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var searchResultAdapter: SearchResultAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    var numberOfElements: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.home = this
        setRecyclerAdapter()
        setSpinner()
        showSpinnerLoading()
        showSearchLoading()
        //검색 버튼 클릭 시
        binding.searchButton.setOnClickListener {
            val page = 0
            val size: Int? = null
            val sort: String? = null
            val beforeDeadlineOnly: Boolean? = null
            val teenPossibleOnly: Boolean? = null
            val category: String? = null
            //군구 코드가 있으면 시도 코드 자리를 널로 함.
            val request = if(gunguCode != null) {
                Request(page, size, sort, null, gunguCode,beforeDeadlineOnly, teenPossibleOnly, category)
            } else{
                Request(page, size, sort, sidoCode, null,beforeDeadlineOnly, teenPossibleOnly, category)
            }
            homeViewModel.search(request)
        }
        getNumberOfElements()
        return binding.root
    }
    //검색 결과 개수 업데이트
    private fun getNumberOfElements(){
        homeViewModel.numberOfElements.observe(viewLifecycleOwner, Observer {
            numberOfElements = it
            binding.invalidateAll()
        })
    }
    //검색 로딩 화면 설정
    private fun showSearchLoading() {
        homeViewModel.searchLoading.observe(viewLifecycleOwner, Observer { loading ->
            if (loading) {
                binding.searchResultRecyclerView.visibility = View.GONE
                binding.loadingShimmer.visibility = View.VISIBLE
                binding.loadingShimmer.startShimmer()
            } else {
                binding.loadingShimmer.stopShimmer()
                binding.loadingShimmer.visibility = View.GONE
                binding.searchResultRecyclerView.visibility = View.VISIBLE
            }
        })

    }
    //초기 스피너 설정 시 로딩 화면 구현
    private fun showSpinnerLoading() {
        homeViewModel.sidoLoading.observe(viewLifecycleOwner, Observer { sidoLoading ->
            if(sidoLoading or homeViewModel.gunguLoading.value!!){
                //로딩 시작
                binding.homeLayout.visibility = View.GONE
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loadingLayout.startShimmer()
            }else{
                //로딩 종료
                binding.loadingLayout.stopShimmer()
                binding.loadingLayout.visibility = View.GONE
                binding.homeLayout.visibility = View.VISIBLE
            }
        })
        homeViewModel.gunguLoading.observe(viewLifecycleOwner, Observer { gunguLoading ->
            if(gunguLoading or homeViewModel.sidoLoading.value!!){
                //로딩 시작
                binding.homeLayout.visibility = View.GONE
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loadingLayout.startShimmer()
            }else{
                //로딩 종료
                binding.loadingLayout.stopShimmer()
                binding.loadingLayout.visibility = View.GONE
                binding.homeLayout.visibility = View.VISIBLE
            }
        })
    }

    //검색 결과 리사이클러뷰 설정
    private fun setRecyclerAdapter() {
        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(activity)
        searchResultAdapter = SearchResultAdapter(this)
        homeViewModel.activityList.observe(viewLifecycleOwner, Observer { service ->
            searchResultAdapter.submitList(service)
            binding.searchResultRecyclerView.adapter = searchResultAdapter
        })
    }

    //필터들 설정
    private fun setSpinner() {
        //시도 리스트 받아 오기
        homeViewModel.searchSidoList()
        //군구 리스트 받아 오기
        homeViewModel.searchGunguList()
        // 정렬 스피너 설정
        val sortAdapter = ArrayAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.sortList
        )
        sortAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.sort.adapter = sortAdapter
        //정렬 선택 시
        binding.sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        homeViewModel.sidoList.observe(viewLifecycleOwner, Observer {sidoGunguList ->
            //지역 대분류 시도 이름 리스트 저장
            val majorRegoinList = mutableListOf("지역 대분류")
            majorRegoinList.addAll(sidoGunguList.map { sidoGungu -> sidoGungu.sidoName })
            // 지역 대분류 시도 코드 리스트 저장
            val majorSidoCodeList = mutableListOf<Int>(0)
            majorSidoCodeList.addAll(sidoGunguList.map { sidoGungu -> sidoGungu.sidoCode })
            // 지역 대분류 스피너 설정
            val majorRegionAdapter = SpinnerHintAdapter(
                requireContext(),
                com.example.potatoservice.R.layout.spinner_item,
                majorRegoinList
            )
            majorRegionAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
            binding.majorRegionalCategories.adapter = majorRegionAdapter
            //지역 대분류 선택 시
            binding.majorRegionalCategories.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        //지역 대분류 선택 시 선택된 시도 코드 저장
                        var majorSidoCode: Int? = 0
                        if (position != 0){
                            sidoCode = majorSidoCodeList[position]
                            majorSidoCode = sidoCode
                        }else{
                            sidoCode = null
                        }
                        //지역 대분류 선택에 따라 소분류 목록이 바뀜
                        val minorRegionList = homeViewModel.mappingGunguCode()[majorSidoCode]?.map {list->
                            list[0] as String
                        }
                        val minorRegionAdapter =SpinnerHintAdapter(
                            requireContext(),
                            com.example.potatoservice.R.layout.spinner_item,
                            minorRegionList
                        )
                        minorRegionAdapter.setDropDownViewResource(
                            com.example.potatoservice.R.layout.spinner_item_dropdown)
                        binding.minorRegionalCategories.adapter = minorRegionAdapter
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
        })

        //지역 소분류 스피너 설정
        val minorRegionAdapter =SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.mappingGunguCode()[0]?.get(0) as List<String>
        )
        minorRegionAdapter.setDropDownViewResource(
            com.example.potatoservice.R.layout.spinner_item_dropdown)
        binding.minorRegionalCategories.adapter = minorRegionAdapter
        //지역 소분류 선택 시
        binding.minorRegionalCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    gunguCode = if (position != 0){
                        homeViewModel.mappingGunguCode()[sidoCode]?.get(position)?.get(1) as Int
                    }else{
                        null
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }


        // 봉사 분야 스피너 설정
        val volunteerActivitiesAdapter = SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.volunteerList
        )
        volunteerActivitiesAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.volunteerActivitiesCategories.adapter = volunteerActivitiesAdapter
        //봉사 분야 선택 시
        binding.volunteerActivitiesCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }


        // 나이 제한 스피너 설정
        val ageAdapter = SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.ageList
        )
        ageAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.ageCategories.adapter = ageAdapter
        //나이 제한 선택 시
        binding.ageCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    override fun onClicked(id: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("id", id) // 데이터 추가
        startActivity(intent)
    }



}