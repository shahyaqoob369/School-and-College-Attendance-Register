package com.acode.attendanceHome.roomDataBase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AttendanceViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String className;

    public AttendanceViewModelFactory(Application mApplication, String className) {
        this.mApplication = mApplication;
        this.className = className;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AttendanceViewModel(mApplication, className);
    }
}
