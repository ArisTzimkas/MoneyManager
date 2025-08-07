package com.example.money_manager.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Transactions.class,Category.class, Schedule.class},version = 3)
public abstract class MyDatabase extends RoomDatabase {
    public abstract MyDao myDao();
}
