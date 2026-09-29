package com.example.campusfind.models;

public class User {
    private int userId;
    private String name;
    private String studentId;
    private String contact;

    public User(int userId, String name, String studentId, String contact) {
        this.userId = userId;
        this.name = name;
        this.studentId = studentId;
        this.contact = contact;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getContact() { return contact; }
}
