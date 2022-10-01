package com.kma.taskmanagement.ui.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.data.repository.UserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;

    public UserViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
