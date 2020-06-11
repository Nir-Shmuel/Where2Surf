package com.example.where2surf.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String surfingType;
    private String homeSpot;

    public User(String firstName, String lastName, String email, String surfingType, String homeSpot) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.surfingType = surfingType;
        this.homeSpot = homeSpot;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurfingType() {
        return surfingType;
    }

    public void setSurfingType(String surfingType) {
        this.surfingType = surfingType;
    }

    public String getHomeSpot() {
        return homeSpot;
    }

    public void setHomeSpot(String homeSpot) {
        this.homeSpot = homeSpot;
    }


}
