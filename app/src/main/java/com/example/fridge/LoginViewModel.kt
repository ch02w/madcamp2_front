package com.example.fridge;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private ApiService apiService;

    public LoginViewModel(Application application) {
        super(application);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public void loginWithKakao() {
        UserApiClient.getInstance().loginWithKakaoTalk(getApplication(), (OAuthToken token, Throwable error) -> {
            if (error != null) {
                loginResult.setValue(false);
            } else if (token != null) {
                UserApiClient.getInstance().me((user, meError) -> {
                    if (meError != null) {
                        loginResult.setValue(false);
                    } else {
                        // 백엔드와 통신하여 사용자 정보 전달
                        sendUserInfoToBackend(user);
                    }
                    return null;
                });
            }
            return null;
        });
    }

    private void sendUserInfoToBackend(User user) {
        String id = String.valueOf(user.getId());
        String email = user.getKakaoAccount().getEmail();
        String nickname = user.getKakaoAccount().getProfile().getNickname();

        User userInfo = new User(id, email, nickname);
        Call<LoginResponse> call = apiService.login(userInfo);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body().isSuccess());
                } else {
                    loginResult.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResult.setValue(false);
            }
        });
    }
}

