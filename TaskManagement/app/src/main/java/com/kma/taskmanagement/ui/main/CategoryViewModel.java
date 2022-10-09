package com.kma.taskmanagement.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.CategoryRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryViewModel extends ViewModel {
    private CategoryRepository categoryRepository;
    MutableLiveData<String> mResponseMutableData = new MutableLiveData<>();
    MutableLiveData<List<Category>> mResultMutableData = new MutableLiveData<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(String token, Category category) {
        mResponseMutableData.postValue("Đang xử lý...");
        categoryRepository.addCategory(token, category)
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

    public void updateCategory(String token, long id, Category category) {
        mResponseMutableData.postValue("Đang xử lý...");
        categoryRepository.updateCategory(token, id, category)
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

    public void getAllCategories(String token) {
        mResponseMutableData.postValue("Đang xử lý...");
        Observable<List<Category>> observable = categoryRepository.getAllCategories(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(observable.subscribe(o -> mResultMutableData.setValue(o)));
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(List<Category> categories) {
//                        mResultMutableData.postValue(categories);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mResponseMutableData.postValue("Lỗi " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        mResponseMutableData.postValue("Hoàn thành");
//                    }
//                });
    }

    public LiveData<String> getResponse(){
        return mResponseMutableData;
    }

    public LiveData<List<Category>> getResult() {
        return mResultMutableData;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
