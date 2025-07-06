package com.example.ergasia.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedule")
public class Schedule {
    @PrimaryKey
    private int sid;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "category_name")
    private String cat_name;

    @ColumnInfo(name = "value")
    private int value;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
