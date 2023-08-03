package com.acode.attendanceHome.roomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "addClass_name")
public class AddClassName {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "className")
    private final String className;

    public AddClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

}
