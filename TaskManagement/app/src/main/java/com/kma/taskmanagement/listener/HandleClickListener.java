package com.kma.taskmanagement.listener;

import android.view.View;

import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;

public interface HandleClickListener {
    void onLongClick(View view);
    void onTaskClick(Task task, String status);
    void onGroupClick(Group group);
}
