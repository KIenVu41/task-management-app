package com.kma.taskmanagement.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;

public class GroupViewModelFactory implements ViewModelProvider.Factory{
    private final GroupRepository groupRepository;

    public GroupViewModelFactory(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GroupViewModel.class)) {
            return (T) new GroupViewModel(groupRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

