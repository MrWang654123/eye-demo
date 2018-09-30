package com.cheersmind.cheersgenie.main.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.entity.VersionEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.main.view.qsdialog.AppHintUpdateDialog;
import com.cheersmind.cheersgenie.main.view.qsdialog.AppUpdateDialog;

import java.util.Map;

/**
 * Created by goodm on 2017/2/16.
 */
public class VersionUpdateUtil {

    public static boolean isCurrVersionUpdateDialogShow;
    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查更新
     */
    public static void checkUpdate(final Activity context,final boolean fromUser) {
        checkUpdate(context, fromUser, true);
    }


    /**
     * 检查更新
     */
    public static void checkUpdate(final Activity context,final boolean fromUser,final boolean showLoading) {
        String url = HttpConfig.URL_VERSION_UPDATE.replace("{app_id}", Constant.API_APP_ID);
        if (showLoading) {
            LoadingView.getInstance().show(context);
        }
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                if (fromUser) {
                    Toast.makeText(context, "获取版本信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponse(Object obj) {
                if (showLoading) {
                    LoadingView.getInstance().dismiss();
                }
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    final VersionEntity versionObj = InjectionWrapperUtil.injectMap(dataMap,VersionEntity.class);
                    if(versionObj!=null){
                        int forceUpdate = versionObj.getForceUpdate();
                        String updateDesc = versionObj.getDescription();
                        int versionCode = Integer.valueOf(versionObj.getVersionCode());
                        boolean isDebug =false; //versionObj.getBoolean("is_debug");
                        final String apkDownloadUrl = versionObj.getUpdateUrl();

                        boolean isApkInDebug = VersionUpdateUtil.isApkInDebug(QSApplication.getContext());
                        if (isDebug == isApkInDebug || isApkInDebug) {
                            PackageManager manager = context.getPackageManager();
                            try {
                                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                                int currVersion = PackageUtils.getPackageVersion(context,info.packageName);
                                System.out.println("versionUpdate online versionCode : "+versionCode+" , isDebug : "+(isDebug)+" , apkDownloadUrl : "+apkDownloadUrl +" | localVersionCode : "+currVersion);
                                if (versionCode <= currVersion) {
                                    if (fromUser) {
                                        Toast.makeText(context,"当前已经是最新版本",Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    AppHintUpdateDialog appHintUpdateDialog = new AppHintUpdateDialog(context, versionObj, new AppHintUpdateDialog.AppHintUpdateCallback() {
                                        @Override
                                        public void onCancel() {
                                            //非强制更新，显示取消，点击取消后本次不再显示提示更新
                                            isCurrVersionUpdateDialogShow = true;
                                        }

                                        @Override
                                        public void onSure() {

                                            isCurrVersionUpdateDialogShow = true;
                                            final AppUpdateDialog appUpdateDialog = new AppUpdateDialog(context,versionObj.getForceUpdate());
                                            appUpdateDialog.show();
                                            final DownLoadApk downLoadApk = new DownLoadApk(apkDownloadUrl);
                                            downLoadApk.downloadApk(new FileDownloaderUtils.DownloadCallBack() {
                                                @Override
                                                public void onStart() {

                                                }

                                                @Override
                                                public void onFinish() {
                                                    isCurrVersionUpdateDialogShow = false;
                                                    if(appUpdateDialog!=null){
                                                        appUpdateDialog.dismiss();
                                                    }
                                                    downLoadApk.installApk(context);
                                                }

                                                @Override
                                                public void onProgress(int done, int total) {
                                                    LogUtils.w("downloadApk : onProgress done :",FileUtil.formetFileSize(done) + "/" + FileUtil.formetFileSize(total));
                                                    appUpdateDialog.setProgress(done,total);
                                                }

                                                @Override
                                                public void onFail(Throwable msg) {
                                                    isCurrVersionUpdateDialogShow = false;
                                                }
                                            },false);
                                        }
                                    });
                                    appHintUpdateDialog.show();
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }

            }
        });
    }


    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
