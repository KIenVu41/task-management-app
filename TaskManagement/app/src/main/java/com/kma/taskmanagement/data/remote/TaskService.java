package com.kma.taskmanagement.data.remote;

import com.kma.taskmanagement.data.model.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskService {

    @POST("api/v1/tasks")
    Completable addTask(@Header("Authorization") String authHeader, @Body Task task);

    @GET("api/v1/tasks/mytask")
    Observable<List<Task>> getAllTasks(@Header("Authorization") String authHeader);

    @GET("api/v1/tasks/category/{id}")
    Observable<List<Task>> getTasksByCategory(@Header("Authorization") String authHeader, @Path("id") long cateId);

    @PUT("api/v1/tasks/{id}")
    Completable updateTask(@Header("Authorization") String authHeader, @Path("id") long taskId,  @Body Task task);

    @DELETE("api/v1/tasks/{id}")
    Completable deleteTask(@Header("Authorization") String authHeader, @Path("id") long taskId);
}
