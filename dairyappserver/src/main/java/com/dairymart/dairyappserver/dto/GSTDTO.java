package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.GSTDao;
import jakarta.persistence.*;

public class GSTDTO {

    private int gstId;
    private String gstNumber;
    private String aadharNumber;
    private String panNumber;

    public int getGstId() {
        return gstId;
    }

    public void setGstId(int gstId) {
        this.gstId = gstId;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public GSTDTO(int gstId, String gstNumber, String aadharNumber, String panNumber) {
        this.gstId = gstId;
        this.gstNumber = gstNumber;
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
    }

    public GSTDTO(GSTDao dao) {
        this.gstId = dao.getGstId();
        this.aadharNumber = dao.getAadharNumber();
        this.gstNumber = dao.getGstNumber();
        this.panNumber = dao.getPanNumber();
    }

}
