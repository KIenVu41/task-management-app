package com.kma.taskmanagement.data.remote.request;

public class GroupRequest {
    private long id;
    private String member_name;
    private String name;

    public GroupRequest() {
    }

    public GroupRequest(long id, String member_name, String name) {
        this.id = id;
        this.member_name = member_name;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
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
        return "GroupRequest{" +
                "id=" + id +
                ", member_name='" + member_name + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
