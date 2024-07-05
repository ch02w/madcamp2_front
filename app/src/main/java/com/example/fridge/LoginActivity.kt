package com.example.fridge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

public class LoginActivity extends AppCompatActivity {

    private Button btnKakaoLogin;
    private final LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 카카오 SDK 초기화
        KakaoSdk.init(this, "1c1884724b665157347d412727279fcb");

        btnKakaoLogin = findViewById(R.id.btnKakaoLogin);
        btnKakaoLogin.setOnClickListener(view -> loginViewModel.loginWithKakao());

        loginViewModel.getLoginResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (isSuccess) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
