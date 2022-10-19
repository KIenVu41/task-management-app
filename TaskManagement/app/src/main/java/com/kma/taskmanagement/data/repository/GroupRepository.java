package com.kma.taskmanagement.data.repository;

import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.remote.request.GroupRequest;
import com.kma.taskmanagement.data.remote.request.InviteRequest;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GroupRepository {

    Completable addGroup(String token, GroupRequest groupRequest);

    Completable join(String token, InviteRequest ir);

    Observable<List<InviteRequest>> getInvites(String authHeader);

    Observable<List<Group>> getGroups(String authHeader);

    Completable delete(String authHeader, long groupId);
}