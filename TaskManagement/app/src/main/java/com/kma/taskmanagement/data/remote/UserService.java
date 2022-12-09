package com.kma.taskmanagement.data.remote;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.RegisterResponse;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.request.ChangePassRequest;
import com.kma.taskmanagement.ui.user.RegisterActivity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @POST("api/v1/users/register")
    Call<RegisterResponse> signUp(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<Token> login(@Body LoginRequest loginRequest);

    @GET("api/v1/users/{id}")
    Observable<User> getInfo(@Header("Authorization") String authHeader, @Path("id") long userId);

    @PUT("api/v1/users/{id}")
    Observable<User> update(@Header("Authorization") String authHeader, @Path("id") long userId, @Body User user);

    @PUT("api/v1/users")
    Completable changepass(@Header("Authorization") String authHeader, @Body ChangePassRequest changePassRequest);

    @PUT("api/v1/users/reset")
    Completable forgotPass(@Header("Authorization") String authHeader, @Body ChangePassRequest changePassRequest);
}
