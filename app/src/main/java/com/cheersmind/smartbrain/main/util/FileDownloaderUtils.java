package com.cheersmind.smartbrain.main.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/16.
 */
public class FileDownloaderUtils {

    private AsyncTask<String, Integer, Boolean> mTask;

    private String mUrl;

    private String mLocalFile;

    private DownloadCallBack mCallBack;

    private int mBlockSize = BLOCK_SIZE;

    private HttpURLConnection mConnection;

    private Throwable mError;

    /**
     * 默认缓存块大小
     */
    public final static int BLOCK_SIZE = 1024;

    /**
     *
     * @param source
     *            下载的地址
     * @param destination
     *            本地保存地址
     * @throws Exception
     */
    public FileDownloaderUtils(String source, String destination) throws Exception {
        mUrl = source;
        mLocalFile = destination;

        if (mUrl == null || mUrl.trim().equals("")) {
            throw new Exception("source is null");
        }

        if (mLocalFile == null || mLocalFile.trim().equals("")) {
            throw new Exception("destination is null");
        }

    }

    public FileDownloaderUtils(String source, String destination,
                               DownloadCallBack callback) throws Exception {
        this(source, destination);
        this.mCallBack = callback;
    }

    public void setCallBack(DownloadCallBack callBack) {
        mCallBack = callBack;
    }

    public void removeCallBack() {
        mCallBack = null;
    }

    /**
     * 取消下载
     *
     * @return
     */
    public boolean cancel() {
        if (mTask != null) {
            if (mTask.isCancelled())
                return true;
            return mTask.cancel(false);
        }

        return true;
    }

    /**
     * 下载的块大小，单位byte，大于0的整数。如果不大于0会被设置为默认值 {@link #BLOCK_SIZE}
     *
     * @param blockSize
     */
    public void setBlockSize(int blockSize) {
        if (blockSize <= 0)
            mBlockSize = BLOCK_SIZE;
        else
            mBlockSize = blockSize;
    }

    /**
     * 下载的块大小
     *
     * @return
     */
    public int getBlockSize() {
        return mBlockSize;
    }


    public void download(){
        download(true);
    }

    /**
     *
     * <p>
     * 下载
     * </P>
     *
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void download(boolean isSync) {
        if (mTask == null) {
            mTask = new AsyncTask<String, Integer, Boolean>() {

                protected Boolean doInBackground(String... urls) {

                    //get the downloaded size
                    long downloaded=0;
                    File file=new File(mLocalFile);
                    if(!file.exists()){
                        try {
                            file.createNewFile();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                            mError=new Exception("cannot create destination file");
                            return false;
                        }
                    }else
                        downloaded=file.length();


                    // start the task
                    publishProgress(Integer.MIN_VALUE);

                    URL url = null;
                    InputStream is = null;
                    FileOutputStream fos = null;

                    try {
                        url = new URL(mUrl);
                    } catch (MalformedURLException e2) {
                        e2.printStackTrace();
                        mError = e2;
                        return false;
                    }

                    try {
                        mConnection = (HttpURLConnection) url.openConnection();
                        //跳过已经卸载的
                        if(downloaded>0){
                            mConnection.setRequestProperty("RANGE", String.format(Locale.CHINA, "bytes=%d-",downloaded));

                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        mError = e2;
                        return false;
                    }


                    try {

                        //范围值错误，则认为已经下载了全部内容
                        if(mConnection.getResponseCode()==416 && downloaded>0){
                            mConnection.disconnect();

                            publishProgress((int)downloaded,(int)downloaded);

                            return true;
                        }


                        if(mConnection.getResponseCode()!=200 && mConnection.getResponseCode()!=206){
                            mError=new Exception("connection error");
                            return false;
                        }
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        mError=e3;
                        return false;
                    }

                    int length = mConnection.getContentLength();

                    //已经全部下载完毕，直接结束
                    if(0==length){
                        mConnection.disconnect();
                        return true;
                    }

                    length+=downloaded;

                    try {
                        is = mConnection.getInputStream();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        mError = e2;
                        return false;
                    }

                    byte[] buffer = new byte[mBlockSize];
                    int total = (int)downloaded;
                    int count = 0;


                    try {

                        fos = new FileOutputStream(mLocalFile,true);
                    } catch (FileNotFoundException e2) {
                        e2.printStackTrace();
                        try {
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        mError = e2;
                        return false;
                    }

                    //read data
                    try {
                        while (!mTask.isCancelled() && (count = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);

                            total += count;
                            publishProgress(total, length);
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();

                        mError = e2;
                        return false;

                    } finally {

                        try {
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        try {

                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mConnection.disconnect();

                    return true;
                }

                protected void onProgressUpdate(Integer... progress) {
                    if (progress[0] == Integer.MIN_VALUE) {
                        if (mCallBack != null)
                            mCallBack.onStart();
                    } else {
                        if (mCallBack != null)
                            mCallBack.onProgress(progress[0], progress[1]);
                    }
                }

                protected void onPostExecute(Boolean result) {
                    if (result) {
                        if (mCallBack != null)
                            mCallBack.onFinish();

                    } else {
                        if (mCallBack != null)
                            mCallBack.onFail(mError);
                    }
                }


            };
        }
//		mTask.execute(mUrl);
        if(isSync) {
            mTask.execute(mUrl);
        }else {
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
        }


    }

    /**
     *
     * <p>
     * 下载回调
     * </p>
     * <p>
     * Created by johnny wu on 2014-7-23
     * </p>
     */
    public interface DownloadCallBack {
        void onStart();

        void onFinish();

        void onProgress(int done, int total);

        void onFail(Throwable msg);


    }
}
