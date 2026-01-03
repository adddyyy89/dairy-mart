package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "Gst", schema = "public")
public class GSTDao {

    @Id
    @Column(name = "gstid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int gstId;

    @Column(name = "gstnumber")
    private String gstNumber;

    @Column(name = "aadharnumber")
    private String aadharNumber;

    @Column(name = "pannumber")
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

    public GSTDao(int gstId, String gstNumber, String aadharNumber, String panNumber) {
        this.gstId = gstId;
        this.gstNumber = gstNumber;
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
    }

    public GSTDao() {
    }
}
