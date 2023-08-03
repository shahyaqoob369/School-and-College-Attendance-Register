package com.acode.attendanceHome.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acode.attendanceHome.R;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.List;

public class MonthlyReportAdaptor extends BaseAdapter {

    Context ctx;
    List<DailyAttendance> months;
    LayoutInflater layoutInflater;


    public MonthlyReportAdaptor(Context ctx, List<DailyAttendance> months) {
        this.ctx = ctx;
        this.months = months;
        this.layoutInflater = (LayoutInflater.from(ctx));
    }


    @Override
    public int getCount() {
        return months.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.activity_report_list_item,null);

        TextView textView = convertView.findViewById(R.id.date_list_item);

        textView.setText(getMonth(months.get(position).getAttendanceDate()));
        return convertView;
    }

    private String getMonth(String attendanceDate) {
        return attendanceDate.length() >2 ? attendanceDate.substring(3) : "";
    }

}
