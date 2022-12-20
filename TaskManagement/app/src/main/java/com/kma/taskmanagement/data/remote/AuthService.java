package com.kma.taskmanagement.data.remote;

import com.kma.taskmanagement.data.model.RefreshTokenResult;
import com.kma.taskmanagement.data.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("refreshtoken")
    Call<RefreshTokenResult> refreshToken(@Body String refreshToken);
}
