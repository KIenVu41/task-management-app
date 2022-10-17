package com.kma.taskmanagement.data.remote;

import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.remote.request.GroupRequest;
import com.kma.taskmanagement.data.remote.request.InviteRequest;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GroupService {

    @POST("api/v1/groups")
    Completable addGroup(@Header("Authorization") String authHeader, @Body GroupRequest groupRequest);

    @GET("api/v1/groups/invite")
    Observable<List<InviteRequest>> getInvites(@Header("Authorization") String authHeader);

    @GET("api/v1/groups")
    Observable<List<Group>> getGroups(@Header("Authorization") String authHeader);


    @PUT("api/v1/groups/join")
    Completable join(@Header("Authorization") String authHeader, @Body InviteRequest inviteRequest);

    @DELETE("api/v1/groups/{id}")
    Completable delete(@Header("Authorization") String authHeader, @Path("id") long groupId);
}
