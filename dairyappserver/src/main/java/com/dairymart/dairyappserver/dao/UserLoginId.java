package com.dairymart.dairyappserver.dao;

import java.sql.Timestamp;

public class UserLoginId {
    private int userId;
    private Timestamp loggedIn;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Timestamp loggedIn) {
        this.loggedIn = loggedIn;
    }
}
