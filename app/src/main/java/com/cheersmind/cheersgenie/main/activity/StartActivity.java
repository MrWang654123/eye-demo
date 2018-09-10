package com.cheersmind.cheersgenie.main.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.module.login.LoginActivity;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 闪屏的核心代码
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class cls = null;
                //todo 判断是否是第一次启动，是否可以自动登录
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
                int featureVersion = pref.getInt("feature_version",0);
                boolean showNewFeature = featureVersion < Constant.VERSION_FEATURE;
                boolean conAutoLogin = false;
                if (showNewFeature) {
                    cls = NewFeatureActivity.class;
                } else if (conAutoLogin) {
                    cls = MainActivity.class;
                } else {
                    cls = LoginActivity.class;
                }

                Intent intent = new Intent(StartActivity.this,
                        cls); // 从启动动画ui跳转到主ui
                startActivity(intent);

                StartActivity.this.finish(); // 结束启动动画界面
            }
        }, 2000); // 启动动画持续3秒钟

    }
}
