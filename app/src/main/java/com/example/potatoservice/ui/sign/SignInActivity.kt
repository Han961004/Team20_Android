package com.example.potatoservice.ui.sign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.potatoservice.model.remote.AccessTokenRequest
import com.example.potatoservice.model.remote.JwtResponse
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

                // 토큰을 서버에 보내서 JWT 받기
                sendKakaoAccessTokenToServer(token.accessToken)

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

    // 서버에 액세스 토큰을 보내고 JWT를 받아오는 함수
    private fun sendKakaoAccessTokenToServer(kakaoAccessToken: String) {
        val request = AccessTokenRequest(accessToken = kakaoAccessToken)

        RetrofitClient.apiService.sendKakaoAccessToken(request).enqueue(object : Callback<JwtResponse> {
            override fun onResponse(call: Call<JwtResponse>, response: Response<JwtResponse>) {
                if (response.isSuccessful) {
                    // JWT 수신 성공
                    val jwtToken = response.body()?.jwtToken
                    Log.d("testt", "JWT Token received: $jwtToken")

                    // SharedPreferences에 JWT 토큰 저장
                    val sharedPreferences: SharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("jwt_token", jwtToken)
                    editor.apply()

                    // MainActivity로 이동
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()

                } else {
                    // 서버 응답이 실패한 경우
                    Log.e("testt", "Failed to get JWT, response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JwtResponse>, t: Throwable) {
                // 네트워크 오류 등의 실패
                Log.e("testt", "Error in API call: ${t.message}")
            }
        })
    }


}
