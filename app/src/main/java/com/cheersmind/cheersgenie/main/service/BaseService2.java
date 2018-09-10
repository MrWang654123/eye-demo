package com.cheersmind.cheersgenie.main.service;

import android.text.TextUtils;
import android.util.Log;

import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by goodm on 2017/4/15.
 */
public class BaseService2 {

    private static final  String TAG = "BaseService";

    public interface ServiceCallback {
        public abstract void onFailure(QSCustomException e);
        public abstract void onResponse(Object obj);
    }

    public static void get(final String url, final ServiceCallback callback) {
        BaseRequest.get(url, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    QSApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new QSCustomException(e.getMessage()));
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if(TextUtils.isEmpty(resp)){
                    resp = "{}";//特殊处理返回串为空的情况
                }
                final String respStr = resp;
                Log.i(TAG,"GET -- "+respStr);
                if (callback != null) {
                    QSApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                if(url.contains("projects")){
//                                    JSONArray jsonArray = new JSONArray(respStr);
//                                    JSONObject respObj = new JSONObject();
//                                    respObj.put("items", respObj);
                                    String respStr2 = "{" + "items" + ":" + respStr +"}";
                                    JSONObject respObj = new JSONObject(respStr2);
                                    callback.onResponse(respObj);
                                }else{
                                    JSONObject respObj = new JSONObject(respStr);
                                    callback.onResponse(respObj);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onFailure(new QSCustomException("服务器数据异常"));
                            }
                        }
                    });
                }
            }
        });
    }

    public static void post(final String url, Map<String,Object> params, boolean isFormType, final ServiceCallback callback) {

        BaseRequest.post(url, params, isFormType, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
//                System.out.println("BaseService post------------------"+e.toString());
                if (callback != null) {
                    QSApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new QSCustomException(e.getMessage()));
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String respStr = response.body().string();
                Log.i(TAG,"POST -- "+respStr);

                if (callback != null) {
                    QSApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject respObj = new JSONObject(respStr);
                                Log.i("WXTest", "respone:"+respStr.toString());
                                if (url.equals(HttpConfig.URL_LOGIN)) {
                                    if (response.code() == 201 || response.code() == 200) {
                                        callback.onResponse(respObj);
                                    } else {
                                        callback.onFailure(new QSCustomException(respObj.getString("message")));
                                    }
                                } else {
                                    callback.onResponse(respObj);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onFailure(new QSCustomException(e.getMessage()));
                            }
                        }
                    });
                }
            }
        });
    }

    public static void patch(String url, Map<String,Object> params,final Callback callback) {

    }

    public static void put(String url, Map<String,Object> params,final Callback callback) {

    }
}
