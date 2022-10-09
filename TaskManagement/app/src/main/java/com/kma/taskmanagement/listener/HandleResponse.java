package com.kma.taskmanagement.listener;

import com.kma.taskmanagement.data.model.Token;

public interface HandleResponse<T, K> {
    void onResponse(T t, K k);
    void onFailure(T t);
}
