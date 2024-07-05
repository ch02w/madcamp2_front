package com.example.fridge

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.common.KakaoSdk.init

class LoginActivity : AppCompatActivity() {
    private var btnKakaoLogin: Button? = null
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 카카오 SDK 초기화
        init(this, "1c1884724b665157347d412727279fcb")

        btnKakaoLogin = findViewById(R.id.btnKakaoLogin)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        btnKakaoLogin?.setOnClickListener { loginViewModel.loginWithKakao() }

        loginViewModel.getLoginResult().observe(this, Observer { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
