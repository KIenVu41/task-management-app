package com.kma.taskmanagement.data.repository;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.listener.HandleResponse;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UserRepository {
//    Completable signUp(RegisterRequest registerRequest);
    void singUp(RegisterRequest registerRequest, HandleResponse handleResponse);
//    Single<Token> singin(LoginRequest loginRequest);
    Observable<User> update(String token, long id, User user);

    Observable<User> getInfo(String token, long userId);

    void singin(LoginRequest loginRequest, HandleResponse handleResponse);
}
