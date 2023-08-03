package com.acode.attendanceHome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

public class AboutUsActivity extends AppCompatActivity {

    LinearLayout us_facebook, us_twitter, us_email, us_policy;
    TextView app_text, us_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //Set ActionBar:
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>ABOUT US </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));


        us_facebook = findViewById(R.id.us_facebook);
        us_twitter = findViewById(R.id.us_twitter);
        us_email = findViewById(R.id.us_email);
        us_policy =findViewById(R.id.us_policy);
        app_text = findViewById(R.id.textView2);
        us_txt = findViewById(R.id.us_txt);

        us_txt.setText(R.string.about_a_code);
        app_text.setText(R.string.a_code);
        app_text.setGravity(Gravity.CENTER);

        us_facebook.setOnClickListener(view -> {
            final String urlFb = "https://web.facebook.com/a.code8084101/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urlFb));

            // If a Facebook app is installed, use it. Otherwise, launch
            // a browser
            final PackageManager packageManager = getPackageManager();
            List<ResolveInfo> list =
                    packageManager.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() == 0) {
                final String urlBrowser = "https://web.facebook.com/a.code8084101/";
                intent.setData(Uri.parse(urlBrowser));
            }

            startActivity(intent);
        });

        us_twitter.setOnClickListener(v -> {
            try {
                // Check if the Twitter app is installed on the phone.
                this.getPackageManager().getPackageInfo("com.twitter.android", 0);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
                // Don't forget to put the "L" at the end of the id.
                intent.putExtra("user_id", 342391L);
                startActivity(intent);
            } catch (Exception e) {
                // If Twitter app is not installed, start browser.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/a_code8084101")));
            }
        });

        us_email.setOnClickListener(v -> {

            Intent intent=new Intent(Intent.ACTION_SEND);
            String[] recipients={"a.code8084101@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject here");
            intent.putExtra(Intent.EXTRA_TEXT,"Body of the content here...");
            intent.putExtra(Intent.EXTRA_CC,"yaqigpag@gmail.com");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));
        });

        us_policy.setOnClickListener(v -> {
            goToUrl("https://docs.google.com/document/d/13BjxX22arTmYw2ubjMfvK3Sj1qP48_z6lnyS6JxQ_iY/edit?usp=sharing");
        });
    }

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}