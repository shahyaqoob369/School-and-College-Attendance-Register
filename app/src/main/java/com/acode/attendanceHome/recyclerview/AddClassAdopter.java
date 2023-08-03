package com.acode.attendanceHome.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.AdManager;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.roomDataBase.AddClassName;

import java.util.List;

public class AddClassAdopter extends RecyclerView.Adapter<AddClassAdopter.AddClassNameViewHolder> {

    public static List<AddClassName> addClassNameList;
    private final AdManager adManager = new AdManager();

    public AddClassAdopter(List<AddClassName> addClassNameListView) {
        addClassNameList = addClassNameListView;
    }

    @NonNull
    @Override
    public AddClassNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addclass_item_view, parent, false);
        return new AddClassNameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddClassNameViewHolder holder, int position) {
        AddClassName currentPosition = addClassNameList.get(position);

        holder.classNameInCard_view.setText(currentPosition.getClassName());

    }

    @Override
    public int getItemCount() {
        return addClassNameList.size();
    }


    static class AddClassNameViewHolder extends RecyclerView.ViewHolder{

        private final TextView classNameInCard_view;


        public AddClassNameViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameInCard_view = itemView.findViewById(R.id.classNameInCard_view);
        }
    }



}
