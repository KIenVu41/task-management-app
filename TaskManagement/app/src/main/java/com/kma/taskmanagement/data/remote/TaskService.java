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
import retrofit2.http.Query;

public interface TaskService {

    @POST("api/v1/tasks")
    Completable addTask(@Header("Authorization") String authHeader, @Body Task task);

    @GET("api/v1/tasks/mytask")
    Observable<List<Task>> getAllTasks(@Header("Authorization") String authHeader);

    @GET("api/v1/tasks/mytask")
    Observable<List<Task>> getAllTasksByPrio(@Header("Authorization") String authHeader, @Query("priorityType") String priorityType);

    @GET("api/v1/tasks/mytask")
    Observable<List<Task>> getAllTasksByStatus(@Header("Authorization") String authHeader, @Query("statusType") String statusType);

    @GET("api/v1/tasks/mytask")
    Observable<List<Task>> getAllTasksByStatusAndPrio(@Header("Authorization") String authHeader, @Query("priorityType") String priorityType, @Query("statusType") String statusType);

    @GET("api/v1/tasks/category/{id}")
    Observable<List<Task>> getTasksByCategory(@Header("Authorization") String authHeader, @Path("id") long cateId);

    @GET("api/v1/tasks/category/{id}")
    Observable<List<Task>> filterPersonalTaskByPrio(@Header("Authorization") String authHeader, @Path("id") long cateId, @Query("priorityType") String priorityType);

    @GET("api/v1/tasks/category/{id}")
    Observable<List<Task>> filterPersonalTaskByStatus(@Header("Authorization") String authHeader, @Path("id") long cateId, @Query("statusType") String statusType);

    @GET("api/v1/tasks/category/{id}")
    Observable<List<Task>> filterPersonalTaskByPrioAndStatus(@Header("Authorization") String authHeader, @Path("id") long cateId, @Query("priorityType") String priorityType, @Query("statusType") String statusType);

    @GET("api/v1/tasks/assigntask")
    Observable<List<Task>> getAssign(@Header("Authorization") String authHeader);

    @GET("api/v1/tasks/assigntask")
    Observable<List<Task>> getAssignByPrio(@Header("Authorization") String authHeader, @Query("priorityType") String priorityType);

    @GET("api/v1/tasks/assigntask")
    Observable<List<Task>> getAssignByStatus(@Header("Authorization") String authHeader, @Query("statusType") String statusType);

    @GET("api/v1/tasks/assigntask")
    Observable<List<Task>> getAssignByPrioAndStatus(@Header("Authorization") String authHeader,  @Query("priorityType") String priorityType, @Query("statusType") String statusType);

    @PUT("api/v1/tasks/{id}")
    Completable updateTask(@Header("Authorization") String authHeader, @Path("id") long taskId,  @Body Task task);

    @DELETE("api/v1/tasks/{id}")
    Completable deleteTask(@Header("Authorization") String authHeader, @Path("id") long taskId);
}
