package com.kma.taskmanagement.data.repository;

import com.kma.taskmanagement.data.model.Chart;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.listener.HandleResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TaskRepository {
    Completable addTask(String token, Task task);

    Observable<List<Task>> getAllTasks(String token);

    Observable<List<Task>> getTasksByCategory(String token, long id);

    Observable<List<Task>> filterPersonalTaskByPrio(String token, long id, String priorityType);

    Observable<List<Task>> filterPersonalTaskByStatus(String token, long id, String statusType);

    Observable<List<Task>> filterPersonalTaskByPrioAndStatus(String token, long id, String priorityType, String statusType);

    Observable<List<Task>> getAllTasksByPrio(String token, String priorityType);

    Observable<List<Task>> getAllTasksByStatus(String token, String statusType);

    Observable<List<Task>> getAllTasksByStatusAndPrio(String authHeader, String priorityType, String statusType);

    Observable<List<Task>> getAssign(String authHeader);

    Observable<List<Task>> getAssignByGroupId(String authHeader, int groupId);

    Observable<List<Task>> getAssignByPrio(String authHeader, String priorityType);

    Observable<List<Task>> getAssignByStatus(String authHeader, String statusType);

    Observable<List<Task>> getAssignByPrioAndStatus(String authHeader, String priorityType, String statusType);

    Observable<Chart> getGroupCount(String authHeader);

    Observable<Chart> getPersonalCount(String authHeader);

    Completable updateTask(String token, long id, Task task);

    Completable deleteTask(String token, long id);
}
