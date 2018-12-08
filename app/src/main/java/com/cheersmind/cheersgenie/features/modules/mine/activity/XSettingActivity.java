package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.util.DataCleanCacheUtils;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.VersionUpdateUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserLicenseActivity;
import com.cheersmind.cheersgenie.module.mine.AboutActivity;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置页面
 */
public class XSettingActivity extends BaseActivity {

    @BindView(R.id.st_music)
    Switch stMusic;
    @BindView(R.id.tv_cur_version)
    TextView tvCurVersion;
    @BindView(R.id.tv_cache)
    TextView tvCache;

    @Override
    protected int setContentView() {
        return R.layout.activity_xsetting;
    }

    @Override
    protected String settingTitle() {
        return "设置";
    }

    @Override
    protected void onInitView() {
        //声音切换器初始化
        stMusic.setChecked(SoundPlayUtils.getSoundStatus(XSettingActivity.this));
        //声音切换器切换监听
        stMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SoundPlayUtils.setSoundStatus(XSettingActivity.this, isChecked);
            }
        });

//        ColorStateList colorStateList = ColorStateList.valueOf(ActivityCompat.getColor(XSettingActivity.this, R.color.colorAccent));
        //5.0以下
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            stMusic.setThumbResource(R.color.colorAccent);
        }

        //当前版本号
        tvCurVersion.setText("当前版本：" + VersionUpdateUtil.getVerName(this));

        try {
            tvCache.setText(DataCleanCacheUtils.getTotalCacheSize(XSettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onInitData() {

    }


    @OnClick({R.id.rl_about, R.id.rl_license, R.id.btn_exit, R.id.rl_update, R.id.rl_cache, R.id.rl_account_bind,R.id.rl_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //关于我们
            case R.id.rl_about: {
                startActivity(new Intent(XSettingActivity.this, AboutActivity.class));
                break;
            }
            //用户许可协议
            case R.id.rl_license: {
                startActivity(new Intent(XSettingActivity.this, UserLicenseActivity.class));
                break;
            }
            //检查更新
            case R.id.rl_update: {
                VersionUpdateUtil.checkUpdate(this, true, httpTag);
                break;
            }
            //清除缓存
            case R.id.rl_cache: {
                //弹出清除缓存对话框
                popupClearCacheWindow();
                break;
            }
            //退出
            case R.id.rl_exit:
            case R.id.btn_exit: {
                //弹出退出确认对话框
                popupExitWindows();
                break;
            }
            //账号绑定设置
            case R.id.rl_account_bind: {
                startActivity(new Intent(XSettingActivity.this, AccountBindActivity.class));
                break;
            }
        }
    }


    /**
     * 弹出清除缓存对话框
     */
    private void popupClearCacheWindow() {
        //清除缓存
        AlertDialog dialog = new AlertDialog.Builder(this)
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
                        DataCleanCacheUtils.clearAllCache(XSettingActivity.this);
                        try {
                            tvCache.setText(DataCleanCacheUtils.getTotalCacheSize(XSettingActivity.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
        dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
        dialog.show();
    }

    /**
     * 弹出退出确认对话框
     */
    private void popupExitWindows() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("确定要退出吗？")
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
                        //退出操作
                        doExit();
                    }
                })
                .create();
        dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
        dialog.show();
    }

    /**
     * 退出操作
     */
    private void doExit() {
        //清空用户名、密码
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
        SharedPreferences.Editor editor = pref.edit();
//        editor.remove("user_name");
        editor.remove("user_password");
        editor.apply();
        //删除数据库中的用户对象
        DataSupport.deleteAll(WXUserInfoEntity.class);
        //删除数据库中的孩子对象
        DataSupport.deleteAll(ChildInfoEntity.class);
        //清空登录信息的临时缓存
        UCManager.getInstance().clearToken();
//                SharedPreferencesUtils.setParam(this, MainActivity.SLIDING_ITEM_SHARE_KEY, 0);

        // 用户注销埋点
        MANService manService = MANServiceProvider.getService();
        manService.getMANAnalytics().updateUserAccount("", "");

        //友盟统计：登出
        MobclickAgent.onProfileSignOff();

        //跳转到登录主页面（作为根activity）
        Intent intent = new Intent(XSettingActivity.this, XLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
