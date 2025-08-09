package com.example.money_manager.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration1to2 extends Migration {
    public Migration1to2(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {


        // For `cat_id`, keeping it NOT NULL with a DEFAULT value if that aligns with business logic
        database.execSQL("ALTER TABLE transactions ADD COLUMN cat_id INTEGER NOT NULL DEFAULT 0");

        // For `user_id` and `date`, allowing NULL with no default, which should be expressed as DEFAULT NULL
        database.execSQL("ALTER TABLE transactions ADD COLUMN user_id TEXT DEFAULT NULL");
        database.execSQL("ALTER TABLE transactions ADD COLUMN date TEXT DEFAULT NULL");
        database.execSQL("CREATE TABLE IF NOT EXISTS `expense_category` (`cid` INTEGER NOT NULL, `category_name` TEXT, PRIMARY KEY(`cid`))");
        database.execSQL("CREATE TABLE IF NOT EXISTS `schedule` (`sid` INTEGER NOT NULL, `date` TEXT, `category_name` TEXT, `value` INTEGER, PRIMARY KEY(`sid`))");
    }
}

