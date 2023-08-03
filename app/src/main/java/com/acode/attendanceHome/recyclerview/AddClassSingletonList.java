package com.acode.attendanceHome.recyclerview;

import android.util.Log;

import com.acode.attendanceHome.roomDataBase.AddClassName;

import java.util.ArrayList;
import java.util.List;

public class AddClassSingletonList {
    private static final String TAG = "SingletonList";

    private final List<AddClassName> myAddClassList;

    private static AddClassSingletonList instance;

    private AddClassSingletonList() {
        this.myAddClassList = new ArrayList<AddClassName>();
    }

    public static AddClassSingletonList getInstance() {
        Log.d(TAG, "getInstance: : instance Created");
        if (instance == null) {
            instance = new AddClassSingletonList();
        }
        return instance;
    }

    public List<AddClassName> getMyAddClassList() {
        return myAddClassList;
    }
}
