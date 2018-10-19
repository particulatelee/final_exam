package com.example.shenglin.easynote;

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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    LinkedList<MainLine> mainLines = new LinkedList<>();
    final LineAdapter adapter1 = new LineAdapter(MainActivity.this,mainLines);

    boolean isMultiSelect = false;
    private SparseBooleanArray checkedMap = new SparseBooleanArray();
    int boundary = 0;
    int del_sum = 0;
    int search_tag = 0;

    private MyDBHelper dbHelper;
    private final String user = BmobUser.getCurrentUser(MyBmobUser.class).getUsername();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//--------------------!!!!!!!!!------------用户名user--------------!!!!!!!!!!!!---------------------------------//////////////////////////////////////////
//        user = BmobUser.getCurrentUser(MyBmobUser.class).getUsername();
//-------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!---------------------------------//////////////////////////////////////////

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_layout);
        final TextView sum = (TextView)findViewById(R.id.sum);

        setTitle(user);

/////----开局先创建存放文件的文件夹，据说安卓6.0之后有变动，需要动态添加权限-----------------------------------------------------------------/////////////////
        final File file = new File(getExternalStorageDirectory()+"/轻松记/"+user);
        if (!file.exists()) {
            file.mkdirs();
        }

////一开始，检索db，拉出listview/////////////---------------------------------------------------------------------------------------/////////////////////////
        dbHelper = new MyDBHelper(MainActivity.this,user);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("directory",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String dir_name_table = cursor.getString(cursor.getColumnIndex("name"));
                mainLines.add(new MainLine(R.drawable.folder_icon,dir_name_table));
                boundary++;
            }while (cursor.moveToNext());
            cursor.close();
        }db.close();

        SQLiteDatabase db1 = dbHelper.getReadableDatabase();
        Cursor cursor1 = db1.query("file",null,null,null,null,null,null);
        if (cursor1.moveToFirst()){
            do {
                String file_name_table = cursor1.getString(cursor1.getColumnIndex("name"));
                mainLines.add(new MainLine(R.drawable.doc_icon,file_name_table));
            }while(cursor1.moveToNext());
            cursor1.close();
        }db1.close();

        final ListView show = (ListView)findViewById(R.id.list_main);
        show.setAdapter(adapter1);

        toolbar.setTitle(user);
        setSupportActionBar(toolbar);

//监听listview项/////////////////-------------------------------------------------------------------------------------------/////////////////////////////
        show.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isMultiSelect){
                    isMultiSelect=true;
                    checkedMap.put(position,true);
                    del_sum++;
                    sum.setText("已选"+"("+del_sum+")");
                    adapter1.notifyDataSetChanged();
                    show.setAdapter(adapter1);
                    fab.setVisibility(View.GONE);
                    bottom.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isMultiSelect) {
                    if (checkedMap.get(position)){checkedMap.put(position,false);
                        del_sum--;}
                    else {checkedMap.put(position,true);
                        del_sum++;}
                    adapter1.notifyDataSetChanged();
                    show.setAdapter(adapter1);
                    sum.setText("已选"+"("+del_sum+")");
                }
                else if (position<boundary){
                    String table_name = (String)((TextView)view.findViewById(R.id.main_line_text)).getText();

                    MyDBHelper dbHelper = new MyDBHelper(MainActivity.this,user);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.query("directory",
                            new String[] { "ObjectId" },
                            "name = ?", new String[] { table_name },
                            null,null,null);
                    String ObjectId = null;
                    if (cursor.moveToFirst()){
                        do {
                            ObjectId = cursor.getString(cursor.getColumnIndex("ObjectId"));
                        }while (cursor.moveToNext());
                        cursor.close();
                    }db.close();

                    Intent intent = new Intent();
                    intent.putExtra("table",table_name);
                    intent.putExtra("user",user);
                    intent.putExtra("dirObjectId", ObjectId);
                    intent.setClass(MainActivity.this,FilePageActivity.class);
                    startActivity(intent);
                }
                else {String file_name = (String)((TextView)view.findViewById(R.id.main_line_text)).getText();
                    Intent intent = new Intent();
                    intent.putExtra("file_name",file_name);
                    intent.putExtra("table","file");
                    intent.putExtra("user",user);
                    intent.setClass(MainActivity.this,EditActivity.class);
                    startActivity(intent);
                }
            }
        });
//-----------------------删除、取消-----------------------------------------------------------------------------------------//////////////////////////
        Button delete_button = (Button)findViewById(R.id.delete);
        Button cancel_button = (Button)findViewById(R.id.cancel);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(user);
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

