package com.acode.attendanceHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.acode.attendanceHome.walkthrouhScreen.OnBoarding;

import java.util.Objects;


public class StartupActivity extends BaseActivity {

    SharedPreferences onBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {

            onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
            boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

            if (isFirstTime) {

                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

                Intent intent = new Intent(StartupActivity.this, OnBoarding.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                startActivity(intent);
            }
            finish();

        }, 2500);
    }
}