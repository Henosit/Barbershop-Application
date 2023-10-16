package com.example.barbershopapp.utils;

public class ReadWriteUserDetails {

    private String ID;
    private String birthday;
    private String gender;
    private String mobile;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;

    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String ID, String birthday, String gender, String mobile, String role) {
        this.ID = ID;
        this.birthday = birthday;
        this.gender = gender;
        this.mobile = mobile;
        this.role = role;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
