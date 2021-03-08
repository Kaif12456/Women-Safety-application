package com.a.womensafety;

public class UserDetails
{
    String name;
    String img;
    String email;
    String pswd;

    public UserDetails(String name, String img, String email, String pswd) {
        this.name = name;
        this.img = img;
        this.email = email;
        this.pswd = pswd;
    }

    public UserDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }
}
