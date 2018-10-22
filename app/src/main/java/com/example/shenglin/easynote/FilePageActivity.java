package com.example.shenglin.easynote;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.widget.Toast.makeText;
import static com.example.shenglin.easynote.R.id.default_activity_button;
import static com.example.shenglin.easynote.R.id.toolbar;

public class FilePageActivity extends AppCompatActivity {

    private LinkedList<MainLine> fileLines = new LinkedList<>();
    LineAdapter adapter2 = new LineAdapter(FilePageActivity.this, fileLines);

    private SparseBooleanArray checkedMap = new SparseBooleanArray();
    boolean isMultiSelect = false;
    int del_sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_page);

//-----------------------------------------!!!!!!!!!!!!!表table_name,用户user!!!!!!!!!!!!!!-------------------------------------------------//////////////////
        Intent intent = getIntent();
        final String table_name = intent.getStringExtra("table");
        final String user = intent.getStringExtra("user");
        final String dirObjectId = intent.getStringExtra("dirObjectId");
//-------------------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!-------------------------------------------------//////////////////////

//        设标题
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_filepage);
        toolbar.setTitle(table_name);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //列表显示文件名//---------------------------------------------------------------------------------------------------------//////////////////////////
        MyDBHelper dbHelper = new MyDBHelper(FilePageActivity.this,user);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table_name, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String file_name_table = cursor.getString(cursor.getColumnIndex("name"));
                fileLines.add(new MainLine(R.drawable.doc_icon, file_name_table));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        final ListView show2 = (ListView) findViewById(R.id.list_main);
        show2.setAdapter(adapter2);
        //监听悬浮按钮////////////////---------------------------------------------------------------------------------------------------////////////////////
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogview = LayoutInflater.from(FilePageActivity.this).inflate(R.layout.dirname, null);
                final EditText filename1 = dialogview.findViewById(R.id.dir_names);
                AlertDialog.Builder builder = new AlertDialog.Builder(FilePageActivity.this);

                builder.setTitle("请输入文件名称：");
                builder.setCancelable(false);
                builder.setView(dialogview);
                //确定
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String file_name = filename1.getText().toString();
                        //先判定名字重复没//------------------------------------------------------//////////////////////////////////
                        switch (MainActivity.Repetition(file_name, table_name, FilePageActivity.this, user)) {
                            case 1:
                                fileLines.add(new MainLine(R.drawable.doc_icon, file_name));
                                ListView show = (ListView) findViewById(R.id.list_main);
                                LineAdapter adapter1 = new LineAdapter(FilePageActivity.this, fileLines);
                                show.setAdapter(adapter1);
                                //------------------------------同步并加入本地表------------------////////////////
                                MyBmobNote myBmobNote = new MyBmobNote();
                                myBmobNote.setUser(BmobUser.getCurrentUser(MyBmobUser.class));
                                myBmobNote.setName(file_name);
                                myBmobNote.setValue("version", 1);
                                myBmobNote.setValue("fileId", dirObjectId);
                                myBmobNote.setValue("context", file_name);
                                myBmobNote.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            MyDBHelper dbHelper = new MyDBHelper(FilePageActivity.this, user);
                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("name", file_name);
                                            contentValues.put("ver", 1);
                                            contentValues.put("ObjectId", s);
                                            contentValues.put("dir", table_name);
                                            contentValues.put("user", user);
                                            contentValues.put("dirObjectId", dirObjectId);
                                            db.insert(table_name, null, contentValues);
                                        }
                                    }
                                });
                                //---------------------------创建文件-----------------------------//////////////////
                                FileHelper fileHelper1 = new FileHelper();
                                try {
                                    fileHelper1.save(table_name + "0" + file_name + ".txt", file_name, user);//预设标题
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 0:
                                filename1.setText("");
                                makeText(FilePageActivity.this, "该名称已重复，请重新输入！", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //取消
                AlertDialog dir_alert = builder.create();
                dir_alert.show();
            }
        });
        //监听listview项/////////////////-------------------------------------------------------------------------------------------/////////////////////////////

        show2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isMultiSelect){
                    isMultiSelect=true;
                    adapter2.notifyDataSetChanged();
                    show2.setAdapter(adapter2);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setVisibility(View.GONE);
                    LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_layout);
                    bottom.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        show2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isMultiSelect){String file_name = (String)((TextView)view.findViewById(R.id.main_line_text)).getText();
                    Intent intent = new Intent();
                    intent.putExtra("file_name",file_name);
                    intent.putExtra("table",table_name);
                    intent.putExtra("user",user);
                    intent.setClass(FilePageActivity.this,EditActivity.class);
                    startActivity(intent);}
                if (isMultiSelect){
                    if (checkedMap.get(position)){checkedMap.put(position,false);
                    del_sum--;}
                    else {checkedMap.put(position,true);
                    del_sum++;}
                    adapter2.notifyDataSetChanged();
                    show2.setAdapter(adapter2);
                    TextView sum = (TextView)findViewById(R.id.sum);
                    sum.setText("已选"+"("+del_sum+")");
                }
            }
        });
        /////////////-------------  删除、取消、sum  ----------------------------------------------------------------------------------------///////////////////
        Button delete_button = (Button)findViewById(R.id.delete);
        Button cancel_button = (Button)findViewById(R.id.cancel);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(user,table_name);
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }
    //Adapter适配器//////-------------------------------------------------------------------------------------------------------------------------/////////////
    private class LineAdapter extends BaseAdapter {
        private Context context;
        private LinkedList<MainLine> mainLines;

        private LineAdapter(Context context, LinkedList<MainLine> mainLines){
            this.context = context;
            this.mainLines = mainLines;
        }
        @Override
        public int getCount(){
            return mainLines.size();
        }
        @Override
        public Object getItem(int position){
            return position;
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.main_line,parent,false);
                final FilePageActivity.LineAdapter.ViewHolder holder = new FilePageActivity.LineAdapter.ViewHolder();
                holder.img = (ImageView)convertView.findViewById(R.id.main_line_img);
                holder.text = (TextView)convertView.findViewById(R.id.main_line_text);
                holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
                holder.text.setText(mainLines.get(position).getContent());
                holder.img.setImageResource(mainLines.get(position).getImgId());
                if (isMultiSelect){
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.checkBox.setChecked(checkedMap.get(position));
                }
                else {holder.checkBox.setVisibility(View.GONE);}
            }
            return convertView;
        }

        private class ViewHolder{
            ImageView img;
            TextView text;
            CheckBox checkBox;
        }
    }
    ///////////////
    private void delete(final String user, final String table){
        final int max=fileLines.size();
        MyDBHelper dbHelper = new MyDBHelper(FilePageActivity.this,user);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        final ContentValues contentValues = new ContentValues();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(FilePageActivity.this);
//        builder.setIcon();
        builder2.setTitle("提示信息：");
        builder2.setMessage("您确定要删除选中的选项吗？");
        builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int j=0;
                for (int i=0;i<max;i++){
                    if (checkedMap.get(i)){
                        final String del_name = fileLines.get(j).getContent();
                        ////---listview视图---------//////
                        checkedMap.put(i,false);
                        fileLines.remove(j);
                        ////---table------------//////
                        BmobCUD.noteDelete(BmobCUD.getObjectId(FilePageActivity.this, user, table, del_name));
                        db.delete(table,"name=?",new String[]{ del_name });
                        ////---文件------------///////
                        String path = Environment.getExternalStorageDirectory()+"/轻松记/"+user+"/"+table+"0"+del_name+".txt";
                        File file1 = new File(path);
                        file1.delete();
                    }else {j++;}
                }
                db.close();
                cancel();
            }
        });
        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel();
            }
        });
        AlertDialog dir_alert1 = builder2.create();
        dir_alert1.show();
    }
    ///////////////
    @SuppressLint("RestrictedApi")
    private void cancel(){
        isMultiSelect=false;
        adapter2.notifyDataSetChanged();
        ListView lv = (ListView)findViewById(R.id.list_main);
        lv.setAdapter(adapter2);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_layout);
        bottom.setVisibility(View.GONE);
        del_sum=0;
        checkedMap.clear();
    }
    //////////////菜单////------------------------------------------------------------------------------------------------------------------------------/////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }
    ///////监听toolbar/////-----------------------------------------------------------------------------------------------------------------------------//////
    //监听toolbar菜单项///////////-----------------------------------------------------------------------------------------------------------------//////////
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.all_selected) {
            isMultiSelect=true;
            int j = fileLines.size();
            for (int i=0;i<j;i++){
                checkedMap.put(i,true);
            }
            del_sum = j;
            TextView sum = (TextView)findViewById(R.id.sum);
            sum.setText("已选"+"("+del_sum+")");
            adapter2.notifyDataSetChanged();
            ListView show = (ListView)findViewById(R.id.list_main);
            show.setAdapter(adapter2);
            FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
            LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_layout);
            bottom.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
