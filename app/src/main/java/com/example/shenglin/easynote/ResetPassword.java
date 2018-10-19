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

public class ResetPassword extends AppCompatActivity {
    private MyCountTimer reset_timer;
    private Button reset_send;
    private EditText reset_phone;
    private EditText reset_code;
    private EditText reset_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("重置密码");

        reset_send = (Button) findViewById(R.id.reset_send);
        reset_phone = (EditText) findViewById(R.id.reset_phone);
        reset_code = (EditText) findViewById(R.id.reset_verify_code);
        reset_password = (EditText) findViewById(R.id.reset_password);
    }

    public void back(View v) {
        finish();
    }

    public void reset_send(View v) {
        requestSMSCode();
    }
    private void requestSMSCode() {
        String number = reset_phone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            reset_timer = new MyCountTimer(60000, 1000, reset_send);
            reset_timer.start();
            BmobSMS.requestSMSCode(number, "EasyNote", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(ResetPassword.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        reset_timer.cancel();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }
    }

    public void reset_resetPassword(View v) {
        final String code = reset_code.getText().toString();
        final String pwd = reset_password.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(ResetPassword.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(ResetPassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(ResetPassword.this);
        progress.setMessage("正在重置密码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        Toast.makeText(ResetPassword.this, "", Toast.LENGTH_SHORT).show();
        Toast.makeText(ResetPassword.this, "", Toast.LENGTH_SHORT).show();
        BmobUser.resetPasswordBySMSCode(code, pwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                progress.dismiss();
                if (e == null) {
                    Toast.makeText(ResetPassword.this, "密码重置成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(ResetPassword.this,
                            "密码重置失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reset_timer != null) {
            reset_timer.cancel();
        }
    }
}

