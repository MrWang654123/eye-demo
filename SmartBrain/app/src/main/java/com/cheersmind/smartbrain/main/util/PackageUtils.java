package com.cheersmind.smartbrain.main.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 版本更新相关
 * Created by Administrator on 2016/6/16.
 */
public class PackageUtils {

    public final static String CURRENT_APP_PACKAGE_NAME = "com.cheersmind.smartbrain";

    private final static String SDCARD_STORAGE = Environment.getExternalStorageDirectory()+ "/" + CURRENT_APP_PACKAGE_NAME;


    public static String getSdcardStorage() {
        return SDCARD_STORAGE;
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
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
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
