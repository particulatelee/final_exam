package com.example.shenglin.easynote;

import android.os.CountDownTimer;
import android.widget.Button;


public class MyCountTimer extends CountDownTimer {
    Button send;

    public MyCountTimer(long millisInFuture, long countDownInterval, Button send) {
        super(millisInFuture, countDownInterval);
        this.send = send;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        send.setText((millisUntilFinished / 1000) +"秒后重发");
        send.setEnabled(false);
        send.setBackgroundResource(R.drawable.bg_btn_press_selector_hui);
    }

    @Override
    public void onFinish() {
        send.setText("重新发送验证码");
        send.setEnabled(true);
        send.setBackgroundResource(R.drawable.bg_btn_press_selector_1);
    }
}
