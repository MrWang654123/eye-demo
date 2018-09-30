package com.cheersmind.cheersgenie.main.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.main.QSApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 版本更新相关
 * Created by Administrator on 2016/6/16.
 */
public class PackageUtils {

//    public final static String CURRENT_APP_PACKAGE_NAME = "com.cheersmind.cheersgenie";
    //当前应用的包名
    public final static String CURRENT_APP_PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    private final static String SDCARD_STORAGE = Environment.getExternalStorageDirectory()+ "/" + CURRENT_APP_PACKAGE_NAME;


    public static String getSdcardStorage() {
        return SDCARD_STORAGE;
    }


    //请求安装未知来源的应用
    public static final int INSTALL_PACKAGES_REQUESTCODE = 930;
    //将用户引导至安装未知应用界面
    public static final int GET_UNKNOWN_APP_SOURCES = 931;

    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    public static void checkIsAndroidO(Activity activity, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = activity.getPackageManager().canRequestPackageInstalls();
            if (b) {
                installPackage(activity, file);//安装应用的逻辑(写自己的就可以)
            } else {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
            }
        } else {
            installPackage(activity, file);
        }

    }


    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    public static void checkIsAndroidO(Activity activity) {
        PackageInfo appVersion = PackageUtils.getPackage(QSApplication.getContext(), PackageUtils.CURRENT_APP_PACKAGE_NAME);
        if(appVersion==null){
            return;
        }
        File cacheDir = QSApplication.getContext().getExternalCacheDir();

//        mFile = String.format(Locale.CHINA, "%s/"+appVersion.packageName+".apk",cacheDir!=null?cacheDir: Environment.getExternalStorageDirectory().getAbsolutePath());
        String mFile = String.format(Locale.CHINA, "%s/"+ BuildConfig.APPLICATION_ID+".apk",cacheDir!=null?cacheDir: Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = new File(mFile);
        if(!file.exists()){
            return;
        }

        checkIsAndroidO(activity, file);
    }


    /**
     * 安装apk
     * @param context
     * @param file
     */
    public static void installPackage(Context context, File file)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".fileprovider",file);
        } else {
            uri = Uri.fromFile(file);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 安装apk
     * @param context
     */
    public static void installPackage(Context context)
    {
        PackageInfo appVersion = PackageUtils.getPackage(QSApplication.getContext(), PackageUtils.CURRENT_APP_PACKAGE_NAME);
        if(appVersion==null){
            return;
        }
        File cacheDir = QSApplication.getContext().getExternalCacheDir();

//        mFile = String.format(Locale.CHINA, "%s/"+appVersion.packageName+".apk",cacheDir!=null?cacheDir: Environment.getExternalStorageDirectory().getAbsolutePath());
        String mFile = String.format(Locale.CHINA, "%s/"+ BuildConfig.APPLICATION_ID+".apk",cacheDir!=null?cacheDir: Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = new File(mFile);
        if(!file.exists()){
            return;
        }

        installPackage(context, file);
    }


    /**
     * 获取安装包信息
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getPackage(Context context, String packageName)
    {
        try {
            PackageManager pm=context.getPackageManager();
            return pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取版本号
     * @param context
     * @param packageName
     * @return
     */
    public static int getPackageVersion(Context context, String packageName){
        PackageInfo info=getPackage(context, packageName);
        return info.versionCode;
    }

    @Deprecated
    public static boolean isPackageInstalled(Context context, final String packageName)
    {
        List<PackageInfo> packages=getAllUserPackages(context);
        if(null == packages)
            return false;
        for(PackageInfo item:packages)
        {
            if(packageName == item.applicationInfo.packageName)
                return true;
        }

        return false;
    }

    public static boolean checkInstall(Context context, String packageName)
    {
        boolean install=false;
        PackageManager pm=context.getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (info!=null&&info.activities.length>0) {
                install=true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return install;
    }

    public static List<PackageInfo> getAllPackages(Context context)
    {
        return context.getPackageManager().getInstalledPackages(0);
    }

    public static List<PackageInfo> getAllSystemPackages(Context context)
    {
        return getPackages(context, true);
    }

    public static List<PackageInfo> getAllUserPackages(Context context)
    {
        return getPackages(context, false);
    }

    private static List<PackageInfo> getPackages(Context context, boolean isSystemPackage)
    {
        List<PackageInfo> packages=getAllPackages(context);
        if(null != packages && packages.size()>0)
        {
            List<PackageInfo> ret=new ArrayList<PackageInfo>();
            for(PackageInfo item:packages)
            {
                if(isSystemPackage(item) == isSystemPackage)
                    ret.add(item);

            }

            return ret;
        }

        return null;
    }



    public static boolean isSystemPackage(PackageInfo packageInfo)
    {
        return 1 == (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM);
    }

    public static String getAppLabel(Context context, PackageInfo info)
    {
        return (String) context.getPackageManager().getApplicationLabel(info.applicationInfo);

    }

    public static Drawable getAppIcon(Context context, PackageInfo packageInfo)
    {
        return context.getPackageManager().getApplicationIcon(packageInfo.applicationInfo);
    }



    public static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null)
            l1 = 0L;
        while (true) {
            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }

    }


    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
