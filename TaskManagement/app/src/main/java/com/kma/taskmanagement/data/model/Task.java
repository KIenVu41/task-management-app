package com.kma.taskmanagement.data.model;

import java.util.List;

public class Task {
    private long id;
    private String assigner_name;
    private long category_id;
    private String code;
    private String description;
    private String end_date;
    private Long group_id;
    private String name;
    private String performer_name;
    private String priority;
    private String start_date;
    private String status;
    private List<SubTask> sub_tasks = null;
    private List<Group> group_output_dto;

    public Task() {
    }

    public Task(String assigner_name, long category_id, String code, String description, String end_date, Long group_id, String name, String performer_name, String priority, String start_date, String status, List<SubTask> sub_tasks) {
        this.assigner_name = assigner_name;
        this.category_id = category_id;
        this.code = code;
        this.description = description;
        this.end_date = end_date;
        this.group_id = group_id;
        this.name = name;
        this.performer_name = performer_name;
        this.priority = priority;
        this.start_date = start_date;
        this.status = status;
        this.sub_tasks = sub_tasks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssigner_name() {
        return assigner_name;
    }

    public void setAssigner_name(String assigner_name) {
        this.assigner_name = assigner_name;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
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

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerformer_name() {
        return performer_name;
    }

    public void setPerformer_name(String performer_name) {
        this.performer_name = performer_name;
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

    public List<SubTask> getSub_tasks() {
        return sub_tasks;
    }

    public void setSub_tasks(List<SubTask> sub_tasks) {
        this.sub_tasks = sub_tasks;
    }

    public List<Group> getGroup_output_dto() {
        return group_output_dto;
    }

    public void setGroup_output_dto(List<Group> group_output_dto) {
        this.group_output_dto = group_output_dto;
    }
}
