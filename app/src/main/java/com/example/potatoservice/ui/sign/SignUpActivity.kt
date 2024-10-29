package com.example.potatoservice.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.MainActivity
import com.example.potatoservice.databinding.ActivitySignupInfoBinding
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.SendSignUpUserInfo
import com.example.potatoservice.model.remote.SignUpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupInfoBinding
    private var selectedAgeGroup: String? = null
    private var selectedExperience: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSelectionListeners()
        binding.completeButton.setOnClickListener { sendSignUpUserInfo() }
    }

    private fun setupSelectionListeners() {
        val ageButtons = mapOf(
            binding.middleSchoolButton to "중학생",
            binding.highSchoolButton to "고등학생",
            binding.universityButton to "대학생",
            binding.adultButton to "성인"
        )

        ageButtons.forEach { (button, string) ->
            button.setOnClickListener {
                ageButtons.keys.forEach { it.isSelected = false }
                button.isSelected = true
                selectedAgeGroup = string
            }
        }

        val experienceButtons = mapOf(
            binding.firstTimeButton to "처음이에요",
            binding.someExperienceButton to "몇번해봤어요",
            binding.lotsExperienceButton to "자주하고있어요"
        )

        experienceButtons.forEach { (button, interest) ->
            button.setOnClickListener {
                experienceButtons.keys.forEach { it.isSelected = false }
                button.isSelected = true
                selectedExperience = interest
            }
        }
    }

    private fun sendSignUpUserInfo() {
        val nickName = binding.nicknameEditText.text.toString()
        val ageGroup = selectedAgeGroup
        val experience = selectedExperience

        if (nickName.isNotEmpty() && !ageGroup.isNullOrEmpty() && !experience.isNullOrEmpty()) {
            val userInfo = SendSignUpUserInfo(nickName, ageGroup, experience)

            // SharedPreferences에서 JWT 토큰 가져오기
            val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("jwt_token", null)

            RetrofitClient.apiService().sendUserInfo("Bearer $jwtToken", userInfo).enqueue(object : Callback<SignUpRequest> {
                override fun onResponse(call: Call<SignUpRequest>, response: Response<SignUpRequest>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, "회원가입을 환영합니다, $nickName 님!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("testt", "Failure Response: ${response.message()}")
                        Toast.makeText(this@SignUpActivity, "정보 전송 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignUpRequest>, t: Throwable) {
                    Log.d("testt", "Request Failed: ${t.message}")
                    Toast.makeText(this@SignUpActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
