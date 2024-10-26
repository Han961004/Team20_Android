package com.example.potatoservice.ui.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyPageViewModelFactory(
    private val context: Context,
    private val myPageModel: MyPageModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(context, myPageModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
