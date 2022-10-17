package com.kma.taskmanagement.data.repository.impl;

import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.remote.TaskService;
import com.kma.taskmanagement.data.remote.UserService;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.listener.HandleResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class TaskRepositoryImpl implements TaskRepository {

    private TaskService taskService;

    public TaskRepositoryImpl() {
        this.taskService = RetrofitInstance.getRetrofitInstance().create(TaskService.class);
    }
    @Override
    public Completable addTask(String token, Task task) {
        return taskService.addTask(token, task);
    }

    @Override
    public Observable<List<Task>> getAllTasks(String token) {
        return taskService.getAllTasks(token);
    }

    @Override
    public Observable<List<Task>> getTasksByCategory(String token, long id) {
        return taskService.getTasksByCategory(token, id);
    }

    @Override
    public Completable updateTask(String token, long id, Task task) {
        return taskService.updateTask(token, id, task);
    }

    @Override
    public Completable deleteTask(String token, long id) {
        return taskService.deleteTask(token, id);
    }
}
