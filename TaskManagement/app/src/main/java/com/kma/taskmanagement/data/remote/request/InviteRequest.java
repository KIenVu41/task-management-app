package com.kma.taskmanagement.data.remote.request;

public class InviteRequest {
    private long group_id;
    private long id;
    private long invitation_id;
    private String inviter_username;
    private boolean is_accept;

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInviter_username() {
        return inviter_username;
    }

    public void setInviter_username(String inviter_username) {
        this.inviter_username = inviter_username;
    }

    public long getInvitation_id() {
        return invitation_id;
    }

    public void setInvitation_id(long invitation_id) {
        this.invitation_id = invitation_id;
    }

    public boolean isIs_accept() {
        return is_accept;
    }

    public void setIs_accept(boolean is_accept) {
        this.is_accept = is_accept;
    }

    @Override
    public String toString() {
        return "InviteRequest{" +
                "group_id=" + group_id +
                ", id=" + id +
                ", inviter_username='" + inviter_username + '\'' +
                ", is_accept=" + is_accept +
                '}';
    }
}
