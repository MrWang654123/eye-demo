package com.cheersmind.cheersgenie.main.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.cheersmind.cheersgenie.features.modules.base.activity.SplashActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by goodm on 2017/4/14.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.i("uncaughtException",ex.getMessage());

        //友盟统计：手动上传错误
        MobclickAgent.reportError(mContext, ex);

        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(10);

            //友盟统计：调用kill或者exit之类的方法杀死进程之前，保存统计数据
            MobclickAgent.onKillProcess(mContext);

            restartApp();
        }

    }

    private void restartApp(){
        System.out.println("uncaughtException "+"奔溃了，重新启动应用");

        try {
            Thread.sleep(500); // 1秒后重启，可有可无，仅凭个人喜好
            Intent intent = new Intent(mContext, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (InterruptedException e) {
        }
        // 退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        //ex.printStackTrace();
        StringBuilder sb = new StringBuilder();
        // 1.获取当前应用程序的版本号.
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(mContext.getPackageName(),
                    0);
            sb.append("程序的版本号为" + packinfo.versionName);
            sb.append("\n");

            // 2.获取手机的硬件信息.
            Field[] fields = Build.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                // 暴力反射,获取私有的字段信息
                fields[i].setAccessible(true);
                String name = fields[i].getName();
                sb.append(name + " = ");
                String value = fields[i].get(null).toString();
                sb.append(value);
                sb.append("\n");
            }
            // 3.获取程序错误的堆栈信息 .
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);

            String result =    writer.toString();
            sb.append(result);

            System.out.println(sb.toString());

            // 4.把错误信息 提交到服务器

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            StringBuilder fileNameBuilder = new StringBuilder();
            fileNameBuilder.append(year).append("-");
            fileNameBuilder.append(month<10?("0"+month):month).append("-");;
            fileNameBuilder.append(day).append(".txt");
            //FileOutputStream outStream = mContext.openFileOutput(fileNameBuilder.toString(),
            //        Context.MODE_PRIVATE);
            if (getExternalFileDir(mContext)!=null) {
                FileOutputStream outStream = new FileOutputStream(new File(getExternalFileDir(mContext)+fileNameBuilder.toString()),true);
                outStream.write(sb.toString().getBytes());
                outStream.close();

//                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//                FileWriter fileWriter = new FileWriter(getExternalFileDir(mContext)+fileNameBuilder.toString(), true);
//                fileWriter.write(sb.toString());
//                fileWriter.close();
                System.out.println("文件写入完成");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    /**
     * 获取拓展存储Cache的绝对路径
     *
     * @param context
     */
    private static String getExternalFileDir(Context context) {
        String state = Environment.getExternalStorageState();

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        File file = context.getExternalFilesDir(null);
        // In some case, even the sd card is mounted,
        // getExternalCacheDir will return null
        // may be it is nearly full.
        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/files/").append(File.separator).toString();
        }
        return sb.toString();
    }

}

