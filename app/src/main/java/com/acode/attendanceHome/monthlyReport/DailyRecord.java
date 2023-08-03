package com.acode.attendanceHome.monthlyReport;

public class DailyRecord {

    private final String cName;
    private final String status;
    private final int sRN;
    private final int day;
    private final int month;

    public String getStatus() {
        return status;
    }

    public DailyRecord(String cName, int sRN, int day, int month, String status) {
        this.cName = cName;
        this.sRN = sRN;
        this.day = day;
        this.month = month;
        this.status = status;
    }

    public String getcName() {
        return cName;
    }

    public int getsRN() {
        return sRN;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }
}