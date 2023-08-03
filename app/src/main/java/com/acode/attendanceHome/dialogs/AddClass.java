package com.acode.attendanceHome.dialogs;

import android.app.Dialog;
import android.os.Bundle;
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
import com.acode.attendanceHome.roomDataBase.AddClassName;
import com.acode.attendanceHome.R;

public class AddClass extends DialogFragment {
    EditText addClass_text;
    Button createBtn_fragment, cancelBtn_fragment;
    Dialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addClass_text = view.findViewById(R.id.addClass_text);
        createBtn_fragment = view.findViewById(R.id.createBtn_fragment);
        createBtn_fragment.setOnClickListener(v -> {
            String name = addClass_text.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Filled the Required fields", Toast.LENGTH_SHORT).show();
            } else {
                BaseActivity.mViewModel.insert(new AddClassName(name));
                Toast.makeText(getContext(), "Class created successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        cancelBtn_fragment = view.findViewById(R.id.cancelBtn_fragment);
        cancelBtn_fragment.setOnClickListener(v -> {
            dismiss();
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
