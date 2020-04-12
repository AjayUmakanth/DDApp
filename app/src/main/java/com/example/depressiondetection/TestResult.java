package com.example.depressiondetection;

public class TestResult {
    String id,date,time,qres,mres;
    public TestResult(String id, String datetime, String qres, String mres){
        this.id=id;
        this.date=datetime.substring(0,10);
        this.time=datetime.substring(11);
        this.qres=qres;
        this.mres=mres;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getMres() {
        return mres;
    }

    public String getQres() {
        return qres;
    }
}
