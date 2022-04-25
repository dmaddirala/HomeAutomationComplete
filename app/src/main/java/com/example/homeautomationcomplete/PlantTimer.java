package com.example.homeautomationcomplete;

public class PlantTimer {

    private String time;
    private String date;
    private String srNumber;

    public PlantTimer(String time, String date, String srNumber) {
        this.time = time;
        this.date = date;
        this.srNumber = srNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSrNumber() {
        return srNumber;
    }

    public void setSrNumber(String srNumber) {
        this.srNumber = srNumber;
    }
}
