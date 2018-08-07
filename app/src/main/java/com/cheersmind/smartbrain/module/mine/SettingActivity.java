package com.cheersmind.smartbrain.module.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.QSApplication;
import com.cheersmind.smartbrain.main.activity.BaseActivity;
import com.cheersmind.smartbrain.main.activity.MainActivity;
import com.cheersmind.smartbrain.main.entity.WXUserInfoEntity;
import com.cheersmind.smartbrain.main.util.DataCleanCacheUtils;
import com.cheersmind.smartbrain.main.util.SharedPreferencesUtils;
import com.cheersmind.smartbrain.main.util.SoundPlayUtils;
import com.cheersmind.smartbrain.main.util.VersionUpdateUtil;
import com.cheersmind.smartbrain.module.login.LoginActivity;
import com.cheersmind.smartbrain.module.login.UCManager;
import com.cheersmind.smartbrain.module.login.UserLicenseActivity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/9/26.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private Switch stMusic;
    private TextView tvAbout;
    private TextView tvUpdate;
    private TextView tvCurVersion;
    private TextView tvLicense;
    private RelativeLayout rtCache;
    private TextView tvCache;

    private RelativeLayout rlUpdate;

    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().show();
        setTitle("设置");

        initView();
    }

    private void initView(){
        stMusic = (Switch)findViewById(R.id.st_music);
        stMusic.setChecked(SoundPlayUtils.getSoundStatus());

        tvAbout = (TextView)findViewById(R.id.tv_about);
        tvUpdate = (TextView)findViewById(R.id.tv_update);
        tvCurVersion = (TextView)findViewById(R.id.tv_cur_version);
        tvLicense = (TextView)findViewById(R.id.tv_license);
        btnExit = (Button)findViewById(R.id.btn_exit);

        rlUpdate = (RelativeLayout)findViewById(R.id.rl_update);
        rlUpdate.setOnClickListener(this);

        tvAbout.setOnClickListener(this);
//        tvUpdate.setOnClickListener(this);
        tvLicense.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        stMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SoundPlayUtils.setSoundStatus(isChecked);
            }
        });

        tvCurVersion.setText("当前版本："+ VersionUpdateUtil.getVerName(this));

        rtCache = (RelativeLayout)findViewById(R.id.rt_cache);
        rtCache.setOnClickListener(this);
        tvCache = (TextView)findViewById(R.id.tv_cache);
        try {
            tvCache.setText(DataCleanCacheUtils.getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == tvAbout){
            startActivity(new Intent(SettingActivity.this,AboutActivity.class));
        }else if(v == rlUpdate){
//            if(!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
//                VersionUpdateUtil.checkUpdate(this,true);
//            }
            VersionUpdateUtil.checkUpdate(this,true);
        }else if(v == tvLicense){
            startActivity(new Intent(SettingActivity.this, UserLicenseActivity.class));
        }else if(v == btnExit){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("password_cache");
            editor.commit();
            DataSupport.deleteAll(WXUserInfoEntity.class);
            UCManager.getInstance().clearToken();
            SharedPreferencesUtils.setParam(this, MainActivity.SLIDING_ITEM_SHARE_KEY,0);
            Intent intt = new Intent(SettingActivity.this, LoginActivity.class);
            intt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intt);
            finish();
        }else if(v == rtCache){
            //清除缓存
            new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("确定要清除缓存吗？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            DataCleanCacheUtils.clearAllCache(SettingActivity.this);
                            try {
                                tvCache.setText(DataCleanCacheUtils.getTotalCacheSize(SettingActivity.this));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .create().show();
        }
    }
}
