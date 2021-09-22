package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

public class Subscribers {

    private String name;
    private String phone;
    private String image;
    private String email;

    public Subscribers(String name, String phone, String image, String email) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.email = email;
    }



    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    public String getEmail() {
        return email;
    }
}