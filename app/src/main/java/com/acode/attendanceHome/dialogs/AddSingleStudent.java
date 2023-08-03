package com.acode.attendanceHome.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
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
import com.acode.attendanceHome.fragments.AttendanceFragment;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.R;

import java.util.List;

public class AddSingleStudent extends DialogFragment {
    EditText addStudent_text, addRollNo_text;
    Button saveBtn_fragment;
    Dialog dialog;
    List<Attendance> currentList;
    Context mContext;

    public AddSingleStudent(List<Attendance> currentList, Context mContext) {
        this.currentList = currentList;
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_single_students, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addStudent_text = view.findViewById(R.id.addName_text);
        addRollNo_text = view.findViewById(R.id.addRollNo_text);

        saveBtn_fragment = view.findViewById(R.id.saveBtn_fragment);
        addRollNo_text.setInputType(InputType.TYPE_CLASS_PHONE);
        saveBtn_fragment.setOnClickListener(v -> {

            if (addRollNo_text.getText().length() == 0) {
                Toast.makeText(getContext(), "Enter Roll Number", Toast.LENGTH_SHORT).show();
            } else {
                boolean isRollNoAvailable = false;
                String name = addStudent_text.getText().toString();
                String numberOnly= addRollNo_text.getText().toString().replaceAll("[^0-9]", "");
                int rollNo = Integer.parseInt(numberOnly);
                for (Attendance attendance : currentList) {
                    if (attendance.getRollNo() == rollNo) {
                        isRollNoAvailable = true;
                        Toast.makeText(mContext, "Roll number is already exist", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!isRollNoAvailable) {
                    String classNam = AttendanceFragment.classname;
                    BaseActivity.mViewModel.insert(new Attendance(name, rollNo, classNam, "Present", false));
                    Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
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
