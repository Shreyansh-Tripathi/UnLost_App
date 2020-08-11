package com.example.unlost.activities;

public class Reply {
    public String userName, userAnswer;

    public Reply(String userName, String userAnswer) {
        this.userName = userName;
        this.userAnswer = userAnswer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
