package com.cheersmind.cheersgenie.main.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;


public class BaseActivity extends AppCompatActivity {

    private LinearLayout btnActionbarLeft;
    private TextView tvActionbarTitle;
    private Button btnActionbarRight;
    private TextView brnActionbarRightText;
    private ImageView ivActionBarRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        super.onCreate(savedInstanceState);

        //兼容5.0以上版本，以下代码用于去除actionbar阴影
        if(Build.VERSION.SDK_INT>=21){
            getSupportActionBar().setElevation(0);
        }

        ActionBar actionBar = getSupportActionBar();
        View customActionView = View.inflate(this, R.layout.action_bar_custom,null);
        actionBar.setCustomView(customActionView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        btnActionbarLeft = (LinearLayout) actionBar.getCustomView().findViewById(R.id.btn_actionbar_left);
        btnActionbarRight = (Button) actionBar.getCustomView().findViewById(R.id.btn_actionbar_right);
        tvActionbarTitle  = (TextView) actionBar.getCustomView().findViewById(R.id.tv_actionbar_title);
        brnActionbarRightText = (TextView) actionBar.getCustomView().findViewById(R.id.btn_actionbar_right_text);
        ivActionBarRight = (ImageView) actionBar.getCustomView().findViewById(R.id.iv_action_bar_right);

        btnActionbarLeft.setOnClickListener(new View.OnClickListener() {//监听事件
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        getSupportActionBar().hide();

    }


    protected void setTitle(String title) {
        tvActionbarTitle.setText(title);
    }

    protected LinearLayout getButtonLeft () {
        return btnActionbarLeft;
    }
    protected Button getButtonRight () {
        return btnActionbarRight;
    }

    protected ImageView getIvActionBarRight() {
        return ivActionBarRight;
    }

    public void setIvActionBarRight(ImageView ivActionBarRight) {
        this.ivActionBarRight = ivActionBarRight;
    }

    protected TextView getButtonRightText () {
        return brnActionbarRightText;
    }

    protected void setActionBarBg(Drawable drawable){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(drawable);
    }

    public static void setTransparentStatusBar(Activity activity,int colorStatus) {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
//            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(colorStatus);
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
