package com.kma.taskmanagement.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.TaskRepository;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TaskViewModel extends ViewModel {
    private TaskRepository taskRepository;
    MutableLiveData<String> mResponseMutableData = new MutableLiveData<>();
    MutableLiveData<List<Task>> mResultMutableData = new MutableLiveData<>();

    public TaskViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(String token, Task task) {
        mResponseMutableData.postValue("Đang xử lý...");
        taskRepository.addTask(token, task)
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

    public void getAllTasks(String token) {
        mResponseMutableData.postValue("Đang xử lý...");
        taskRepository.getAllTasks(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Task> tasks) {
                        mResultMutableData.setValue(tasks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResponseMutableData.postValue("Lỗi " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mResponseMutableData.postValue("Hoàn thành");
                    }
                });
    }

    public void getTasksByCategory(String token, long id) {
        mResponseMutableData.postValue("Đang xử lý...");
        taskRepository.getTasksByCategory(token, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Task> tasks) {
                        mResultMutableData.setValue(tasks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResponseMutableData.postValue("Lỗi " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mResponseMutableData.postValue("Hoàn thành");
                    }
                });
    }

    public void deleteTask(String token, long id) {
        mResponseMutableData.postValue("Đang xử lý...");
        taskRepository.deleteTask(token, id)
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

    public LiveData<String> getResponse(){
        return mResponseMutableData;
    }

    public LiveData<List<Task>> getResult() {
        return mResultMutableData;
    }
}
