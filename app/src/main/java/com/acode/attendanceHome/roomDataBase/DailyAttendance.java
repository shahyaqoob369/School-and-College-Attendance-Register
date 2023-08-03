package com.acode.attendanceHome.roomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//in this table attendance save with name, class, roll No and date:
@Entity(tableName = "daily_attendance")
public class DailyAttendance {

    @PrimaryKey(autoGenerate = true)
    int sNo;

    @ColumnInfo(name = "studentRollNo")
    int studentRollNo;

    @ColumnInfo(name = "studentName")
    String studentName;


    @ColumnInfo(name = "studentClass")
    String studentClass;


    @ColumnInfo(name = "attendanceDate")
    String attendanceDate;

    @ColumnInfo(name = "studentStatus")
    String studentStatus;

    public int getStudentRollNo() {
        return studentRollNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public void setStudentRollNo(int studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public DailyAttendance(int sNo, int studentRollNo, String studentName, String studentClass, String attendanceDate, String studentStatus) {
        this.sNo = sNo;
        this.studentRollNo = studentRollNo;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.attendanceDate = attendanceDate;
        this.studentStatus = studentStatus;
    }

    @Ignore
    public DailyAttendance(int studentRollNo, String studentName, String studentClass, String attendanceDate, String studentStatus) {
        this.studentRollNo = studentRollNo;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.attendanceDate = attendanceDate;
        this.studentStatus = studentStatus;
    }
}
