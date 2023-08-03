package com.acode.attendanceHome.roomDataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttendanceRepository {

    private AttendanceDao attendanceDao;
    private LiveData<List<Attendance>> getallAttendance;
    private LiveData<List<AddClassName>> getAllClass;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    AttendanceRepository (Application application){
        AttendanceRoomDatabase db = AttendanceRoomDatabase.getDatabase(application);
        attendanceDao = db.attendanceDao();
        getallAttendance = attendanceDao.getAllAttendance();
        getAllClass = attendanceDao.getAllClass();

    }

   public LiveData<List<Attendance>> getAllAttendanceLiveData() {
        return getallAttendance;
   }

    void insert(Attendance attendance){
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()-> {
            attendanceDao.insert(attendance);
        });
    }
    void deleteStudentByClass(String className){
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()-> {
            attendanceDao.deleteStudentByClass(className);
        });
    }

    void deleteDailyAttendanceByCLass(String className){
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()-> {
            attendanceDao.deleteDailyAttendanceByClass(className);
        });
    }

    public LiveData<List<AddClassName>> getAllClassLiveData() {
        return getAllClass;
    }

    void insert(AddClassName addClassName){
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()->{
            attendanceDao.insert(addClassName);
    });
    }
    void delete(AddClassName addClassName){
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()->{
            attendanceDao.deleteStudentByClass(addClassName);
        });
    }

    void deleteStu(Attendance attendance) {
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()->{
            attendanceDao.deleteStudent(attendance);
        });
    }

    public LiveData<List<Attendance>> getallStudentsByClsName(String className){
       return attendanceDao.getAllStudentByClassName(className);
    }

    public LiveData<List<DailyAttendance>> getAllStudentAttendance(String studentName, int studentRN, String studentCls) {
        return attendanceDao.getAllStudentAttendance(studentName, studentRN, studentCls);
    }

    public LiveData<List<DailyAttendance>> getAllClassAttendance(String studentCls) {
        return attendanceDao.getAllClassAttendance(studentCls);
    }


    public LiveData<List<DailyAttendance>> getAllDailyAttendanceStudentName(String classname, int rolno){
        return attendanceDao.getAllDailyAttendanceStudentName(classname, rolno);
    }


    void inserDailyAttendance(List<DailyAttendance> dailyAttendanceList) {
        AttendanceRoomDatabase.dataBaseWriteExecutor.execute(()->{
            attendanceDao.insertDailyAttendance(dailyAttendanceList);
        });
    }

    public LiveData<String> isAttendanceAdded(String date, String ClsName) {
        return attendanceDao.isAttendanceAdded(date, ClsName);
    }

    // query for edit activity
    public LiveData<List<DailyAttendance>> getAllStudentByClassNameAndDate(String className, String date){
        return attendanceDao.getAllStudentByClassNameAndDate(className,date);
    }

    public LiveData<List<DailyAttendance>> getAttendanceForMonthlyReport (String className) {
        return attendanceDao.getAttendanceForMonthlyReport(className);
    }

    public List<DailyAttendance> getMonthlyWiseData(String date,String cn) {
        final List<DailyAttendance> list = new ArrayList<>();

        executorService.execute(() -> {
            list.clear();
            list.addAll(attendanceDao.getMonthlyWiseData(date,cn));
        });
        return list;
    }

}
