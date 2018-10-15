package com.cheersmind.cheersgenie.features.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil {

    /**
     * 保存文件，到Android外部文件系统
     * @param context 上下文
     * @param saveName 保存后的文件名称
     * @param file 内容文件
     * @return true：保存成功
     */
    public static boolean saveFileToExtraDirs(Context context,String saveName, File file) {
        boolean res = false;
        try {
            String state = Environment.getExternalStorageState();

            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                return false;
            }
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (externalFilesDir != null) {
                //目标文件完整名称
                String descFullFileName = externalFilesDir.getAbsolutePath() + File.separator + saveName;

                //打开文件输入流
                FileOutputStream outputStream = new FileOutputStream(descFullFileName);
                FileInputStream inputStream = new FileInputStream(file);

                byte[] buffer = new byte[1024];
                int len = 0;
                //读取文件内容
                while((len = inputStream.read(buffer)) != 0){
                    outputStream.write(buffer,0, len);
                }

                //关闭输入流
                inputStream.close();
                //关闭输出流
                outputStream.close();

                //标记存储成功
                res = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }


    /**
     * 提取文件，从Android外部文件系统
     * @param context 上下文
     * @param fileName 文件名称
     * @return 文件对象
     */
    public static File getFileFromExtraDirs(Context context, String fileName) {
        File file = null;
        try {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                return null;
            }
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (externalFilesDir !=null && externalFilesDir.exists()) {
                //目标文件完整名称
                String descFullFileName = externalFilesDir.getAbsolutePath() + File.separator + fileName;
                file = new File(descFullFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }


    /**
     * 删除文件，在Android外部文件系统
     * @param context 上下文
     * @param fileName 文件名称
     * @return true：删除成功
     */
    public static boolean removeFileOnExtraDirs(Context context, String fileName) {
        boolean res = false;
        try {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                return false;
            }
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (externalFilesDir != null && externalFilesDir.exists()) {
                //目标文件完整名称
                String descFullFileName = externalFilesDir.getAbsolutePath() + File.separator + fileName;
                File file = new File(descFullFileName);
                if (file.exists()) {
                    res = file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }


}
