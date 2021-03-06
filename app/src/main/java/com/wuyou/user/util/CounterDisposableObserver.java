package com.wuyou.user.util;

import android.widget.Button;
import android.widget.TextView;

import com.wuyou.user.R;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by hjn on 2018/2/2.
 * 发送验证码
 */

public class CounterDisposableObserver extends DisposableObserver<Integer> {
    private TextView button;

    public CounterDisposableObserver(Button button) {
        this.button = button;
    }

    @Override
    protected void onStart() {
        button.setEnabled(false);
    }


    @Override
    public void onNext(Integer value) {
        button.setText(value + "秒后重发");
        button.setTextColor(button.getResources().getColor(R.color.common_gray));
        button.setBackgroundResource(R.drawable.gray_border);
    }

    @Override
    public void onError(Throwable e) {
        button.setEnabled(true);
        button.setText("发送验证码");
        button.setTextColor(button.getResources().getColor(R.color.night_blue));
        button.setBackgroundResource(R.drawable.night_blue_border);
        dispose();
    }

    @Override
    public void onComplete() {
        button.setEnabled(true);
        button.setText("发送验证码");
        button.setTextColor(button.getResources().getColor(R.color.night_blue));
        button.setBackgroundResource(R.drawable.night_blue_border);
        dispose();
    }
}
