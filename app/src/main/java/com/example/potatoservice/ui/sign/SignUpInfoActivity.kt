package com.example.potatoservice.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.databinding.ActivitySignupInfoBinding
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupInfoBinding
    private var selectedAgeGroup: String? = null
    private var selectedExperience: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSelectionListeners()

        binding.completeButton.setOnClickListener { sendUserInfo() }
    }

    private fun setupSelectionListeners() {
        val ageButtons = mapOf(
            binding.middleSchoolButton to "중학생",
            binding.highSchoolButton to "고등학생",
            binding.universityButton to "대학생",
            binding.adultButton to "성인"
        )
        val experienceButtons = mapOf(
            binding.firstTimeButton to "처음이에요",
            binding.someExperienceButton to "몇번해봤어요",
            binding.lotsExperienceButton to "자주하고있어요"
        )

        ageButtons.forEach { (button, string) ->
            button.setOnClickListener {
                ageButtons.keys.forEach { it.isSelected = false }
                button.isSelected = true
                selectedAgeGroup = string
            }
        }

        experienceButtons.forEach { (button, interest) ->
            button.setOnClickListener {
                experienceButtons.keys.forEach { it.isSelected = false }
                button.isSelected = true
                selectedExperience = interest
            }
        }
    }

    private fun sendUserInfo() {
        val nickname = binding.nicknameEditText.text.toString()
        val ageGroup = selectedAgeGroup
        val experience = selectedExperience

        if (nickname.isNotEmpty() && !ageGroup.isNullOrEmpty() && !experience.isNullOrEmpty()) {
            val userInfo = UserInfo(nickname, ageGroup, experience)

            RetrofitClient.apiService.sendUserInfo(userInfo).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (true) {
                        val intent =
                            Intent(this@SignUpInfoActivity, SignUpInterestActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("testt", "Failure Response: ${response.message()}")
                        Toast.makeText(this@SignUpInfoActivity, "정보 전송 실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("testt", "Request Failed: ${t.message}")
                    Toast.makeText(
                        this@SignUpInfoActivity,
                        "서버 오류: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
