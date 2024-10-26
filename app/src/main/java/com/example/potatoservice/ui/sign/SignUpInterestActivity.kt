package com.example.potatoservice.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.MainActivity
import com.example.potatoservice.databinding.ActivitySignupInterestBinding
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.UserInterest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpInterestActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupInterestBinding
    private val interestList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupInterestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInterestSelection()

        binding.completeButton.setOnClickListener { sendUserInterests() }
    }

    private fun setupInterestSelection() {
        val interestCards = mapOf(
            binding.educationCard to "교육/멘토링",
            binding.supportCard to "생활편의지원",
            binding.culturalEventCard to "문화행사",
            binding.medicalCard to "보건의료",
            binding.safetyCard to "안전예방",
            binding.internationalEventCard to "국제행사"
        )

        interestCards.forEach { (card, interest) ->
            card.setOnClickListener {
                if (interestList.contains(interest)) {
                    interestList.remove(interest)
                    card.isSelected = false
                } else {
                    interestList.add(interest)
                    card.isSelected = true
                }
            }
        }
    }

    private fun sendUserInterests() {
        val userInterest = UserInterest(interestList)

        RetrofitClient.apiService.sendUserInterest(userInterest)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (true) {
                        val intent = Intent(this@SignUpInterestActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("testt", "Failure Response Code: ${response.code()}")
                        Log.d("testt", "Failure Response Body: $errorBody")
                        Toast.makeText(
                            this@SignUpInterestActivity,
                            "관심사 전송 실패: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("testt", "Request Failed: ${t.message}")
                    Toast.makeText(
                        this@SignUpInterestActivity,
                        "관심사 전송 실패: ${t.message}}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
