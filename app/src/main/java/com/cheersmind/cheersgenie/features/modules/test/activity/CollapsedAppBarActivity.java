package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测试：可滚动的AppBar页面
 */
public class CollapsedAppBarActivity extends BaseActivity {


    @Override
    protected int setContentView() {
        return R.layout.activity_collapsed_app_bar;
    }

    @Override
    protected String settingTitle() {
        return null;
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {

    }
}
