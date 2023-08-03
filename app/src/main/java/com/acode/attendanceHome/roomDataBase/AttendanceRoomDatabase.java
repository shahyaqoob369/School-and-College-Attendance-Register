package com.acode.attendanceHome.roomDataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Attendance.class, AddClassName.class, DailyAttendance.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AttendanceRoomDatabase extends RoomDatabase {

    public abstract AttendanceDao attendanceDao();

    //for background Threads working:
    private static volatile AttendanceRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService dataBaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Creating singleton Class:
    public static AttendanceRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AttendanceRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AttendanceRoomDatabase.class , "attendance_database").build();
                }
            }
        }
        return INSTANCE;
    }

}