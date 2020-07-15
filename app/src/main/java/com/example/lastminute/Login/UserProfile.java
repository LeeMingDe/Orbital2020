package com.example.lastminute.Login;
/**
 * A class that acts like a data structure to store user information
 */
public class UserProfile {
    private String userEmail;
    private String userName;
    private String password;

    public UserProfile(String userEmail, String userName, String password) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userName = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
