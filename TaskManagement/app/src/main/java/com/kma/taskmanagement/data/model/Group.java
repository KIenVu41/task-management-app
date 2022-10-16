package com.kma.taskmanagement.data.model;

import java.util.List;

public class Group {
    private long id;
    private List<String> member_name;
    private String name;

    public Group() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getMember_name() {
        return member_name;
    }

    public void setMember_name(List<String> member_name) {
        this.member_name = member_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", member_name='" + member_name.toString() + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
