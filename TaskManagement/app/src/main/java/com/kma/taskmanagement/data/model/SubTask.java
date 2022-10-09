package com.kma.taskmanagement.data.model;

public class SubTask {
    private int assigner_id;
    private String code;
    private String description;
    private String end_date;
    private String name;
    private int performer_id;
    private String priority;
    private String start_date;
    private String status;

    public SubTask(int assigner_id, String code, String description, String end_date, String name, int performer_id, String priority, String start_date, String status) {
        this.assigner_id = assigner_id;
        this.code = code;
        this.description = description;
        this.end_date = end_date;
        this.name = name;
        this.performer_id = performer_id;
        this.priority = priority;
        this.start_date = start_date;
        this.status = status;
    }

    public int getAssigner_id() {
        return assigner_id;
    }

    public void setAssigner_id(int assigner_id) {
        this.assigner_id = assigner_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPerformer_id() {
        return performer_id;
    }

    public void setPerformer_id(int performer_id) {
        this.performer_id = performer_id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
