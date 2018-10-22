package com.example.shenglin.easynote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import java.util.LinkedList;

/**
 * Created by Lee on 2018/10/19.
 */

public class Search {
    private Context context;
    private LinkedList<MainLine> mainLines;

    public Search(Context context, LinkedList<MainLine> mainLines){
        super();
        this.context = context;
        this.mainLines = mainLines;
    }

    public void search(String search_name, String user, SparseArray<String> dirs, SparseArray<String> files){
        MyDBHelper dbHelper = new MyDBHelper(context,user);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("directory",new String[]{"name"},null,null,null,null,null);
        int i = 0;
        if (cursor.moveToFirst()){
            do {
                String dir = cursor.getString(cursor.getColumnIndex("name"));
                Cursor cursor1 = db.query(dir,new String[]{"name"},"name=?",new String[]{search_name},null,null,null);
                if (cursor1.moveToNext()){
                    mainLines.add(new MainLine(R.drawable.doc_icon,search_name+" / "+dir));
                    dirs.put(i,dir);
                    files.put(i,search_name);
                    i++;
                }
                cursor1.close();
            }while (cursor.moveToNext());
            cursor.close();
        }
        Cursor cursorF = db.query("file",new String[]{"name"},"name = ?",new String[]{search_name},null,null,null);
        int iF = 0;
        if (cursorF.moveToFirst()){
            do {
                mainLines.add(new MainLine(R.drawable.doc_icon,search_name+" / "));
                dirs.put(iF,"file");
                files.put(iF,search_name);
                iF++;
            }while (cursorF.moveToNext());
        cursorF.close();
        }
        db.close();
    }
}
