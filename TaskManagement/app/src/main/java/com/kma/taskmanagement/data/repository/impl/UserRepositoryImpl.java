package com.kma.taskmanagement.data.repository.impl;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.RegisterResponse;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.remote.UserService;
import com.kma.taskmanagement.data.remote.request.ChangePassRequest;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.listener.HandleResponse;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository {

    private UserService userService;

    public UserRepositoryImpl() {
        this.userService = RetrofitInstance.getRetrofitInstance().create(UserService.class);
    }

    @Override
    public void singUp(RegisterRequest registerRequest, HandleResponse handleResponse) {
        Call<RegisterResponse> initateRegister = userService.signUp(registerRequest);
        initateRegister.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.isSuccessful()) {
                    handleResponse.onResponse(response.body(), 200);
                } else {
                    handleResponse.onResponse(null, response.code());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                handleResponse.onFailure(t);
            }
        });
    }

    @Override
    public Observable<User> update(String token, long id, User user) {
        return userService.update(token, id, user);
    }

    @Override
    public Observable<User> getInfo(String token, long userId) {
        return userService.getInfo(token, userId);
    }

    @Override
    public void singin(LoginRequest loginRequest, HandleResponse handleResponse) {
        Call<Token> initiateLogin = userService.login(loginRequest);
        initiateLogin.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()) {
                    handleResponse.onResponse(response.body(), 200);
                } else {
                    handleResponse.onResponse(null, response.code());
                }
             }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                handleResponse.onFailure(t);
            }
        });
    }

    @Override
    public void refresh(String refreshToken, HandleResponse handleResponse) {
        Call<Token> refreshCall = userService.refreshToken(refreshToken);
        refreshCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()) {
                    handleResponse.onResponse(response.body(), 200);
                } else {
                    handleResponse.onResponse(null, response.code());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                handleResponse.onFailure(t);
            }
        });
    }

    @Override
    public Completable changepass(String authHeader, ChangePassRequest changePassRequest) {
        return userService.changepass(authHeader, changePassRequest);
    }

    @Override
    public Completable forgotPass(String authHeader, ChangePassRequest changePassRequest) {
        return userService.forgotPass(authHeader, changePassRequest);
    }
}
