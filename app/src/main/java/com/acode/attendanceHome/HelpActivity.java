package com.acode.attendanceHome;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Objects;

public class HelpActivity extends AppCompatActivity {

    TextView FullGuide, xls_video;

    //AdMob ads:
    private AdView bannerAd_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFFF'> Help</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));

        //adMob BannerAd
        bannerAd_help = findViewById(R.id.bannerAd_help);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd_help.loadAd(adRequest);


        FullGuide = findViewById(R.id.full_guide);
        xls_video = findViewById(R.id.xls_video);

        FullGuide.setOnClickListener(v -> goToUrl("https://youtu.be/7TgLqoRjc3k/"));

        xls_video.setOnClickListener(v -> goToUrl("https://youtu.be/PZHNTVbNW2k"));
    }

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}