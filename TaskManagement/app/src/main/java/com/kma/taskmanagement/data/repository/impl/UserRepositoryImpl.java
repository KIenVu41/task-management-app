package com.kma.taskmanagement.data.repository.impl;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.remote.UserService;
import com.kma.taskmanagement.data.repository.UserRepository;

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
    public Completable signUp(RegisterRequest registerRequest) {
        return userService.signUp(registerRequest);
    }

    @Override
    public void singin(LoginRequest loginRequest, ILoginResponse loginResponse) {
        Call<Token> initiateLogin = userService.login(loginRequest);
        initiateLogin.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()) {
                    loginResponse.onResponse(response.body());
                }
             }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loginResponse.onFailure(t);
            }
        });
    }

    public interface ILoginResponse{
        void onResponse(Token token);
        void onFailure(Throwable t);
    }

//    @Override
//    public Single<Token> singin(LoginRequest loginRequest) {
//        return userService.login(loginRequest);
//    }
}
