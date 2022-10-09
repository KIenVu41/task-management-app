package com.kma.taskmanagement.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.data.repository.TaskRepository;


public class TaskViewModelFactory implements ViewModelProvider.Factory{
    private final TaskRepository taskRepository;

    public TaskViewModelFactory(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(taskRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
