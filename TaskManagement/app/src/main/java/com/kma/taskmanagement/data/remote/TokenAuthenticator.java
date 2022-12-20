package com.kma.taskmanagement.data.remote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.data.model.RefreshTokenResult;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.listener.HandleResponse;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

import java.io.IOException;

import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;

public class TokenAuthenticator implements Authenticator {

    private AuthService authService;
    private String token = "";
    private String refreshToken = "";
    private boolean isSucces = false;

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        token = SharedPreferencesUtil.getInstance(TaskApplication.getAppContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        refresh(token);

        return response.request().newBuilder()
                .header("Authorization", refreshToken)
                .build();
    }

    public void refresh(String token) {
        Call<RefreshTokenResult> call = RetrofitInstance.authService.refreshToken(token);
        try
        {
            retrofit2.Response<RefreshTokenResult> response = call.execute();
            RefreshTokenResult apiResponse = response.body();
            SharedPreferencesUtil.getInstance(TaskApplication.getAppContext()).storeUserToken(Constants.TOKEN + GlobalInfor.username, apiResponse.getRefreshToken());
            refreshToken = apiResponse.getRefreshToken();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}