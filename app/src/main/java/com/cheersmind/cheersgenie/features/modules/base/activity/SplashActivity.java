package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.SystemTimeEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DateTimeUtils;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.litepal.crud.DataSupport;

import java.util.Map;

/**
 * 启动页面
 */
public class SplashActivity extends BaseActivity {
    //等待时间
    private static final long WAIT_TIME = 2000;

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
        //非根目录则关闭退出
//        if (!isTaskRoot()) {
//            finish();
//            return;
//        }
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
        //判断是否是第一次启动，是否可以自动登录
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        int featureVersion = pref.getInt("feature_version",0);
        boolean showNewFeature = featureVersion < Constant.VERSION_FEATURE;
        boolean conAutoLogin = true;
        //是否显示信功能
        if (showNewFeature) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //引导页面
                    gotoActivity(GuideActivity.class);
                }
            }, WAIT_TIME);

        } else if (conAutoLogin) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //处理自动登录
                    doAutoLogin();
                }
            }, 1000);

        } else {
            //登录主页面
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //登录页面
                    gotoActivity(XLoginActivity.class);
                }
            }, WAIT_TIME);
        }

    }


    /**
     * 自动登录处理
     */
    private void doAutoLogin() {
        //获取服务端时间戳
        DataRequestService.getInstance().getServerTime(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);

                //跳转到登录主页面
                gotoActivity(XLoginActivity.class);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map map = JsonUtil.fromJson(obj.toString(),Map.class);
                    SystemTimeEntity systemTimeEntity = InjectionWrapperUtil.injectMap(map,SystemTimeEntity.class);

                    if (systemTimeEntity == null || TextUtils.isEmpty(systemTimeEntity.getDatetime())) {
                        throw new QSCustomException("获取服务端时间戳失败");
                    }

                    WXUserInfoEntity wxUserInfoEntity  = DataSupport.findFirst(WXUserInfoEntity.class);
                    if (wxUserInfoEntity == null) {
                        throw new QSCustomException("本地无用户信息");
                    }

                    //判断token是否有效
                    if(DateTimeUtils.tokenTimeValid(wxUserInfoEntity.getServerTime(),systemTimeEntity.getDatetime())){
                        //缓存token有效,自动登录
                        UCManager.getInstance().settingUserInfo(wxUserInfoEntity);
                        //判断是否有孩子
                        ChildInfoEntity defaultChild = ChildInfoDao.getDefaultChildFromDataBase();
                        if (defaultChild == null) {
                            //无孩子
                            throw new QSCustomException("无孩子对象");

                        } else {
                            //设置默认孩子到缓存中
                            UCManager.getInstance().setDefaultChild(defaultChild);
                            //跳转到主页面
                            gotoMainPage(SplashActivity.this);
                        }

                    } else {
                        //无效情况
                        throw new QSCustomException("token已失效");
                    }

                } catch (QSCustomException e) {
                    //跳转到登录主页面
                    gotoActivity(XLoginActivity.class);

                } catch (Exception e) {
                    e.printStackTrace();
                    //跳转到登录主页面
                    gotoActivity(XLoginActivity.class);
                }

            }
        }, httpTag, SplashActivity.this);
    }

    /**
     * 跳转页面
     * @param toActivity
     */
    private void gotoActivity(Class toActivity) {
        //跳转到登录页面
        Intent intent = new Intent(SplashActivity.this, toActivity);
        startActivity(intent);
        SplashActivity.this.finish(); // 结束启动动画界面
    }

}
