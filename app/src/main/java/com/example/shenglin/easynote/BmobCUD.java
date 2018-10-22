package com.example.shenglin.easynote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by shenglin on 2017/11/14.
 */

public class BmobCUD {

    /*
    *查询table_name表中名字为name的数据的ObjectId
     */
    public static String getObjectId(Context context, String user, String table_name, String name) {
        MyDBHelper dbHelperBmob = new MyDBHelper(context,user);
        SQLiteDatabase dbBmob = dbHelperBmob.getReadableDatabase();
        Cursor cursorBmob = dbBmob.query(table_name,
                new String[] { "ObjectId" },
                "name = ?", new String[] { name },
                null,null,null);
        String ObjectId = null;
        if (cursorBmob.moveToFirst()){
            do {
                ObjectId = cursorBmob.getString(cursorBmob.getColumnIndex("ObjectId"));
            }while (cursorBmob.moveToNext());
            cursorBmob.close();
        }dbBmob.close();
        return ObjectId;
    }

    public static void fileUpdate(String ObjectId, Integer newVersion) {
        MyBmobFile myBmobFile = new MyBmobFile();
        myBmobFile.setValue("version", newVersion);
        myBmobFile.update(ObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    public static void defaultUpdate(String ObjectId, Integer newVersion, String text) {
        MyBmobDefault myBmobDefault = new MyBmobDefault();
        myBmobDefault.setValue("version", newVersion);
        myBmobDefault.setValue("context", text);
        myBmobDefault.update(ObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    public static void noteUpdate(String ObjectId, Integer newVersion, String text) {
        MyBmobNote myBmobNote = new MyBmobNote();
        myBmobNote.setValue("context", text);
        myBmobNote.setValue("version", newVersion);
        myBmobNote.update(ObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    public static void fileDelete(String ObjectId) {
        MyBmobFile myBmobFile = new MyBmobFile();
        myBmobFile.delete(ObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    public static void defaultDelete(String ObjectId) {
        MyBmobDefault myBmobDefault = new MyBmobDefault();
        myBmobDefault.delete(ObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    public static void noteDelete(String ObjectId) {
        MyBmobNote myBmobNote = new MyBmobNote();
        myBmobNote.delete(ObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }
}
