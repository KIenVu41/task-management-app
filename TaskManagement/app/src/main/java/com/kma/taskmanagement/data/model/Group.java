package com.kma.taskmanagement.data.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

public class Group {
    private long id;
    private List<User> member;
    private String name;
    private String code;
    private String leader_name;

    public Group() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<User> getMember() {
        return member;
    }

    public void setMember(List<User> member) {
        this.member = member;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLeader_name() {
        return leader_name;
    }

    public void setLeader_name(String leader_name) {
        this.leader_name = leader_name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", member_name=" + member.toString() +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", leader_nam='" + leader_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.getId() &&
                name.equals(group.getName()) &&
                leader_name.equals(group.getLeader_name()) &&
                member.equals(group.getMember());

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, leader_name, member);
    }

    public static DiffUtil.ItemCallback<Group> itemCallback = new DiffUtil.ItemCallback<Group>() {
        @Override
        public boolean areItemsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.equals(newItem);
        }
    };
}
