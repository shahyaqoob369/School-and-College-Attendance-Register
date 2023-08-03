package com.acode.attendanceHome.dialogs;

import static com.acode.attendanceHome.BaseActivity.mViewModel;
import static com.acode.attendanceHome.MainActivity.pos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acode.attendanceHome.AdManager;
import com.acode.attendanceHome.Host_Activity;
import com.acode.attendanceHome.MainActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.monthlyReport.MonthlyReportListActivity;
import com.acode.attendanceHome.roomDataBase.AddClassName;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ClassBottomDialog extends BottomSheetDialogFragment {
    TextView editclass_name, delete_class, report_class, report_share;
    public static final String CLASS_NAME = "CLASS_NAME";
    String className;

    public ClassBottomDialog(String className) {
        this.className = className;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomdialog_layout, container, false);

        delete_class = v.findViewById(R.id.delete_class);
        report_class = v.findViewById(R.id.report_class);
        report_share = v.findViewById(R.id.report_share);

        report_share.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MonthlyReportListActivity.class);
            intent.putExtra("cn", className);
            startActivity(intent);

            //AdMob InterstitialAd:
            if (AdManager.mInterstitialAd != null) {
                AdManager.mInterstitialAd.show(requireActivity());

                AdManager.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        AdManager.mInterstitialAd = null;
                        AdManager.loadInterstitial(requireActivity());
                    }
                });
            }

            dismiss();
        });

        report_class.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), Host_Activity.class);
            intent.putExtra(CLASS_NAME, className);
            intent.putExtra("REPORT", "REPORT");
            startActivity(intent);
            dismiss();
        });

        delete_class.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete this Class?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                AddClassName addClassName = MainActivity.classesList.get(pos);
                mViewModel.delete(addClassName);
                mViewModel.deleteStudentByClass(addClassName.getClassName());
                mViewModel.deleteDailyAttendanceByClass(addClassName.getClassName());
                dismiss();
            });
            builder.setNegativeButton("No", (dialog, which) -> dismiss());
            builder.show();
            dismiss();
        });
        return v;
    }
}
