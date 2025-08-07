package com.example.money_manager.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration2to3 extends Migration {
    public Migration2to3(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Migration code from version 2 to version 3
        database.execSQL("DROP TABLE IF EXISTS `expense_category`");
        database.execSQL("DROP TABLE IF EXISTS `schedule`");

        // Recreate the tables with the correct schema
        database.execSQL("CREATE TABLE IF NOT EXISTS `expense_category` (`cid` INTEGER PRIMARY KEY NOT NULL, `category_name` TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS `schedule` (`sid` INTEGER PRIMARY KEY NOT NULL, `date` TEXT, `category_name` TEXT, `value` INTEGER NOT NULL)");
    }
    }

