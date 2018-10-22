package com.example.shenglin.easynote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lee on 2018/10/21.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context, String user){
        super(context,user+".db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE directory(dirId INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),ver INTEGER,user VARCHAR(20),ObjectId VARCHAR(20))");
        db.execSQL("CREATE TABLE file(fileId INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),ver INTEGER,user VARCHAR(20),ObjectId VARCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
//    如果开发用户转移或者别的什么功能，或许可以用到，mark一下
    }
}
