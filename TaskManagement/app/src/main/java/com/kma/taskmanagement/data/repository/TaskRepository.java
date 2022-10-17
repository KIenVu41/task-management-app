package com.kma.taskmanagement.data.repository;

import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.listener.HandleResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TaskRepository {
    Completable addTask(String token, Task task);

    Observable<List<Task>> getAllTasks(String token);

    Observable<List<Task>> getTasksByCategory(String token, long id);

    Completable updateTask(String token, long id, Task task);

    Completable deleteTask(String token, long id);
}
