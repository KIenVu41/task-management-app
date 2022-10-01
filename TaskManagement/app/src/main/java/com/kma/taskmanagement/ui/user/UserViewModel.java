package com.kma.taskmanagement.ui.user;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;

import java.util.function.LongToDoubleFunction;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    MutableLiveData<Integer> mProgressMutableData = new MutableLiveData<>();
    MutableLiveData<String> mLoginResultMutableData = new MutableLiveData<>();
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public UserViewModel(UserRepository userRepository) {
        mProgressMutableData.postValue(View.INVISIBLE);
        this.userRepository = userRepository;
    }

    public void singup(RegisterRequest registerRequest, IRegisterResponse iRegisterResponse) {
        mProgressMutableData.postValue(View.VISIBLE);
        userRepository.signUp(registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mProgressMutableData.postValue(View.INVISIBLE);
                        iRegisterResponse.onResponse();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressMutableData.postValue(View.INVISIBLE);
                        iRegisterResponse.onFailure(e);
                    }
                });
    }

//    public Single<Token> login(LoginRequest loginRequest) {
//        return userRepository.singin(loginRequest);
//    }

    public void login(String email, String password){
        mProgressMutableData.postValue(View.VISIBLE);
        userRepository.singin(new LoginRequest(password, email), new UserRepositoryImpl.ILoginResponse() {
            @Override
            public void onResponse(Token token) {
                mProgressMutableData.postValue(View.INVISIBLE);
                mLoginResultMutableData.postValue(token.getToken());
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressMutableData.postValue(View.INVISIBLE);
                mLoginResultMutableData.postValue("Login failure: " + t.getLocalizedMessage());
            }
        });
    }

    public LiveData<String> getLoginResult(){
        return mLoginResultMutableData;
    }

    public LiveData<Integer> getProgress(){
        return mProgressMutableData;
    }

    public interface IRegisterResponse{
        void onResponse();
        void onFailure(Throwable t);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }
}
