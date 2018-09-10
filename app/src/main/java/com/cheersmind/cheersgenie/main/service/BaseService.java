package com.cheersmind.cheersgenie.main.service;

import android.text.TextUtils;
import android.util.Log;

import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.request.BaseRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by goodm on 2017/4/15.
 */
public class BaseService {

    private static final  String TAG = "BaseService";

    public interface ServiceCallback {
        public abstract void onFailure(QSCustomException e);
        public abstract void onResponse(Object obj);
    }

    public static void get(final String url, final ServiceCallback callback) {
        BaseRequest.get(url, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                onResponseDefault(response, callback);
            }
        });
    }

    public static void post(final String url, Map<String,Object> params, boolean isFormType, final ServiceCallback callback) {

        BaseRequest.post(url, params, isFormType, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                onResponseDefault(response, callback);
            }
        });
    }

    public static void post(final String url, JSONObject params, final ServiceCallback callback) {

        BaseRequest.post(url, params, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                onResponseDefault(response, callback);
            }
        });
    }


    public static void patch(String url, Map<String,Object> params,final ServiceCallback callback) {
        BaseRequest.patch(url, params, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                onResponseDefault(response, callback);
            }
        });
    }

    public static void put(String url, Map<String,Object> params,final ServiceCallback callback) {
        BaseRequest.put(url, params, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                onResponseDefault(response, callback);
            }
        });
    }

    /**
     * 默认错误处理
     * @param callback
     * @param e
     */
    private static void onFailureDefault(final IOException e, final ServiceCallback callback) {
        if (callback != null) {
            QSApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof SocketTimeoutException) {
                        //判断超时异常
                        callback.onFailure(new QSCustomException("网络连接超时"));
                    }else if (e instanceof ConnectException) {
                        //判断连接异常
                        callback.onFailure(new QSCustomException("网络连接异常"));
                    } else {
                        //默认处理
//                                callback.onFailure(new QSCustomException(e.getMessage()));
                        callback.onFailure(new QSCustomException("网络异常"));
                    }
                }
            });
        }
    }

    /**
     * 默认响应处理
     * @param response
     * @param callback
     * @throws IOException
     */
    private static void onResponseDefault(final Response response, final ServiceCallback callback) throws IOException {
        if (callback != null) {
            try {
                //返回结果信息
                final String bodyStr = response.body().string();

                QSApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //目前的业务错误机制：只要业务验证发生任何错误，http的code都不会是200出头的正确应答code，
                            // 而只会返回什么400+，500+之类的，并且附带错误信息串
                            if (response.code() == 200 || response.code() == 201) {
                                JSONObject respObj = null;
                                //返回结果为""空字符串，视为成功
                                if (bodyStr == null || bodyStr.equals("")) {
                                    respObj = new JSONObject("{}");
                                } else {
                                    respObj = new JSONObject(bodyStr);
                                }
                                callback.onResponse(respObj);
                            } else {
                                callback.onFailure(new QSCustomException(bodyStr));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFailure(new QSCustomException("服务器返回数据异常"));
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure(new QSCustomException("返回数据异常"));
            }
        }
    }


    /**
     * 拼接get参数
     * @param url
     * @param params
     * @return
     */
    public static String settingGetParams(String url, Map<String, Object> params) {
        String result = url;

        if (params != null && params.size() > 0) {
            if (result.indexOf("?") == -1) {
                result += "?";
            }

            if (result.indexOf("&") != -1) {
                result += "&";
            }

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                result += (entry.getKey() + "=" + entry.getValue() + "&");
            }

            //删除最后一个&
            result = result.substring(0, result.length()-1);
        }

        return result;
    }

}
