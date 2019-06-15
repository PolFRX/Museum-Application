package com.android.museum;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by lhmachine on 2019/6/8.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_MUSEUM = "create table Museum(" +
            "id text, " +
            "time text," +
            "name text," +
            "open_time text," +
            "address text," +
            "city text, " +
            "close_time text," +
            "close_time_annual text," +
            "website text," +
            "cp text," +
            "region text, " +
            "province text)";

    private Context mContext;

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_MUSEUM);
        Toast.makeText(mContext, "Create Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

}
