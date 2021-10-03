package com.nova.rawad.Users;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class UserClass implements Serializable {
    @PropertyName("fullName")
    public String fullName="";
    @PropertyName("phone")
    public String phone="";
    @PropertyName("password")
    public String password="";
    @PropertyName("id")
    public String id="";
}
