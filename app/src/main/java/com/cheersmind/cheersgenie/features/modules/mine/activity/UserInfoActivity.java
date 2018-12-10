package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;

import java.lang.reflect.Field;

/**
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity {

    @Override
    protected int setContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String settingTitle() {
        return "我的资料";
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {

    }


}
