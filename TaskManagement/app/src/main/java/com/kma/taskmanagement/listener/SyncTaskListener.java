package com.kma.taskmanagement.listener;

import com.kma.taskmanagement.data.model.Task;

public interface SyncTaskListener {
    void onHandleSync(Task task);
}
