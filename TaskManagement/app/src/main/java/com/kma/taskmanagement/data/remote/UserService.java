package com.kma.taskmanagement.data.remote;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.ui.user.RegisterActivity;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @POST("api/v1/users/register")
    Completable signUp(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<Token> login(@Body LoginRequest loginRequest);

//    @POST("login")
//    Single<Token> login(@Body LoginRequest loginRequest);

}
