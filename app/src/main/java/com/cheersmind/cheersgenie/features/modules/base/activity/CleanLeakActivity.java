package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * 防止InputMethodManager内存泄漏
 */
public class CleanLeakActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },500);//500毫秒后结束
    }

}
