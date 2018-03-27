package com.example.popoutanu.invaders.activities;

/**
 * Created by Popoutanu on 3/14/2018.
 */

public class User {
    //fields
    private int userId;
    private String userName;
    private int userScore;

    //constructors
    public User() {
    }

    public User(int userId, String userName, int userScore) {
        this.userId = userId;
        this.userName = userName;
        this.userScore = userScore;
    }
    //properties

    public void setUserId(int id) {
        this.userId = id;
    }

    public int getId() {
        return this.userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public int getUserScore() {
        return this.userScore;
    }

    @Override
    public String toString() {
        return userName + " Scored " + userScore;
    }
}

