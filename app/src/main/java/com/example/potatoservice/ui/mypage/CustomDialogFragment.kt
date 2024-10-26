package com.example.potatoservice.ui.mypage

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.potatoservice.R
import com.example.potatoservice.databinding.DialogCustomBinding

class CustomDialogFragment() : DialogFragment() {

    private lateinit var binding: DialogCustomBinding

    // 다이얼로그에서 발생한 클릭 이벤트를 전달할 인터페이스
    interface OnDialogButtonClickListener {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
    }

    private var listener: OnDialogButtonClickListener? = null

    companion object {
        private const val DIALOG_TITLE = "title"
        private const val DIALOG_IMAGE = "image"
        private const val DIALOG_CONTENT = "content"
        private const val DIALOG_POSIVIVE_BUTTON = "positive"
        private const val DIALOG_NEGATIVE_BUTTON = "negative"

        // newInstance 메서드로 매개변수 전달
        fun newInstance(dialogModel: DialogModel): CustomDialogFragment {
            val fragment = CustomDialogFragment()
            val args = Bundle()
            args.putString(DIALOG_TITLE, dialogModel.title)
            args.putInt(DIALOG_IMAGE, dialogModel.imageBackground)
            args.putString(DIALOG_CONTENT, dialogModel.content)
            args.putString(DIALOG_POSIVIVE_BUTTON, dialogModel.positiveButtonText)
            args.putString(DIALOG_NEGATIVE_BUTTON, dialogModel.negativeButtonText)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogCustomBinding.inflate(inflater, container, false)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))   //모서리 투명하게, 없어도 되는 것 같기도..
//        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE) //다이얼로그의 타이틀바 제거 //없어도 되는 것 같기도..

        // 매개변수로 전달받은 값 사용
        val title = arguments?.getString(DIALOG_TITLE) ?: "default_title"
        val imageResId = arguments?.getInt(DIALOG_IMAGE) ?: R.drawable.potato_lv1
        val content = arguments?.getString(DIALOG_CONTENT) ?: "deault_content"
        val positiveButton = arguments?.getString(DIALOG_POSIVIVE_BUTTON) ?: "네"
        val negativeButton = arguments?.getString(DIALOG_NEGATIVE_BUTTON) ?: "아니오"

        // 전달받은 데이터를 다이얼로그 UI에 설정
        binding.customDialogTitle.text = title
        binding.customDialogImage.setImageResource(imageResId ?: 0)
        binding.customDialogContent.text = content
        binding.customDialogPositive.text = positiveButton
        binding.customDialogNegative.text = negativeButton

        // 닫기 버튼 이벤트 처리
        binding.customDialogClose.setOnClickListener {
//            dismiss()
            //todo 중간에 닫기로 나갔을 때 로직 구현
        }

        // 긍정 버튼 이벤트 처리
        binding.customDialogPositive.setOnClickListener {
            listener?.onPositiveButtonClick()
            dismiss()
        }

        // 부정 버튼 이벤트 처리
        binding.customDialogNegative.setOnClickListener {
            listener?.onNegativeButtonClick()
            dismiss()
        }

        return binding.root
    }


    // 사이즈 조절
    override fun onStart() {
        super.onStart()

        // 화면 크기 가져오기
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // 다이얼로그의 크기를 화면 비율로 설정 (예: 너비 80%, 높이 50%)
        val dialogWidth = (screenWidth * 0.8).toInt()  // 너비 80%
        val dialogHeight = (screenHeight * 0.5).toInt() // 높이 50%

        // 다이얼로그 크기 설정
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    fun setDialogListener(listener: OnDialogButtonClickListener) {
        this.listener = listener
    }

}


