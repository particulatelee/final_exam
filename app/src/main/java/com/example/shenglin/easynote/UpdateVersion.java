package com.example.shenglin.easynote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Lee on 2018/10/22.
 */

public class UpdateVersion {

    public static Integer getNewVersionAndUpdate(Context context, String user, String table, String file_name) {
        MyDBHelper myDBHelper = new MyDBHelper(context, user);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = db.query(table,
                new String[]{ "ver" },
                "name = ?", new String[]{ file_name },
                null, null, null);
        Integer newVersion = 0;
        if (cursor.moveToNext()) {
            do {
                newVersion = Integer.valueOf(cursor.getString(cursor.getColumnIndex("ver"))) + 1;
                contentValues.put("ver", newVersion);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.update(table, contentValues ,"name = ?",new String[]{ file_name });
        db.close();
        return newVersion;
    }
}
