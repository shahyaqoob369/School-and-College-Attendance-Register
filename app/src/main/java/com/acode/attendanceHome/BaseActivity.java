package com.acode.attendanceHome;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.acode.attendanceHome.fragments.AttendanceFragment;
import com.acode.attendanceHome.roomDataBase.AttendanceViewModel;
import com.acode.attendanceHome.roomDataBase.AttendanceViewModelFactory;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public static AttendanceViewModel mViewModel;
    public static String EXTRA_NAME = "EXTRA_NAME";
    public static String EXTRA_RN = "EXTRA_RN";
    public static String EXTRA_CLS = "EXTRA_CLS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(
                this, new AttendanceViewModelFactory(this.getApplication(), AttendanceFragment.classname))
                .get(AttendanceViewModel.class);

        AdManager.loadInterstitial(BaseActivity.this);
    }

}
