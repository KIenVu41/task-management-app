package com.kma.taskmanagement.data.repository.impl;

import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.remote.CategoryService;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.repository.CategoryRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class CategoryRepositoryImpl implements CategoryRepository {
    private CategoryService categoryService;

    public CategoryRepositoryImpl() {
        this.categoryService = RetrofitInstance.getRetrofitInstance().create(CategoryService.class);
    }
    @Override
    public Completable addCategory(String token, Category category) {
        return categoryService.addCategory(token, category);
    }

    @Override
    public Completable updateCategory(String token, long id, Category category) {
        return categoryService.updateCategory(token, id, category);
    }

    @Override
    public Observable<List<Category>> getAllCategories(String token) {
        return categoryService.getAllCategories(token);
    }
}
