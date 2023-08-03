package com.acode.attendanceHome.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.roomDataBase.AddClassName;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListener";

    public interface OnRecyclerClickListener{
        void onItemClick(View view, int position, AddClassName addClassName);
        void onItemLongClick(View view, int position, String classname);
    }
    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener mListener) {
        this.mListener = mListener;
        this.mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @SuppressLint("LongLogTag")
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    int position = recyclerView.getChildAdapterPosition(childView);

                    AddClassName addClassName = AddClassAdopter.addClassNameList.get(position);
                    mListener.onItemClick(childView, position, addClassName);
                }
                return true;
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("TAG", "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    Log.d(TAG, "onLongPress: calling listener.onLongPressClick");
                    AddClassName addClassName = AddClassAdopter.addClassNameList.get(recyclerView.getChildAdapterPosition(childView));
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView), addClassName.getClassName());

                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if (mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned: " + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent(): returned false");
            return false;
        }

    }
}
