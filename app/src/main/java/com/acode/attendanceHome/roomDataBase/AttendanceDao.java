package com.acode.attendanceHome.roomDataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AttendanceDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Attendance attendance_sheet);

    @Query("DELETE FROM attendance_sheet WHERE className = :className")
    abstract void deleteStudentByClass(String className);

    @Query("DELETE FROM daily_attendance WHERE studentClass = :className")
    abstract void deleteDailyAttendanceByClass(String className);

    @Query("SELECT * FROM attendance_sheet ORDER BY rollNo ASC")
    LiveData<List<Attendance>> getAllAttendance();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AddClassName addClass_name);

    @Delete
    void deleteStudentByClass(AddClassName addClassName);

    @Delete
    void deleteStudent(Attendance attendance);

    @Query("SELECT * FROM addClass_name")
    LiveData<List<AddClassName>> getAllClass();

    //query for get students list by class name:
    @Query("SELECT * FROM Attendance_Sheet WHERE className = :className ORDER BY rollNo ASC")
    LiveData<List<Attendance>> getAllStudentByClassName(String className);

    //query for get attendance edit activity
    @Query("SELECT * FROM daily_attendance WHERE studentClass = :className AND attendanceDate = :date ORDER BY studentRollNo ASC")
    LiveData<List<DailyAttendance>> getAllStudentByClassNameAndDate(String className, String date);

    //query for get get daily attendance for monthlyReportlist activity
    @Query("SELECT * FROM daily_attendance WHERE studentClass = :className ORDER BY attendanceDate ASC")
    LiveData<List<DailyAttendance>> getAttendanceForMonthlyReport (String className);


    //query for get studentsReport:
    @Query("SELECT * FROM daily_attendance WHERE studentName = :studentName AND studentRollNo = :studentRN " +
            "AND studentClass = :studentCls ORDER BY attendanceDate ASC")
    LiveData<List<DailyAttendance>> getAllStudentAttendance(String studentName, int studentRN, String studentCls);

    //query for get classReport:
    @Query("SELECT * FROM daily_attendance WHERE studentClass = :studentCls ORDER BY attendanceDate ASC")
    LiveData<List<DailyAttendance>> getAllClassAttendance(String studentCls);

    @Query("SELECT * FROM daily_attendance WHERE studentName = :classname AND studentRollNo = :rolno")
    LiveData<List<DailyAttendance>> getAllDailyAttendanceStudentName(String classname, int rolno);


    @Query("SELECT attendanceDate FROM daily_attendance WHERE attendanceDate = :date AND studentClass = :ClsName LIMIT 1")
    LiveData<String> isAttendanceAdded(String date, String ClsName);

    //daily student_attendance
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDailyAttendance(List<DailyAttendance> dailyAttendanceList);


    @Query("SELECT * FROM daily_attendance WHERE studentClass = :cn AND attendanceDate LIKE  '%' || :date || '%' ")
    List<DailyAttendance> getMonthlyWiseData(String date,String cn);
}
