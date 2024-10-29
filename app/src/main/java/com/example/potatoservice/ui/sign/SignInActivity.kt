package com.example.potatoservice.ui.sign

import android.content.Context
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
import com.example.potatoservice.model.remote.AccessToken
import com.example.potatoservice.model.remote.LoginRequest
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
                Log.e("testt", "Login failed: ${error.message}")
            } else if (token != null) {
                Log.d("testt", "Login successful, token: ${token.accessToken}")
                sendAccessTokenToServer(token.accessToken)  // 서버로 인가 코드를 전송하고 JWT 토큰을 받아오는 메서드 호출
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

    // 서버로 인가 코드 전송 및 JWT 토큰 받아오기
    private fun sendAccessTokenToServer(accessToken: String) {
        val accessTokenRequest = AccessToken(accessToken)

        RetrofitClient.apiService().kakaoLogin(accessTokenRequest).enqueue(object : Callback<LoginRequest> {
            override fun onResponse(call: Call<LoginRequest>, response: Response<LoginRequest>) {
                if (response.isSuccessful) {
                    val jwtToken = response.headers()["Authorization"]
                    val userInfo = response.body()?.userInfo

                    if (jwtToken != null) {
                        Log.d("testt", "Received JWT token: $jwtToken")
                        val sharedPref = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("jwt_token", jwtToken)
                            apply()
                        }
                        Log.d("testt", "JWT token saved to SharedPreferences")

                        // 회원 가입 여부에 따라 화면 이동
                        // 현재 != 및 == 로 분기 설정중
                        if (userInfo != null) {
                            // 유저가 존재하면 MainActivity로 이동
                            val intent = Intent(this@SignInActivity, MainActivity::class.java).apply {
                                putExtra("jwt_token", jwtToken)
                                putExtra("nickname", userInfo.nickname)
                                putExtra("ageGroup", userInfo.ageGroup)
                                putExtra("experience", userInfo.experience)
                                putExtra("level", userInfo.level)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            // 유저가 없으면 SignUpActivity로 이동
                            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.e("testt", "JWT token not found in headers")
                    }
                } else {
                    Log.e("testt", "Backend login failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<LoginRequest>, t: Throwable) {
                Log.e("testt", "Backend login error: ${t.message}")
            }
        })
    }
}
