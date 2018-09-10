package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.main.constant.Constant;

/**
 * 启动页面
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected int setContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected String settingTitle() {
        return null;
    }

    @Override
    protected void onInitView() {
        //隐藏工具栏
//        getSupportActionBar().hide();
        //全屏，隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onInitData() {
        doInit();
    }

    /**
     * 初始化操作
     */
    private void doInit() {
        // 闪屏的核心代码
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Class cls = null;
                //判断是否是第一次启动，是否可以自动登录
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                int featureVersion = pref.getInt("feature_version",0);
                boolean showNewFeature = featureVersion < Constant.VERSION_FEATURE;
                boolean conAutoLogin = false;
                if (showNewFeature) {
                    //引导页面
                    cls = GuideActivity.class;
                } else if (conAutoLogin) {
                    //主页面
//                    cls = MainActivity.class;
                    cls = MasterTabActivity.class;
                } else {
                    //登陆页面
                    cls = XLoginActivity.class;
                }

                // 从启动动画ui跳转到主ui
                Intent intent = new Intent(SplashActivity.this, cls);
                startActivity(intent);

                SplashActivity.this.finish(); // 结束启动动画界面
            }
        }, 2000); // 启动动画持续3秒钟
    }

}
