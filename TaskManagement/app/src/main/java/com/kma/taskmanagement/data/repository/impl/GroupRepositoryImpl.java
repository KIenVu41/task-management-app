package com.kma.taskmanagement.data.repository.impl;

import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.remote.GroupService;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.remote.TaskService;
import com.kma.taskmanagement.data.remote.request.GroupRequest;
import com.kma.taskmanagement.data.remote.request.InviteRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class GroupRepositoryImpl implements GroupRepository {

    private GroupService groupService;

    public GroupRepositoryImpl() {
        this.groupService = RetrofitInstance.getRetrofitInstance().create(GroupService.class);
    }

    @Override
    public Completable addGroup(String token, GroupRequest groupRequest) {
        return groupService.addGroup(token, groupRequest);
    }

    @Override
    public Completable join(String token, InviteRequest ir) {
        return groupService.join(token, ir);
    }

    @Override
    public Observable<List<InviteRequest>> getInvites(String authHeader) {
        return groupService.getInvites(authHeader);
    }

    @Override
    public Observable<List<Group>> getGroups(String authHeader) {
        return groupService.getGroups(authHeader);
    }

    @Override
    public Observable<Group> getGroupDetail(String authHeader, long id) {
        return groupService.getGroupDetail(authHeader, id);
    }

    @Override
    public Completable delete(String authHeader, long groupId) {
        return groupService.delete(authHeader, groupId);
    }
}
