package com.example.shenglin.easynote;

import android.app.ProgressDialog;
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
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class Bind extends AppCompatActivity {
    private MyCountTimer bind_timer;
    private EditText bind_number;
    private EditText bind_input;
    private Button bind_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bind);
        bind_number = (EditText) findViewById(R.id.bind_number);
        bind_input = (EditText) findViewById(R.id.bind_input);
        bind_send = (Button) findViewById(R.id.bind_send);
        findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("绑定手机号");
    }

    public void back(View v) {
        finish();
    }

    public void bind_send(View v) {
        requestSMSCode();
    }
    private void requestSMSCode() {
        String number = bind_number.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            bind_timer = new MyCountTimer(60000, 1000, bind_send);
            bind_timer.start();
            BmobSMS.requestSMSCode(number, "Bind", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(Bind.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        bind_timer.cancel();
                    }
                }
            });
        } else {
            Toast.makeText(Bind.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }
    }

    public void bind_bind(View v) {
        final String phone = bind_number.getText().toString();
        String code = bind_input.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(Bind.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(Bind.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(Bind.this, "手机号码已验证", Toast.LENGTH_SHORT).show();
                    bindMobilePhone(phone);
                }
                else {
                    Toast.makeText(Bind.this,
                            "验证失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindMobilePhone(String phone){
        MyBmobUser user =new MyBmobUser();
        user.setMobilePhoneNumber(phone);
        user.setMobilePhoneNumberVerified(true);
        MyBmobUser cur = BmobUser.getCurrentUser(MyBmobUser.class);
        user.update(cur.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(Bind.this, "手机号码绑定成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(Bind.this,
                            "手机号码绑定失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

