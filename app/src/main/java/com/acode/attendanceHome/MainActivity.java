package com.acode.attendanceHome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.dialogs.AddClass;
import com.acode.attendanceHome.dialogs.ClassBottomDialog;
import com.acode.attendanceHome.recyclerview.AddClassAdopter;
import com.acode.attendanceHome.recyclerview.RecyclerItemClickListener;
import com.acode.attendanceHome.roomDataBase.AddClassName;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements RecyclerItemClickListener.OnRecyclerClickListener {

    private static final String TAG = "Main";
    //for sending name and subject through intent from mainActivity to studentActivity:
    public static final String CLASS_NAME = "CLASS_NAME";
    public static final String DATE = "DATE";
    FloatingActionButton add_class_btn;
    public static List<AddClassName> classesList;
    public static int pos;
    Intent intent;
    LinearLayout help_layOut;

    //Navigation Drawer:
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set ActionBar:
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>CLASSES </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));
        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        classesList = new ArrayList<>();

        //AdMob ads:
        MobileAds.initialize(this, initializationStatus -> {
        });

        help_layOut = findViewById(R.id.help_layOut);
        add_class_btn = findViewById(R.id.add_class_btn);
        // drawer layout instance to toggle the menu icon to open
        drawerLayout = findViewById(R.id.my_drawer_layout);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_xml);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        mViewModel.getAllClass().observe(this, addClassNames -> {
            classesList = addClassNames;
            final AddClassAdopter addClassAdopter = new AddClassAdopter(classesList);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            recyclerView.setAdapter(addClassAdopter);
            if (classesList != null) {
                if (!classesList.isEmpty()) {
                    help_layOut.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        add_class_btn.setOnClickListener(v -> {
            addClass();
        });

        // drawer and back button to close drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.nav_open,
                R.string.nav_close);

        //Navigation Drawer:
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.nav_help:
                    intent = new Intent(MainActivity.this, HelpActivity.class);
                    startActivity(intent);
                    break;

                case R.id.nav_aboutUs:
                    intent = new Intent(MainActivity.this, AboutUsActivity.class);
                    startActivity(intent);
                    break;

                case R.id.nav_exit:
                    finish();
                    break;

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        AppRater.appLaunched(this);
    }

    @Override
    public void onItemClick(View view, int position, AddClassName addClassName) {
        Log.d(TAG, "onItemClick: starts");

            String classname = addClassName.getClassName();
            Intent intent = new Intent(MainActivity.this, Host_Activity.class);
            intent.putExtra(CLASS_NAME, classname);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.click_anim);
            view.startAnimation(animation);
            startActivity(intent);

    }

    //to show add class dialog
    public void addClass() {
        DialogFragment dialogFragment = new AddClass();
        dialogFragment.show(getSupportFragmentManager(), "addClass");
    }

    @Override
    public void onItemLongClick(View view, int position, String classname) {
        pos = position;
        showBottomDialog(classname);
    }

    private void showBottomDialog(String classname) {
        ClassBottomDialog bottomDialog = new ClassBottomDialog(classname);
        bottomDialog.show(getSupportFragmentManager(), "ClassBottomDialog");
    }

    //Navigation Drawer:
    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

