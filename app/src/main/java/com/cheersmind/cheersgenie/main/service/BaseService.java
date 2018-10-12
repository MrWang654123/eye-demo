package com.cheersmind.cheersgenie.main.service;

import android.text.TextUtils;

import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.request.BaseRequest;
import com.cheersmind.cheersgenie.main.view.TokenTimeOutView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 通信解析基础服务
 */
public class BaseService {

    public interface ServiceCallback {
        public abstract void onFailure(QSCustomException e);
        public abstract void onResponse(Object obj);
    }

    public static void get(final String url, final ServiceCallback callback) {
        get(url, callback, QSApplication.getCurrentActivity().getLocalClassName());
    }

    public static void get(final String url, final ServiceCallback callback, String tag) {
        BaseRequest.get(url, new BaseRequest.BaseCallback() {
            @Override
            public void onFailure(Exception e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(int code, String bodyStr) {
                onResponseDefault(code, bodyStr, callback);
            }
        }, tag);
    }

    public static void post(final String url, Map<String,Object> params, boolean isFormType, final ServiceCallback callback) {
        post(url, params, isFormType, callback, QSApplication.getCurrentActivity().getLocalClassName());
    }

    public static void post(final String url, Map<String,Object> params, boolean isFormType, final ServiceCallback callback, String tag) {

        BaseRequest.post(url, params, isFormType, new BaseRequest.BaseCallback() {
            @Override
            public void onFailure(Exception e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(int code, String bodyStr) {
                onResponseDefault(code, bodyStr, callback);
            }
        }, tag);
    }

    public static void post(final String url, JSONObject params, final ServiceCallback callback) {

        BaseRequest.post(url, params, new BaseRequest.BaseCallback() {
            @Override
            public void onFailure(Exception e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(int code, String bodyStr) {
                onResponseDefault(code, bodyStr, callback);
            }
        }, QSApplication.getCurrentActivity().getLocalClassName());
    }


    public static void post(final String url, Map<String,File> params, final ServiceCallback callback) {

        BaseRequest.post(url, params, new BaseRequest.BaseCallback() {
            @Override
            public void onFailure(Exception e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(int code, String bodyStr) {
                onResponseDefault(code, bodyStr, callback);
            }
        }, QSApplication.getCurrentActivity().getLocalClassName());
    }


    public static void patch(String url, Map<String,Object> params,final ServiceCallback callback) {
        BaseRequest.patch(url, params, new BaseRequest.BaseCallback() {
            @Override
            public void onFailure(Exception e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(int code, String bodyStr) {
                onResponseDefault(code, bodyStr, callback);
            }
        }, QSApplication.getCurrentActivity().getLocalClassName());
    }

    public static void put(String url, Map<String,Object> params,final ServiceCallback callback) {
        BaseRequest.put(url, params, new BaseRequest.BaseCallback() {
            @Override
            public void onFailure(Exception e) {
                onFailureDefault(e, callback);
            }

            @Override
            public void onResponse(int code, String bodyStr) {
                onResponseDefault(code, bodyStr, callback);
            }
        },QSApplication.getCurrentActivity().getLocalClassName());
    }


    /**
     *  根据tag取消通信
     * @param tag
     */
    public static void cancelTag(String tag) {
        BaseRequest.cancelTag(tag);
    }

    /**
     *  根据tag取消通信
     */
//    public static void cancelTag() {
//        BaseRequest.cancelTag(QSApplication.getPreActivity().getLocalClassName());
//    }

    /**
     * 默认错误处理
     * @param callback
     * @param e
     */
    private static void onFailureDefault(final Exception e, final ServiceCallback callback) {
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
     * @param code
     * @param bodyStr
     * @param callback
     * @throws IOException
     */
    private static void onResponseDefault(final int code, final String bodyStr, final ServiceCallback callback) {
        if (callback != null) {
                QSApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //目前的业务错误机制：只要业务验证发生任何错误，http的code都不会是200出头的正确应答code，
                            // 而只会返回什么400+，500+之类的，并且附带错误信息串
                            if (code == 200 || code == 201) {
                                JSONObject respObj = null;
                                //返回结果为""空字符串，视为成功
                                if (bodyStr == null || bodyStr.equals("")) {
                                    respObj = new JSONObject("{}");
                                } else {
                                    respObj = new JSONObject(bodyStr);
                                }
                                callback.onResponse(respObj);
                            } else {
                                //无效的权限令牌
                                if(!TextUtils.isEmpty(bodyStr) && bodyStr.contains(ErrorCode.AC_AUTH_INVALID_TOKEN)){
                                    TokenTimeOutView.getInstance().show(QSApplication.getCurrentActivity());
                                }

                                callback.onFailure(new QSCustomException(bodyStr));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFailure(new QSCustomException("服务器返回数据异常"));
                        }
                    }
                });
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