//监听悬浮按钮/////////////////-------------------------------------------------------------------------------------------------/////////////////////////
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogview = LayoutInflater.from(MainActivity.this).inflate(R.layout.dirname,null);
                final EditText filename1 = dialogview.findViewById(R.id.dir_names);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("请输入笔记名称：");
                builder.setCancelable(false);
                builder.setView(dialogview);
                //确定
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String file_name = filename1.getText().toString();
                        //先判定是否重名//--------------------------------------------------------////////////////
                        switch (Repetition(file_name, "file", MainActivity.this, user)) {
                            case 1:
                                mainLines.add(new MainLine(R.drawable.doc_icon, file_name));
                                ListView show = (ListView) findViewById(R.id.list_main);
                                LineAdapter adapter1 = new LineAdapter(MainActivity.this, mainLines);
                                show.setAdapter(adapter1);
                                //------------------------------同步并加入本地表----------------------------////////////////
                                MyBmobDefault myBmobDefault = new MyBmobDefault();
                                myBmobDefault.setUser(BmobUser.getCurrentUser(MyBmobUser.class));
                                myBmobDefault.setName(file_name);
                                myBmobDefault.setValue("version", 1);
                                myBmobDefault.setValue("context", file_name);
                                myBmobDefault.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            MyDBHelper dbHelper = new MyDBHelper(MainActivity.this, user);
                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("name", file_name);
                                            contentValues.put("ver", 1);
                                            contentValues.put("ObjectId", s);
                                            contentValues.put("user", user);
                                            db.insert("file", null, contentValues);
                                            db.close();
                                        }
                                    }
                                });
                                //---------------------------创建文件-----------------------------//////////////////
                                FileHelper fileHelper1 = new FileHelper();
                                try {
                                    fileHelper1.save("file" + "0" + file_name + ".txt", file_name, user);//预设标题
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String path = Environment.getExternalStorageDirectory()+"/轻松记/"+user+"/"+"file" + "0" + file_name + ".txt";
                                File fileTxt = new File(path);
                                BmobFile bmobFile = new BmobFile(fileTxt);
                                bmobFile.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                                break;
                            case 0:
                                filename1.setText("");
                                Toast.makeText(MainActivity.this, "该名称已重复，请重新输入！", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
                //取消
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dir_alert = builder.create();
                dir_alert.show();
            }
        });

