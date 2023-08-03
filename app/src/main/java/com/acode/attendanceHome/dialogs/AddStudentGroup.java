package com.acode.attendanceHome.dialogs;

import android.app.Dialog;
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
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.fragments.AttendanceFragment;
import com.acode.attendanceHome.roomDataBase.Attendance;

public class AddStudentGroup extends DialogFragment {
    EditText from_Text, to_Text;
    Button saveBtn_fragment;
    Dialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_students_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        from_Text = view.findViewById(R.id.from_text);
        to_Text = view.findViewById(R.id.to_text);

        saveBtn_fragment = view.findViewById(R.id.saveBtn_fragment);
        to_Text.setInputType(InputType.TYPE_CLASS_PHONE);
        from_Text.setInputType(InputType.TYPE_CLASS_PHONE);
        saveBtn_fragment.setOnClickListener(v -> {

            if (to_Text.getText().length() == 0 || from_Text.getText().length() == 0) {
                Toast.makeText(getContext(), "Set Range first", Toast.LENGTH_SHORT).show();
            } else {
                int from_RN = Integer.parseInt(from_Text.getText().toString());
                int to_RN = Integer.parseInt(to_Text.getText().toString());
                String classNam = AttendanceFragment.classname;
                for (int i = from_RN; i <= to_RN; i++) {
                    BaseActivity.mViewModel.insert(new Attendance("no name", i, classNam,"Present",false));
                }
                Toast.makeText(getContext(), "From " + from_RN + " To " + to_RN + " save successfully", Toast.LENGTH_SHORT).show();
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
