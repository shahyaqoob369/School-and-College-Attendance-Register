package com.acode.attendanceHome.roomDataBase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//in this table attendance save with name, class, roll No and status:
@Entity(tableName = "Attendance_Sheet")
public class Attendance {

    @ColumnInfo(name = "studentName")
    private final String name;

    @PrimaryKey(autoGenerate = true)
     int s_no = 0;

    @ColumnInfo(name = "rollNo")
    private int rollNo = 0;

    @ColumnInfo(name = "className")
    private String className;

    @NonNull
    @ColumnInfo(name = "student_status")
    private String student_status;

    @ColumnInfo(name = "isStruckOff")
    private boolean isStruckOff;


    public Attendance(@NonNull String name, int rollNo, String className,String student_status, boolean isStruckOff){
        this.name = name;
        this.rollNo = rollNo;
        this.className = className;
        this.student_status = student_status;
        this.isStruckOff = isStruckOff;
    }

    @Ignore
    public Attendance(int s_no, @NonNull String name, int rollNo, String className,String student_status,boolean isStruckOff){
        this.name = name;
        this.rollNo = rollNo;
        this.className = className;
        this.student_status = student_status;
        this.s_no = s_no;
        this.isStruckOff = isStruckOff;
    }

    public boolean isStruckOff() {
        return isStruckOff;
    }

    public void setStruckOff(boolean struckOff) {
        isStruckOff = struckOff;
    }

    public String getStudent_status() {
        return student_status;
    }

    public void setStudent_status(String student_status) {
        this.student_status = student_status;
    }

    public String getName(){
        return this.name;
    }

    public int getRollNo(){
        return this.rollNo;
    }

    public int getS_no() {
        return s_no;
    }

    public void setS_no(int s_no) {
        this.s_no = s_no;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
