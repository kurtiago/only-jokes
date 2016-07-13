package com.tiago.onlyjokes;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "onlyJokesDatabase3.db"; //TODO I'm changing database name so it does not crash
    private static final int DATABASE_VERSION = 1;  //update database: https://github.com/jgilfelt/android-sqlite-asset-helper/blob/master/samples/database-v2-upgrade/src/main/assets/databases/northwind.db_upgrade_1-2.sql

    //@Override
    //public void setForcedUpgrade() {
    //    super.setForcedUpgrade();
    //}

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
