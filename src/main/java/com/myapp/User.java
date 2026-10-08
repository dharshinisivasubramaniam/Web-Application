package com.myapp;

public class User {
    public int id;
    public String name;
    public String phone;
    public String email;
    public String passwordHash;
    public String passwordSalt;

    public User(String name, String phone, String email, String passwordHash, String passwordSalt) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }
}