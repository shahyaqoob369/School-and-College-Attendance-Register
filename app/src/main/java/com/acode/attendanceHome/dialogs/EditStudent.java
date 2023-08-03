package com.acode.attendanceHome.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.acode.attendanceHome.BaseActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.fragments.StudentListFragment;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.ArrayList;
import java.util.List;

public class EditStudent extends DialogFragment {
    private static final String TAG = "EDITSTUDENT";
    EditText editStudent_name, editStudent_rN;
    Button saveBtn_editStudent;
    Dialog dialog;
    Attendance attendance;

    public EditStudent(Attendance attendance) {
        this.attendance = attendance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editStudent_name = view.findViewById(R.id.editStudent_name);
        editStudent_rN = view.findViewById(R.id.editStudent_rN);

        saveBtn_editStudent = view.findViewById(R.id.saveBtn_editStudent);
        editStudent_rN.setInputType(InputType.TYPE_CLASS_PHONE);


        editStudent_name.setText(attendance.getName());
        editStudent_rN.setText(String.valueOf(attendance.getRollNo()));


        saveBtn_editStudent.setOnClickListener(v -> {

            if (editStudent_rN.getText().length() == 0) {

                Toast.makeText(getContext(), "Filled the Required fields", Toast.LENGTH_SHORT).show();
            } else {
                List<DailyAttendance> newList = new ArrayList<>();
                String name = editStudent_name.getText().toString();
                String value = editStudent_rN.getText().toString();
                int rn = Integer.parseInt(value);
                StudentListFragment st = new StudentListFragment();
                BaseActivity.mViewModel.insert(new Attendance(
                        attendance.getS_no(),
                        name,
                        rn,
                        attendance.getClassName(),
                        "Present",
                        false));
                for (DailyAttendance daily: StudentListFragment.getEditStudentList()) {
                    DailyAttendance newDaily = new DailyAttendance(
                            daily.getsNo(),
                            rn,
                            name,
                            daily.getStudentClass(),
                            daily.getAttendanceDate(),
                            daily.getStudentStatus()
                    );

                    newList.add(newDaily);
                    Log.d(TAG, "Edit list = " +name);
                }
                BaseActivity.mViewModel.insertDailyAttendance(newList);
                Toast.makeText(getContext(), "Edit successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        return dialog;
    }
}
