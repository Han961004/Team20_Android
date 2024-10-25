package com.example.potatoservice.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.KakaoSdk
import com.example.potatoservice.MainActivity
import com.example.potatoservice.R
import com.example.potatoservice.databinding.ActivitySignInBinding
import com.example.potatoservice.model.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        KakaoSdk.init(this, getString(R.string.kakao_api_key))
        tryLoginKakao()
    }

    // 카카오 로그인 처리 함수
    private fun tryLoginKakao() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                // 로그인 실패
                Log.e("testt", "Login failed: ${error.message}")
            } else if (token != null) {
                // 로그인 성공
                Log.d("testt", "Login successful, token: ${token.accessToken}")

                // 토큰을 서버에 보내서 회원가입 여부 확인
                checkIfUserIsRegistered(token.accessToken)
            }
        }

        UserApiClient.instance.run {
            if (isKakaoTalkLoginAvailable(this@SignInActivity)) {
                loginWithKakaoTalk(this@SignInActivity, callback = callback)
            } else {
                loginWithKakaoAccount(this@SignInActivity, callback = callback)
            }
        }
    }

    // 서버에 카카오 토큰을 보내 회원가입 여부 확인하는 함수
    private fun checkIfUserIsRegistered(kakaoAccessToken: String) {
        // Retrofit 인스턴스를 통해 API 호출
        val apiService = RetrofitClient.apiService

        // 서버에 해당 사용자가 회원가입 되어 있는지 요청
        apiService.isUserRegistered(kakaoAccessToken).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isRegistered = response.body() ?: false
                    if (isRegistered) {
                        // 사용자가 회원가입 되어 있으면 MainActivity로 이동
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 사용자가 회원가입 안 되어 있으면 SignUpInfoActivity로 이동
                        val intent = Intent(this@SignInActivity, SignUpInfoActivity::class.java)
                        startActivity(intent)
                    }
                    finish()  // 현재 Activity 종료
                } else {
                    Log.e("testt", "서버 에러: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("testt", "요청 실패: ${t.message}")
            }
        })
    }
}
