package com.kma.taskmanagement.data.repository;

import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface CategoryRepository {
    Completable addCategory(String token, Category category);

    Completable updateCategory(String token, long id, Category category);

    Observable<List<Category>> getAllCategories(String token);

    Completable deleteCategory(String authHeader, long cateId);
}