//抽屉/////---------------------------------------------------------------------------------------------------------------------------------///////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Adapter适配器//////-------------------------------------------------------------------------------------------------------------------------/////////////
    private class LineAdapter extends BaseAdapter {
        private Context context;
        private LinkedList<MainLine> mainLines;
        ViewHolder holder;

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
                holder = new ViewHolder();
                holder.img = convertView.findViewById(R.id.main_line_img);
                holder.text = convertView.findViewById(R.id.main_line_text);
                holder.checkBox = convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(mainLines.get(position).getContent());
            holder.img.setImageResource(mainLines.get(position).getImgId());
            if (isMultiSelect){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(checkedMap.get(position));
            }
            else {holder.checkBox.setVisibility(View.GONE);}
            return convertView;
        }

        private class ViewHolder{
            ImageView img;
            TextView text;
            CheckBox checkBox;
        }
    }

    //查重////-----------------------------------------------------------------------------------------------------------------------------------////////////
    public static int Repetition(String name,String table_name,Context context,String user){
        MyDBHelper dbHelper = new MyDBHelper(context,user);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table_name,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                if (name.equals(cursor.getString(cursor.getColumnIndex("name")))){return 0;}
            }while (cursor.moveToNext());
            cursor.close();
        }db.close();
        return 1;
    }

    //左上角按钮////--------------------------------------------------------------------------------------------------------------------------////////////
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //初始化菜单/////-------------------------------------------------------------------------------------------------------------------------------//////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.fortoolbar,menu);
        return true;
    }
    //监听toolbar菜单项///////////-----------------------------------------------------------------------------------------------------------------//////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.OCR) {
            Intent intent = new Intent(MainActivity.this, OCR.class);
            startActivity(intent);
        }
        else if (id == R.id.toolbar_add) {
            /////////////////////-------分类-----------弹出提示输入分类名称的对话框/////////////////////////////////////////////////////////
            View dialogview = LayoutInflater.from(MainActivity.this).inflate(R.layout.dirname,null);
            final EditText dirname1 = dialogview.findViewById(R.id.dir_names);
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("新建文件夹");
            builder.setCancelable(false);
            builder.setView(dialogview);
            //确定
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String dir_name = dirname1.getText().toString();
                    switch (Repetition(dir_name,"directory",MainActivity.this,user)) {
                        case 1:
                            //----------------显示在listview---------(这里有一个疑问，为什么在一开始加载页面的代码后面加实时更新函数会使listview部分顺序发生错乱呢？)////
                            mainLines.add(boundary,new MainLine(R.drawable.folder_icon, dir_name));
                            ListView show = (ListView) findViewById(R.id.list_main);
                            LineAdapter adapter1 = new LineAdapter(MainActivity.this, mainLines);
                            show.setAdapter(adapter1);
                            //------------------------------同步并加入本地表----------------------------////////////////
                            MyBmobFile myBmobFile = new MyBmobFile();
                            myBmobFile.setUser(BmobUser.getCurrentUser(MyBmobUser.class));
                            myBmobFile.setName(dir_name);
                            myBmobFile.setValue("version", 1);
                            myBmobFile.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        MyDBHelper dbHelper = new MyDBHelper(MainActivity.this,user);
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("name", dir_name);
                                        contentValues.put("user",user);
                                        contentValues.put("ObjectId", s);
                                        contentValues.put("ver", 1);
                                        db.insert("directory", null, contentValues);
                                        db.execSQL("CREATE TABLE "+String.valueOf(dir_name)+"(file_id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),ver INTEGER,dir VARCHAR(20),user VARCHAR(20),ObjectId VARCHAR(20),dirObjectId VARCHAR(20))");
                                        db.close();
                                    }
                                    else {
                                        Log.d("EasyNoteBook",
                                                "失败：code=" + e.getErrorCode() + "，描述：" + e.getLocalizedMessage());
                                    }
                                }
                            });
                            //-----------------------创建文件   表-------------------------------///////////////////
                            boundary++;
                            break;
                        case 0:
                            dirname1.setText("");
                            Toast.makeText(MainActivity.this,"该名称已重复，请重新输入！",Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                }
            });
            //取消
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dir_alert = builder.create();
            dir_alert.show();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }
        else if (id == R.id.all_selected) {
            isMultiSelect=true;
            int j = mainLines.size();
            for (int i=0;i<j;i++){
                checkedMap.put(i,true);
            }
            del_sum = j;
            TextView sum = (TextView)findViewById(R.id.sum);
            sum.setText("已选"+"("+del_sum+")");
            adapter1.notifyDataSetChanged();
            ListView show = (ListView)findViewById(R.id.list_main);
            show.setAdapter(adapter1);
            FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
            LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_layout);
            bottom.setVisibility(View.VISIBLE);
            return true;
        }

        else if (id == R.id.toolbar_search){
            EditText search_edit = (EditText)findViewById(R.id.search_edittext);
            if (search_tag == 1){
                search_tag = 0;
                String search_name = search_edit.getText().toString();
                search_edit.setText("");
                search_edit.setVisibility(View.GONE);
                Intent intent = new Intent();
                intent.putExtra("name",search_name);
                intent.putExtra("user",user);
                intent.setClass(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
            else {
                search_edit.setVisibility(View.VISIBLE);
                search_tag = 1;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    //监听侧拉栏菜单项////-------------------------------------------------------------------------------------------------------------------------/////////////
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.update) {

        }
        else if (id == R.id.add) {
            Intent intent = new Intent(MainActivity.this, Bind.class);
            startActivity(intent);
        }
        else if (id == R.id.setting) {
        }
        else if (id == R.id.about) {
            Toast.makeText(MainActivity.this,"we are bosses.",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //delete()  and   cancel()  ////-----------------------------------------------------------------------------------------------------------------////////
    private void delete(final String user){
        final int max=mainLines.size();
        MyDBHelper dbHelper = new MyDBHelper(MainActivity.this,user);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
        builder2.setTitle("提示信息：");
        builder2.setMessage("您确定要删除选中的选项吗？");
        builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int j=0;
                for (int i=0;i<max;i++){
                    if (checkedMap.get(i)){
                        if (i<boundary){
                            boundary--;
                            String del_name = mainLines.get(j).getContent();
                            ////-----表---文件-----//////
                            BmobCUD.fileDelete(BmobCUD.getObjectId(MainActivity.this, user, "directory", del_name));
                            Cursor cursor = db.query(del_name,null,null,null,null,null,null);
                            if (cursor.moveToFirst()){
                                do {
                                    String file_name_del = cursor.getString(cursor.getColumnIndex("name"));
                                    String file_objectId = cursor.getString(cursor.getColumnIndex("ObjectId"));
                                    BmobCUD.noteDelete(file_objectId);
                                    String path = getExternalStorageDirectory()+"/轻松记/"+user+"/"+del_name+"0"+file_name_del+".txt";
                                    File file1 = new File(path);
                                    file1.delete();
                                }while (cursor.moveToNext());
                                cursor.close();
                            }
                            db.delete("directory","name=?",new String[]{del_name});

                            db.execSQL("DROP TABLE " + del_name);
                            ///----listview------///////
                            checkedMap.put(i,false);
                            mainLines.remove(j);
                        }
                        else {
                            String del_name = mainLines.get(j).getContent();
                            ////-----表----------///////
                            BmobCUD.defaultDelete(BmobCUD.getObjectId(MainActivity.this, user, "file", del_name));
                            db.delete("file","name=?",new String[]{del_name});
                            ////-----文件--------///////
                            String path = getExternalStorageDirectory()+"/轻松记/"+user+"/"+"0"+del_name+".txt";
                            File file1 = new File(path);
                            file1.delete();
                            ////-----listview------//////
                            checkedMap.put(i,false);
                            mainLines.remove(j);
                        }
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
    private void cancel(){
        isMultiSelect=false;
        adapter1.notifyDataSetChanged();
        ListView lv = (ListView)findViewById(R.id.list_main);
        lv.setAdapter(adapter1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_layout);
        bottom.setVisibility(View.GONE);
        del_sum=0;
        checkedMap.clear();
    }

    public void backToLogin(View v) {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
