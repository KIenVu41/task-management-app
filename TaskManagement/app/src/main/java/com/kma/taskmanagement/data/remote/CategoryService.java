package com.kma.taskmanagement.data.remote;

import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {

    @POST("api/v1/categories")
    Completable addCategory(@Header("Authorization") String authHeader, @Body Category category);

    @PUT("api/v1/categories/{id}")
    Completable updateCategory(@Header("Authorization") String authHeader, @Path("id") long cateId, @Body Category category);

    @GET("api/v1/categories")
    Observable<List<Category>> getAllCategories(@Header("Authorization") String authHeader);
}

