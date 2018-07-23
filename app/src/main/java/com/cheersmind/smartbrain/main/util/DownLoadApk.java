package com.cheersmind.smartbrain.main.util;

import android.content.pm.PackageInfo;
import android.os.Environment;
import com.cheersmind.smartbrain.main.QSApplication;

import java.io.File;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/22.
 */
public class DownLoadApk {

    private String mFile;
    FileDownloaderUtils loader=null;
    private String downUrl;

    public DownLoadApk(String downUrl){
        this.downUrl = downUrl;
    }

    /**
     * 下载apk
     * @param callback
     * @param isSync
     */
    public void downloadApk(FileDownloaderUtils.DownloadCallBack callback,boolean isSync){

        //PackageInfo appVersion = PackageUtils.getPackage(MyApplication.getMyContext(),PackageUtils.CURRENT_APP_PACKAGE_NAME);
        PackageInfo appVersion = PackageUtils.getPackage(QSApplication.getContext(), PackageUtils.CURRENT_APP_PACKAGE_NAME);
        if(appVersion==null){
            return;
        }
        File cacheDir = QSApplication.getContext().getExternalCacheDir();

        mFile = String.format(Locale.CHINA, "%s/"+appVersion.packageName+".apk",cacheDir!=null?cacheDir: Environment.getExternalStorageDirectory().getAbsolutePath());

        if(loader!=null){
            loader.cancel();
        }

        if(new File(mFile).exists()){
            new File(mFile).delete();
        }

        try {
            loader = new FileDownloaderUtils(downUrl, mFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loader.setBlockSize(1024 * 10);//10kb

        loader.setCallBack(callback);

        loader.download(isSync);

    }

    public void installApk(){

        PackageUtils.installPackage(QSApplication.getContext(), new File(mFile));
    }

}
