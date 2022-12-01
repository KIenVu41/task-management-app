package com.kma.taskmanagement.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.remote.request.GroupRequest;
import com.kma.taskmanagement.data.remote.request.InviteRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class GroupViewModel extends ViewModel {

    private GroupRepository groupRepository;

    private MutableLiveData<String> mResponseMutableData = new MutableLiveData<>();
    private MutableLiveData<List<Group>> mResultMutableData = new MutableLiveData<>();
    private MutableLiveData<Group> mSingleResultMutableData = new MutableLiveData<>();
    private MutableLiveData<List<InviteRequest>> mInviteMutableData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public GroupViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void addGroup(String token, GroupRequest groupRequest) {
        mResponseMutableData.postValue("Đang xử lý...");
        groupRepository.addGroup(token, groupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mResponseMutableData.postValue("Hoàn thành");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResponseMutableData.postValue("Lỗi " + e.getMessage());
                    }
                });
    }

    public void updateGroup(String token, long id, GroupRequest groupRequest) {
        mResponseMutableData.postValue("Đang xử lý...");
        groupRepository.updateGroup(token, id, groupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mResponseMutableData.postValue("Hoàn thành");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResponseMutableData.postValue("Lỗi " + e.getMessage());
                    }
                });
    }


    public void getInvites(String token){
        try{
            compositeDisposable.add(
                    Observable.interval(20, TimeUnit.SECONDS)
                            .flatMap(aLong -> groupRepository.getInvites(token))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(inviteRequests ->   mInviteMutableData.setValue(inviteRequests), e->    mResponseMutableData.postValue("Lỗi " + e.getMessage())));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getGroups(String token) {
        mResponseMutableData.postValue("Đang xử lý...");
        compositeDisposable.add(groupRepository.getGroups(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> mResultMutableData.setValue(list), t ->  mResponseMutableData.postValue("Lỗi " + t.getMessage())));
    }

    public void getGroupDetail(String token, long id) {
        mResponseMutableData.postValue("Đang xử lý...");
        compositeDisposable.add(groupRepository.getGroupDetail(token, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gr -> mSingleResultMutableData.setValue(gr), t ->  mResponseMutableData.postValue("Lỗi " + t.getMessage())));
    }

    public void join(String token, InviteRequest ir) {
        mResponseMutableData.postValue("Đang xử lý...");
        compositeDisposable.add(
                groupRepository.join(token, ir)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> mResponseMutableData.postValue("Hoàn thành"), t -> mResponseMutableData.postValue("Lỗi " + t.getMessage())));
    }

    public void delete(String token, long id) {
        mResponseMutableData.postValue("Đang xử lý...");
        compositeDisposable.add(
                groupRepository.delete(token, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> mResponseMutableData.postValue("Hoàn thành"), t -> mResponseMutableData.postValue("Lỗi " + t.getMessage())));
    }

    public LiveData<String> getResponse(){
        return mResponseMutableData;
    }

    public LiveData<List<InviteRequest>> getInviteResponse(){
        return mInviteMutableData;
    }

    public LiveData<List<Group>> getGroupResponse(){
        return mResultMutableData;
    }

    public LiveData<Group> getGroupSingleResponse(){
        return mSingleResultMutableData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
