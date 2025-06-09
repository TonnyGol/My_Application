package com.example.myapplication;

public class User {
    private String email, password, phone, address, description;

    public User(String email, String password, String phone, String address){
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.description = "";
    }

    public User(){
        this.email = "";
        this.password = "";
        this.phone = "";
        this.address = "";
        this.description = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
