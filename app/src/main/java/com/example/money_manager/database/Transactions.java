package com.example.money_manager.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transactions {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "transaction_type")
    private String type;

    @ColumnInfo(name = "transaction_value")
    private int value;

    @ColumnInfo(name = "cat_id")
    private int catId;

    @ColumnInfo(name = "user_id", defaultValue = "NULL")
    private String userId;


    @ColumnInfo(name = "date", defaultValue = "NULL")
    private String date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
