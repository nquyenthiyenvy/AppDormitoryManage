package com.example.doan.model;

public class Student {
    private int id;
    private String name;
    private String birthday;
    private String mssv;
    private String gender;
    private String phone;
    private String address;
    private  String avatar;
    private int roomId;

    public Student(int roomId, String name, String phone, String gender, String mssv, String birthday, String address, int id) {
        this.roomId = roomId;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.mssv = mssv;
        this.birthday = birthday;
        this.address = address;
        this.id = id;
    }

    public Student(int id, String name, String birthday, String mssv, String gender, String phone, String address, String avatar, int roomId) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.mssv = mssv;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.roomId = roomId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

}
