package com.example.ergasia.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

@Database(entities = {Transactions.class,Category.class, Schedule.class},version = 3)
public abstract class MyDatabase extends RoomDatabase {
    public abstract MyDao myDao();
}
