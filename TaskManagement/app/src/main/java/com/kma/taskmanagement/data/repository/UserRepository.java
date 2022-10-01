package com.kma.taskmanagement.data.repository;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface UserRepository {
    Completable signUp(RegisterRequest registerRequest);

//    Single<Token> singin(LoginRequest loginRequest);

    void singin(LoginRequest loginRequest, UserRepositoryImpl.ILoginResponse loginResponse);
}
