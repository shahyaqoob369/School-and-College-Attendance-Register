package com.acode.attendanceHome.roomDataBase;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class AttendanceViewModel extends AndroidViewModel {

    private static final String TAG = "ViewModel";
    private final AttendanceRepository attendanceRepository;
    public final LiveData<List<Attendance>> allAttendance;
    public final LiveData<List<AddClassName>> allClass;

    MutableLiveData<String> selectionCount = new MutableLiveData<>();

    public void setSelectionCount(String s) {
        Log.d(TAG, "setSelectionCount: called");
        selectionCount.setValue(s);
    }
    public MutableLiveData<String> getCount() {
        Log.d(TAG, "getCount: called");
        return selectionCount;
    }

    public AttendanceViewModel(@NonNull Application application, String className){
        super(application);
        attendanceRepository = new AttendanceRepository(application);
        allAttendance = attendanceRepository.getAllAttendanceLiveData();
        allClass = attendanceRepository.getAllClassLiveData();

    }

     public LiveData<List<Attendance>> getAllAttendance(){
        return allAttendance;
    }

    public void insert(Attendance attendance){
        attendanceRepository.insert(attendance);
    }
    public void deleteStudentByClass(String className){
        attendanceRepository.deleteStudentByClass(className);
    }
    public void deleteDailyAttendanceByClass(String className){
        attendanceRepository.deleteDailyAttendanceByCLass(className);
    }
    public void deleteStu(Attendance attendance) {
        attendanceRepository.deleteStu(attendance);
    }
    public LiveData<List<AddClassName>> getAllClass(){
        return allClass;
    }

    public void insert(AddClassName addClassName){
        attendanceRepository.insert(addClassName);
    }
    public void delete(AddClassName addClassName){
        attendanceRepository.delete(addClassName);
    }

    public LiveData<List<Attendance>> getallStudentsByClsNm(String className){
        return attendanceRepository.getallStudentsByClsName(className);
    }
    public LiveData<List<DailyAttendance>> getAllStudentAttendanceByNm(String studentName, int studentRN, String studentCls) {
        return attendanceRepository.getAllStudentAttendance(studentName, studentRN, studentCls);
    }

    public LiveData<List<DailyAttendance>> getAllClassAttendanceByCls(String studentCls) {
        return  attendanceRepository.getAllClassAttendance(studentCls);
    }

    public LiveData<List<DailyAttendance>> getAllDailyAttendanceStudentName(String classname, int rolno){
        return attendanceRepository.getAllDailyAttendanceStudentName(classname, rolno);
    }

    public void insertDailyAttendance(List<DailyAttendance> dailyAttendanceList){
        attendanceRepository.inserDailyAttendance(dailyAttendanceList);
    }

    public LiveData<String> isAttendanceAdded(String date, String ClsName) {
        return attendanceRepository.isAttendanceAdded(date, ClsName);
    }

    // query for edit activity
    public LiveData<List<DailyAttendance>> getAllStudentByClassNameAndDate(String className, String date){
        return attendanceRepository.getAllStudentByClassNameAndDate(className,date);
    }

    public LiveData<List<DailyAttendance>> getAttendanceForMonthlyReport (String className) {
        return attendanceRepository.getAttendanceForMonthlyReport(className);
    }

    public List<DailyAttendance> getMonthlyWiseData(String date, String cn){
        return attendanceRepository.getMonthlyWiseData(date,cn);
    }

}
