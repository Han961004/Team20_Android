package com.example.potatoservice.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.SplashActivity
import com.example.potatoservice.databinding.ActivitySignupInfoBinding
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.SendSignUpUserInfo
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

    /* 회원 가입 시, 필요 값
    * 버튼 바인딩 -> 닉네임 입력, 나이대 입력, 경험 입력
     */
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

    /* 회원 가입 버튼 누름
    * sharedpreference 에서 jwt 꺼내기
    * 위에서 바인딩한 누른 값을 <객체>로 만들어서 jwt와 함께 서버에 전송
    *
    * 회원 가입한 후 1) Main 으로 값을 넘기기 2) Splash 로 넘기기 3) SignIn 로 넘기기
    *
    * 회원 가입 시 나타날 수 있는 오류
    * response HTTP 코드로 판별할 것
    * HTTP 201 Created
    * HTTP 400 Bad Request : 존재하지 않는 유저
    * HTTP 401 Unauthorized : 유저 토큰 인증 오류
    * HTTP 409 Conflict : 이미 존재하는 닉네임 (중복)
     */
    private fun sendSignUpUserInfo() {
        val nickName = binding.nicknameEditText.text.toString()
        val ageGroup = selectedAgeGroup
        val experience = selectedExperience

        if (nickName.isNotEmpty() && !ageGroup.isNullOrEmpty() && !experience.isNullOrEmpty()) {
            val userInfo = SendSignUpUserInfo(nickName, ageGroup, experience)
            val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("jwt_token", null)

            RetrofitClient.apiService().sendUserInfo("Bearer $jwtToken", userInfo).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        201 -> {
                            Toast.makeText(this@SignUpActivity, "회원가입을 환영합니다, $nickName 님!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignUpActivity, SplashActivity::class.java)
                            // SplashActivity 냐 SignInActivity 냐
                            startActivity(intent)
                            finish()
                        }
                        400 -> {
                            Toast.makeText(this@SignUpActivity, "존재하지 않는 유저입니다.", Toast.LENGTH_SHORT).show()
                        }
                        401 -> {
                            Toast.makeText(this@SignUpActivity, "유효하지 않은 인증입니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        409 -> {
                            Toast.makeText(this@SignUpActivity, "이미 존재하는 닉네임입니다. 다른 닉네임을 사용해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.d("testt", "Failure Response: ${response.message()}")
                            Toast.makeText(this@SignUpActivity, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("testt", "Request Failed: ${t.message}")
                    Toast.makeText(this@SignUpActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

}
