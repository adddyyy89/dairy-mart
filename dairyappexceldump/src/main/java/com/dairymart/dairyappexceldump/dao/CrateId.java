package com.dairymart.dairyappexceldump.dao;

import java.sql.Timestamp;

public class CrateId {
    private int userId;
    private Timestamp recordTimestamp;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getRecordTimestamp() {
        return recordTimestamp;
    }

    public void setRecordTimestamp(Timestamp recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }
}
