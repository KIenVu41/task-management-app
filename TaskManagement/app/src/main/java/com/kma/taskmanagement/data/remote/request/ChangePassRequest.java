package com.kma.taskmanagement.data.remote.request;

public class ChangePassRequest {
    private String exist_password;
    private String new_password;

    public ChangePassRequest(String exist_password, String new_password) {
        this.exist_password = exist_password;
        this.new_password = new_password;
    }

    public String getExist_password() {
        return exist_password;
    }

    public void setExist_password(String exist_password) {
        this.exist_password = exist_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
