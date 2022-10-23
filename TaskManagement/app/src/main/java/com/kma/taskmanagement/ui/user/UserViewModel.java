package com.kma.taskmanagement.ui.user;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.RegisterResponse;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.request.ChangePassRequest;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.listener.HandleResponse;

import java.util.function.LongToDoubleFunction;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    MutableLiveData<String> mProgressMutableData = new MutableLiveData<>();
    MutableLiveData<Token> mLoginResultMutableData = new MutableLiveData<>();
    MutableLiveData<User> mUpdateResultMutableData = new MutableLiveData<>();
    MutableLiveData<String> mRegisterResultMutableData = new MutableLiveData<>();
    MutableLiveData<Integer> mCPResultMutableData = new MutableLiveData<>();
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void singup(RegisterRequest registerRequest) {
        mProgressMutableData.postValue("Đang xử lý...");
        userRepository.singUp(registerRequest, new HandleResponse() {
            @Override
            public void onResponse(Object o,  Object k) {
                RegisterResponse registerRequest1 = (RegisterResponse) o;
                Integer code = (Integer) k;
                if(registerRequest1 != null & code == 200) {
                    mProgressMutableData.postValue("Thành công");
                } else if (registerRequest1 == null){
                    mProgressMutableData.postValue("Có vấn đề trong quá trình xử lý");
                }
                mRegisterResultMutableData.postValue(registerRequest1.toString());
            }

            @Override
            public void onFailure(Object o) {
                Throwable t = (Throwable) o;
                mProgressMutableData.postValue("Lỗi: " + t.getMessage());
                mRegisterResultMutableData.postValue("Register failure: " + t.getLocalizedMessage());
            }
        });

    }

    public void login(String email, String password){
        mProgressMutableData.postValue("Đang xử lý...");
        userRepository.singin(new LoginRequest(password, email), new HandleResponse() {
            @Override
            public void onResponse(Object o, Object k) {
                Token token = (Token) o;
                Integer code = (Integer) k;
                if(token != null & code == 200) {
                    mProgressMutableData.postValue("Thành công");
                } else if (token == null && code == 500){
                    mProgressMutableData.postValue("Có vấn đề trong quá trình xử lý");
                }
                mLoginResultMutableData.postValue(token);
            }

            @Override
            public void onFailure(Object o) {
                Throwable t = (Throwable) o;
                mProgressMutableData.postValue("Lỗi: " + t.getMessage());
                mLoginResultMutableData.postValue(null);
            }
        });
    }

    public void update(String token, long id, User user) {
        mProgressMutableData.postValue("Đang xử lý...");
        userRepository.update(token, id, user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {
                        mUpdateResultMutableData.setValue(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressMutableData.postValue("Lỗi " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mProgressMutableData.postValue("Hoàn thành");
                    }
                });
//                .subscribe(new CompletableObserver() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        mProgressMutableData.postValue("Hoàn thành");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mProgressMutableData.postValue("Lỗi " + e.getMessage());
//                    }
//                });
//                .subscribe(new SingleObserver<Token>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Token token) {
//                        mProgressMutableData.postValue("Hoàn thành");
//                        mUpdateResultMutableData.setValue(token);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mProgressMutableData.postValue("Lỗi " + e.getMessage());
//                    }
//                });
    }

    public void changePass(String token, ChangePassRequest changePassRequest) {
        mCPResultMutableData.postValue(0);
        userRepository.changepass(token, changePassRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mCPResultMutableData.postValue(1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCPResultMutableData.postValue(-1);
                    }
                });
    }

    public LiveData<Token> getResult(){
        return mLoginResultMutableData;
    }

    public LiveData<User> getUpdateResult(){
        return mUpdateResultMutableData;
    }

    public LiveData<String> getRegisterResult(){
        return mRegisterResultMutableData;
    }

    public LiveData<String> getProgress(){
        return mProgressMutableData;
    }

    public LiveData<Integer> getChangePass(){
        return mCPResultMutableData;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }
}
