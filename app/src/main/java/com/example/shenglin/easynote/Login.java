package com.example.shenglin.easynote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "99015b9beadaaf95404e7a300e9c3882");
    }

    public void login_login(View v) {
        String account = ((EditText) findViewById(R.id.login_account)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(Login.this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(Login.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.loginByAccount(account, password, new LogInListener<MyBmobUser>() {
            @Override
            public void done(MyBmobUser myBmobUser, BmobException e) {
                progress.dismiss();
                if (e == null) {
                    Toast.makeText(Login.this, "登录成功---用户名："+myBmobUser.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Login.this,
                            "登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void login_oneKey(View v) {
        Intent intent = new Intent(Login.this, OneKeyLogin.class);
        startActivity(intent);
        finish();
    }

    public void login_register(View v) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();
    }
}
