package com.kma.taskmanagement.data.model;

public class RegisterResponse {
    public int id;
    public String phone;
    public String username;
    public String email;
    public String url_image;

    public RegisterResponse(int id, String phone, String username, String email, String url_image) {
        this.id = id;
        this.phone = phone;
        this.username = username;
        this.email = email;
        this.url_image = url_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", url_image='" + url_image + '\'' +
                '}';
    }
}
