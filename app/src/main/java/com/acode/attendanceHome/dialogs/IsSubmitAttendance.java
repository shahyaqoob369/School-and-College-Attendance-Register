package com.acode.attendanceHome.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.acode.attendanceHome.Host_Activity;

public class IsSubmitAttendance extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Attendance of the same date is already submitted." +
                " Press OK, go to REPORT and update attendance of the same date")
                .setPositiveButton("OK", (dialog, id) -> Host_Activity.viewPager.setCurrentItem(2))
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
