package com.acode.attendanceHome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class AppRater {

    private static final String TAG = "AppRater";
    private static final int DAYS_UNTIL_PROMPT = 2; //Min number of days
    private static final int LAUNCHES_UNTIL_PROMPT = 3; //Min number of launches

    public static void appLaunched(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launchCount = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launchCount);

        // Get date of first launch
        long dateFirstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", dateFirstLaunch);
        }

        // Wait at least n days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch + DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
                showReview(mContext);
            }
        }
        editor.apply();
    }

    private static void showReview(Activity mContext) {
        ReviewManager manager = ReviewManagerFactory.create(mContext);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo info = task.getResult();
                if (info != null) {
                    manager.launchReviewFlow(mContext, info);
                    Log.d(TAG, "review is successful " + task.getResult());
                }

            } else {
                String reviewErrorCode = task.getException().getMessage();
                Log.d(TAG, "Review got error " + reviewErrorCode);
            }
        });
    }
}