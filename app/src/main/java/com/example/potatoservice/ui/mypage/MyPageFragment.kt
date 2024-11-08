package com.example.potatoservice.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.potatoservice.databinding.FragmentMypageBinding
import com.example.potatoservice.ui.share.Volunteer

class MyPageFragment : Fragment(), OnVolunteerClickListener, CustomDialogFragment.OnDialogButtonClickListener{

    private lateinit var binding: FragmentMypageBinding
    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var customDialog : CustomDialogFragment
    private lateinit var dialogArray : Array<DialogModel>



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val myPageModel = MyPageModel(requireContext(),mainViewModel)
        val factory = MyPageViewModelFactory(requireContext())
        myPageViewModel = ViewModelProvider(this, factory).get(MyPageViewModel::class.java)
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        binding.myPageSpinner.adapter = myPageViewModel.vmSpinnerAdapter

        dialogArray = myPageViewModel.vmDialogArray
        observeDialogModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 김동한
        * 확인차 일단 MyPageFragment에 들어갈 때마다 잘 나오는지 Log를 찍어봤습니다. 나중에 지우셔도 됩니다.
        * 단, 걱정되는게, 제 예상으로는 이게 로그인 한 다음 부터는 MyPage에서 경험치가 올라가도 실시간 반영이 안 될 수도 있습니다. 10시간이 채워져도 1레벨 -> 1레벨 그대로 일 수 있다는 것 입니다.
        * 해결 방안은 아마 retrofit으로 경험치를 실시간 주고 받아야 하거나, 참조를 sharedpreferences 가 아니라 따로 ViewModel에 저장시켜 놓으시는 게 좋을 것 같습니다.
         */
//        myPageViewModel.jwtToken.observe(viewLifecycleOwner) { jwtToken -> Log.d("testt", "MyPage JWT Token: $jwtToken") }
//        myPageViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
//            Log.d("testt", "MyPage User Info: $userInfo")
//        }


//        myPageViewModel.setVolunteerHours()
//        myPageViewModel.setVolunteerCount()
//        myPageViewModel.setRecyclerViewCount()
        setUpInit()

    }

    private fun setUpInit(){
        setupProgressBar()
        setupRecyclerView()
        setupTvLevel()
        setupTvTotalHours()
        setupTvTotalCount()
        setupRecyclerViewCount()
        setupNickname()
    }

    //nickname 설정 함수
    private fun setupNickname(){
        myPageViewModel.vmNickname.observe(viewLifecycleOwner){
            binding.tvNickname.text = it
        }
    }

    // ProgressBar 설정 함수
    private fun setupProgressBar() {
        // ViewModel의 progress 값을 관찰하고 ProgressBar에 반영
        myPageViewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = progress
        }

        myPageViewModel.progressPercent.observe(viewLifecycleOwner){
            binding.tvProgressPercent.text = "${it}%"
        }
    }

    //총 봉사 건수 설정
    private fun setupTvTotalCount(){
        myPageViewModel.vmVolunteerCount.observe(viewLifecycleOwner, Observer {
            binding.tvTotalVolunteerCount.text = "총 봉사 건수 : ${it} 건"
        })
    }

    //총 봉사 시간 설정
    private fun setupTvTotalHours(){
        myPageViewModel.vmVolunteerHours.observe(viewLifecycleOwner, Observer {
            binding.tvTotalHours.text = "총 봉사 시간 : ${it}"
        })
    }

    // 레벨 설정

    private fun setupTvLevel(){
        myPageViewModel.vmLevel.observe(viewLifecycleOwner, Observer {
            binding.tvLevel.text = "Lv. ${it}"
        })
    }


    private fun setupRecyclerViewCount(){
        myPageViewModel.vmRecyclerViewCount.observe(viewLifecycleOwner, Observer {
            binding.mypageRecyclerViewCount.text = "총 ${it}건"
        })
    }




    // RecyclerView 설정 함수
    private fun setupRecyclerView() {

        // 예시 데이터 리스트 생성
        //todo mvvm패턴 변경하기
//        val exVolunteerList = listOf(
//            Volunteer(1,"서버로부터 못 받아온거임", "기관 A", "교육",
//                "2024.09.01 ~ 2024.09.30", 5,
//                "2024.10.01 ~ 2024.10.31", "132시간", "서울특별시", "확정 대기 중")
//        )
//        val volunteers = MyPageModel.volunteerList.value ?: exVolunteerList
//        Log.d("seyoung","MyPageFragment_어댑터 설정하기===${MyPageModel.volunteerList.value}")

        // 어댑터 설정
        binding.recyclerView.adapter = myPageViewModel.vmVolunteerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeDialogModel() {
        // 다이얼로그 모델 관찰
        myPageViewModel.currentDialogModel.observe(viewLifecycleOwner) { dialogModel ->
            dialogModel?.let {
                val customDialog = CustomDialogFragment.newInstance(it)
                customDialog.setDialogListener(this)
                customDialog.show(parentFragmentManager, "customDialog")
            }
        }

        // 긍정/부정 응답 횟수 관찰
        myPageViewModel.positiveCount.observe(viewLifecycleOwner) { positiveCount ->
            //todo
        }

        myPageViewModel.negativeCount.observe(viewLifecycleOwner) { negativeCount ->
            //todo
        }
    }

    override fun onVolunteerClick(volunteer: Volunteer) {
        myPageViewModel.showNextDialog() // 다이얼로그 표시 요청
    }

    override fun onPositiveButtonClick() {
        myPageViewModel.onPositiveButtonClick() // 긍정 응답 처리
    }

    override fun onNegativeButtonClick() {
        myPageViewModel.onNegativeButtonClick() // 부정 응답 처리
    }


}
