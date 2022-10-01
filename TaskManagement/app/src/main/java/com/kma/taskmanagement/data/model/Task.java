package com.kma.taskmanagement.data.model;

public class Task {
    int taskId;
    String taskTitle;
    String date;
    String taskDescrption;
    boolean isComplete;
    String firstAlarmTime;
    String secondAlarmTime;
    String lastAlarm;
    String event;

    public Task() {
    }

    public Task(int taskId, String taskTitle, String date, String taskDescrption, boolean isComplete, String firstAlarmTime, String secondAlarmTime, String lastAlarm, String event) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.date = date;
        this.taskDescrption = taskDescrption;
        this.isComplete = isComplete;
        this.firstAlarmTime = firstAlarmTime;
        this.secondAlarmTime = secondAlarmTime;
        this.lastAlarm = lastAlarm;
        this.event = event;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaskDescrption() {
        return taskDescrption;
    }

    public void setTaskDescrption(String taskDescrption) {
        this.taskDescrption = taskDescrption;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getFirstAlarmTime() {
        return firstAlarmTime;
    }

    public void setFirstAlarmTime(String firstAlarmTime) {
        this.firstAlarmTime = firstAlarmTime;
    }

    public String getSecondAlarmTime() {
        return secondAlarmTime;
    }

    public void setSecondAlarmTime(String secondAlarmTime) {
        this.secondAlarmTime = secondAlarmTime;
    }

    public String getLastAlarm() {
        return lastAlarm;
    }

    public void setLastAlarm(String lastAlarm) {
        this.lastAlarm = lastAlarm;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
