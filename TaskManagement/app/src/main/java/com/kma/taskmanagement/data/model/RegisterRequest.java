package com.kma.taskmanagement.data.model;

public class RegisterRequest {
    public String email;
    public int id;
    public String password;
    public String phone;
    public String url_image;
    public String username;

    public RegisterRequest(String email, int id, String password, String phone, String url_image, String username) {
        this.email = email;
        this.id = id;
        this.password = password;
        this.phone = phone;
        this.url_image = url_image;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPass() {
        return password;
    }

    public void setPass(String pass) {
        this.password = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", id=" + id +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", url_image='" + url_image + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
