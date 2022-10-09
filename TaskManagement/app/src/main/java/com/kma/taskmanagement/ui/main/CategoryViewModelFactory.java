package com.kma.taskmanagement.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.data.repository.CategoryRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {
    private final CategoryRepository categoryRepository;

    public CategoryViewModelFactory(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(categoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
