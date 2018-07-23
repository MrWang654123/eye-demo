package com.cheersmind.smartbrain.main.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.QSApplication;
import com.cheersmind.smartbrain.main.constant.Constant;
import com.cheersmind.smartbrain.main.constant.HttpConfig;
import com.cheersmind.smartbrain.main.entity.VersionEntity;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.view.LoadingView;
import com.cheersmind.smartbrain.main.view.qsdialog.AppHintUpdateDialog;
import com.cheersmind.smartbrain.main.view.qsdialog.AppUpdateDialog;

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
        String url = HttpConfig.URL_VERSION_UPDATE.replace("{app_id}", Constant.API_APP_ID);
        LoadingView.getInstance().show(context);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                Toast.makeText(context,"获取版本信息失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    final VersionEntity versionObj = InjectionWrapperUtil.injectMap(dataMap,VersionEntity.class);

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
                                                downLoadApk.installApk();
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

//                                CustomDialog.Builder builder = new CustomDialog.Builder(context,true);
//                                builder.setTitle("版本更新")
//                                        .setMessage(updateDesc!=null?updateDesc:"有最新的版本,是否立即更新?");
//
//                                if (forceUpdate == 1) {
//                                    //强制更新
//                                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                            isCurrVersionUpdateDialogShow = true;
//                                            final DownLoadApk downLoadApk = new DownLoadApk(apkDownloadUrl);
//
//                                            downLoadApk.downloadApk(new FileDownloaderUtils.DownloadCallBack() {
//                                                @Override
//                                                public void onStart() {
//
//                                                }
//
//                                                @Override
//                                                public void onFinish() {
//                                                    downLoadApk.installApk();
//                                                }
//
//                                                @Override
//                                                public void onProgress(int done, int total) {
//                                                    System.out.println("downloadApk : onProgress done : "+done+" | total : "+total);
//                                                }
//
//                                                @Override
//                                                public void onFail(Throwable msg) {
//
//                                                }
//                                            },false);
//                                        }
//                                    });
//                                } else {
//                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                            isCurrVersionUpdateDialogShow = false;
//                                            final DownLoadApk downLoadApk = new DownLoadApk(apkDownloadUrl);
//
//                                            downLoadApk.downloadApk(new FileDownloaderUtils.DownloadCallBack() {
//                                                @Override
//                                                public void onStart() {
//
//                                                }
//
//                                                @Override
//                                                public void onFinish() {
//                                                    downLoadApk.installApk();
//                                                }
//
//                                                @Override
//                                                public void onProgress(int done, int total) {
//                                                    System.out.println("downloadApk : onProgress done : "+done+" | total : "+total);
//                                                }
//
//                                                @Override
//                                                public void onFail(Throwable msg) {
//
//                                                }
//                                            },false);
//                                        }
//                                    });
//
//                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                            isCurrVersionUpdateDialogShow = false;
//                                        }
//                                    });
//                                }
//
//
//                                CustomDialog dialog = builder.create();
//                                dialog.setCanceledOnTouchOutside(false);
//                                dialog.show();
//                                isCurrVersionUpdateDialogShow = true;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
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