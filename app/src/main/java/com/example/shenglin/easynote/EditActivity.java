package com.example.shenglin.easynote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.example.shenglin.easynote.UpdateVersion.getNewVersionAndUpdate;

public class EditActivity extends AppCompatActivity {

    String detail = "";
    int menu_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bar);
        //---------------------!!!!!!!!!!!!!!!!!文件名name_edit_file,文件夹name_edit_dir,用户user!!!!!!!!!!!!!------------------------------////////////
        Intent intent = getIntent();
        String name_edit_file = intent.getStringExtra("file_name");
        String name_edit_dir = intent.getStringExtra("table");
        String user = intent.getStringExtra("user");
        String filename = name_edit_dir+"0"+name_edit_file+".txt";
        //----------------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------------------------//////////////////

        TextView show3 = (TextView)findViewById(R.id.detail_text);

        Toolbar toolbar = (Toolbar)findViewById(R.id.edit_toolbar);
        toolbar.setTitle(name_edit_file);
        setSupportActionBar(toolbar);

        //读取

        final FileHelper fileHelper = new FileHelper(getApplicationContext());
        try{
            detail = fileHelper.read(filename,user);
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(EditActivity.this,"wrong!", Toast.LENGTH_SHORT).show();
        }
        show3.setText(detail);
    }
    //////////////////////////////////////

    //初始化菜单/////-------------------------------------------------------------------------------------------------------------------------------//////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

           if (menu_num==0){getMenuInflater().inflate(R.menu.edit_bar_menu, menu);}
           if (menu_num==1){getMenuInflater().inflate(R.menu.edit_bar_menu2,menu);}

        return true;
    }

    //监听toolbar菜单项///////////-----------------------------------------------------------------------------------------------------------------//////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        TextView show3 = (TextView)findViewById(R.id.detail_text);
        EditText detail_ed = (EditText)findViewById(R.id.detail);
        Intent intent = getIntent();
        String name_edit_file = intent.getStringExtra("file_name");
        String name_edit_dir = intent.getStringExtra("table");
        String user = intent.getStringExtra("user");
        String filename = name_edit_dir+"0"+name_edit_file+".txt";

        int id = item.getItemId();

        if (id == R.id.edit1) {

            show3.setVisibility(View.GONE);
            detail_ed.setVisibility(View.VISIBLE);
            detail_ed.setText(detail);

            menu_num=1;
            invalidateOptionsMenu();
            return true;
            }
            if (id==R.id.save1){

                FileHelper fileHelper1 = new FileHelper();
                String text = detail_ed.getText().toString();
                try {
                    fileHelper1.save(filename,text,user);
                    Toast.makeText(EditActivity.this,"yes!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this,"WRONG!", Toast.LENGTH_SHORT).show();
                }

                if (!(detail.equals(text))){
                    String ObjectId = BmobCUD.getObjectId(EditActivity.this, user, name_edit_dir, name_edit_file);
                    Integer newVersion = getNewVersionAndUpdate(EditActivity.this, user, name_edit_dir, name_edit_file);
                    if (name_edit_dir.equals("file")) {
                        BmobCUD.defaultUpdate(ObjectId, newVersion, text);
                    }
                    else {
                        String FileObjectId = BmobCUD.getObjectId(EditActivity.this, user, "directory", name_edit_dir);
                        Integer newFileVersion = UpdateVersion.getNewVersionAndUpdate(EditActivity.this, user, "directory", name_edit_dir);
                        BmobCUD.noteUpdate(ObjectId, newVersion, text);
                        BmobCUD.fileUpdate(FileObjectId, newFileVersion);
                    }
                }

                detail=text;
                show3.setVisibility(View.VISIBLE);
                detail_ed.setVisibility(View.GONE);
                show3.setText(text);

                menu_num=0;
                invalidateOptionsMenu();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}
