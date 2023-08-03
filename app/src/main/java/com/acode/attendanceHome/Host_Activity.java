package com.acode.attendanceHome;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.acode.attendanceHome.fragments.AttendanceFragment;
import com.acode.attendanceHome.fragments.ReportFragment;
import com.acode.attendanceHome.fragments.StudentListFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Host_Activity extends BaseActivity {

    private static final String TAG = "HOST_ACTIVITY";
    public static ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);


        //for get class name and subject through intent
        Intent intent = this.getIntent();
        String classname = intent.getStringExtra(MainActivity.CLASS_NAME);
        String report = intent.getStringExtra("REPORT");
        Log.d(TAG, "getInten = "+ report);
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFFF'> " + classname + " </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));

        viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tablayout);

        tabLayout.addTab(tabLayout.newTab().setText("Students").setIcon(R.drawable.ic_person_24));
        tabLayout.addTab(tabLayout.newTab().setText("Attendance").setIcon(R.drawable.ic_class));
        tabLayout.addTab(tabLayout.newTab().setText("Report").setIcon(R.drawable.ic_baseline_report_24));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyAdaptor myAdaptor = new MyAdaptor(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(myAdaptor);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (report != null && report.equals("REPORT")){
            viewPager.setCurrentItem(2);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public static class MyAdaptor extends FragmentPagerAdapter {

        private Context mContext;
        int totalTabs;

        public MyAdaptor(@NonNull FragmentManager fm, Context mcontext, int totalTabs) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mContext = mcontext;
            this.totalTabs = totalTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StudentListFragment();
                case 1:
                    return new AttendanceFragment();
                case 2:
                    return new ReportFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }

}