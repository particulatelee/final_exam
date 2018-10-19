package com.example.shenglin.easynote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.example.shenglin.easynote.R.id.register_backToLogin;

public class Register extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("注册");
    }

    public void back(View v) {
        onBackPressed();
    }

    public void register_register(View v) {
        String account = ((EditText) findViewById(R.id.register_account)).getText().toString();
        String password = ((EditText) findViewById(R.id.register_password)).getText().toString();
        String pwd = ((EditText) findViewById(R.id.register_pwd_again)).getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(Register.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Register.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(pwd)) {
            Toast.makeText(Register.this, "两次密码不一样", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(Register.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final MyBmobUser user = new MyBmobUser();
        user.setUsername(account);
        user.setPassword(password);
        user.signUp(new SaveListener<MyBmobUser>() {
            @Override
            public void done(MyBmobUser myBmobUser, BmobException e) {
                if (e == null) {
                    progress.dismiss();
                    Toast.makeText(Register.this, "注册成功---用户名：" + user.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Register.this,
                            "登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register_backToLogin(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }
}