package com.example.shenglin.easynote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

public class OneKeyLogin extends AppCompatActivity {
    private Button oneKey_send;
    private EditText oneKey_phone;
    private EditText oneKey_code;
    private MyCountTimer oneKey_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_one_key_login);
        oneKey_send = (Button) findViewById(R.id.oneKey_send);
        oneKey_phone = (EditText) findViewById(R.id.oneKey_phone);
        oneKey_code = (EditText) findViewById(R.id.oneKey_verify_code);
        findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("手机号码一键登录");
    }

    public void back(View v) {
        onBackPressed();
    }

    public void oneKey_send(View v) {
        requestSMSCode();
    }
    private void requestSMSCode() {
        String number = oneKey_phone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            oneKey_timer = new MyCountTimer(60000, 1000, oneKey_send);
            oneKey_timer.start();
            BmobSMS.requestSMSCode(number, "EasyNote", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(OneKeyLogin.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        oneKey_timer.cancel();
                    }
                }
            });
        } else {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }
    }

    public void oneKey_login(View v) {
        final String phone = oneKey_phone.getText().toString();
        final String code = oneKey_code.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(OneKeyLogin.this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.signOrLoginByMobilePhone(phone, code, new LogInListener<MyBmobUser>() {
            @Override
            public void done(MyBmobUser myBmobUser, BmobException e) {
                progress.dismiss();
                if (e == null) {
                    Toast.makeText(OneKeyLogin.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OneKeyLogin.this, MainActivity.class);
                    intent.putExtra("from", "oneKeyLogin");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(OneKeyLogin.this,
                            "登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (oneKey_timer != null) {
            oneKey_timer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OneKeyLogin.this, Login.class);
        startActivity(intent);
        finish();
    }
}
