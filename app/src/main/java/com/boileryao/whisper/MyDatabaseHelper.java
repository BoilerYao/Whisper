package com.boileryao.whisper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by BoilerYao on 2016/10/2.
 * SQLite Open Helper
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";

    //Create Table in DB statement
    private static final String CREATE_MESSAGE = "create table Message("
            + "id integer primary key autoincrement,"
            + "time integer,"
            + "content text,"
            + "user_id text)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE);
        Log.i(TAG, "onCreate: SUCCESS");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing
    }
}
