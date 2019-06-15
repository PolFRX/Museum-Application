package com.android.museum;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.museum.Model.Museum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lhmachine on 2019/6/8.
 */

public class SQLiteHelper {

    private MySQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public SQLiteHelper(Context context){
        // 连接数据库
        dbHelper = new MySQLiteOpenHelper(context, "museum.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    public List<HashMap<String, String>> get_museum_list(){
        List<HashMap<String, String>> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT `id`, `time`,`name` FROM Museum", null);
        if (cursor.moveToFirst()){
            do{
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(0));
                map.put("time", cursor.getString(1));
                map.put("name", cursor.getString(2));
                result.add(map);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public Museum get_museum_detail(String id){
        String[] result = new String[12];
        Museum museum = new Museum();
        Cursor cursor = db.rawQuery("SELECT * FROM Museum WHERE id = '"+id+"'", null);
        if (cursor.moveToFirst()){
            do{
                museum.setId(cursor.getString(0));
                museum.setTime(cursor.getString(1));
                museum.setNom(cursor.getString(2));
                museum.setPeriode_ouverture(cursor.getString(3));
                museum.setAdresse(cursor.getString(4));
                museum.setVille(cursor.getString(5));
                museum.setFerme(Boolean.valueOf(cursor.getString(6)));
                museum.setFermeture_annuelle(cursor.getString(7));
                museum.setSite_web(cursor.getString(8));
                museum.setCp(cursor.getString(9));
                museum.setRegion(cursor.getString(10));
                museum.setDept(cursor.getString(11));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return museum;
    }

    public void add_museum(String[] detail){
        Cursor cursor = db.rawQuery("SELECT * FROM Museum WHERE id = '"+detail[0]+"'", null);
        if (!cursor.moveToFirst()){
            String query = "INSERT INTO Museum(id, time, name, open_time, address, city, close_time, " +
                    "close_time_annual, website, cp, region, province) VALUES " + "(\""+detail[0]+"\", " +
                    "\""+detail[1]+"\", \""+detail[2]+"\", \""+detail[3]+"\", \""+detail[4]+"\", " +
                    "\""+detail[5]+"\", \""+detail[6]+"\", \""+detail[7]+"\", \""+detail[8]+"\", " +
                    "\""+detail[9]+"\", \""+detail[10]+"\", \""+detail[11]+"\")";

            db.execSQL(query);
        }
        cursor.close();
    }

}
