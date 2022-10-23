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
    public Observable<List<Task>> filterPersonalTaskByPrio(String token, long id, String priorityType) {
        return taskService.filterPersonalTaskByPrio(token, id, priorityType);
    }

    @Override
    public Observable<List<Task>> filterPersonalTaskByStatus(String token, long id, String statusType) {
        return taskService.filterPersonalTaskByStatus(token, id, statusType);
    }

    @Override
    public Observable<List<Task>> filterPersonalTaskByPrioAndStatus(String token, long id, String priorityType, String statusType) {
        return taskService.filterPersonalTaskByPrioAndStatus(token, id, priorityType, statusType);
    }

    @Override
    public Observable<List<Task>> getAllTasksByPrio(String token, String priorityType) {
        return taskService.getAllTasksByPrio(token, priorityType);
    }

    @Override
    public Observable<List<Task>> getAllTasksByStatus(String token, String statusType) {
        return taskService.getAllTasksByStatus(token, statusType);
    }

    @Override
    public Observable<List<Task>> getAllTasksByStatusAndPrio(String authHeader, String priorityType, String statusType) {
        return taskService.getAllTasksByStatusAndPrio(authHeader, priorityType, statusType);
    }

    @Override
    public Observable<List<Task>> getAssign(String authHeader) {
        return taskService.getAssign(authHeader);
    }

    @Override
    public Observable<List<Task>> getAssignByPrio(String authHeader, String priorityType) {
        return taskService.getAssignByPrio(authHeader, priorityType);
    }

    @Override
    public Observable<List<Task>> getAssignByStatus(String authHeader, String statusType) {
        return taskService.getAssignByStatus(authHeader, statusType);
    }

    @Override
    public Observable<List<Task>> getAssignByPrioAndStatus(String authHeader, String priorityType, String statusType) {
        return taskService.getAssignByPrioAndStatus(authHeader, priorityType, statusType);
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
