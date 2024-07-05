package com.example.fridge

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val loginResult = MutableLiveData<Boolean>()
    private val apiService: ApiService
    private val sharedPreferences: SharedPreferences

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java) //Retrofit은 Java의 라이브러리이므로 코틀린 -> 자바 클래스로 변환
        sharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    fun getLoginResult(): LiveData<Boolean> {
        return loginResult
    }

    fun loginWithKakao() {
        UserApiClient.instance.loginWithKakaoTalk(getApplication<Application>()) { token: OAuthToken?, error: Throwable? ->
            if (error != null) {
                Log.e("LoginViewModel", "Login with Kakao failed", error)
                loginResult.value = false
            } else if (token != null) {
                UserApiClient.instance.me { user: User?, meError: Throwable? ->
                    if (meError != null || user == null) {
                        Log.e("LoginViewModel", "Fetching user info failed", meError)
                        loginResult.value = false
                    } else {
                        // 백엔드와 통신하여 사용자 정보 전달
                        sendUserInfoToBackend(user)
                    }
                }
            }
        }
    }

    private fun sendUserInfoToBackend(user: User) {
        val id: String = user.id.toString()
        val nickname = user.kakaoAccount?.profile?.nickname


        val call = apiService.login(user)

        call?.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    // 로그인 성공 시 SharedPreferences에 사용자 정보 저장
                    val editor = sharedPreferences.edit()
                    editor.putString("id", id) // 사용자의 아이디 저장
                    editor.putString("nickname", nickname) // 사용자의 닉네임 저장
                    editor.apply()
                    loginResult.value = response.body()!!.isSuccess
                } else {
                    loginResult.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                loginResult.value = false
            }
        })
    }
}
