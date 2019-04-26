package com.cheersmind.cheersgenie.main.service;

import android.content.Context;
import android.text.TextUtils;

import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.utils.NetworkUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.request.BaseRequest;
import com.cheersmind.cheersgenie.main.view.TokenTimeOutView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 通信解析基础服务
 */
public class BaseService {

    public interface ServiceCallback {
        void onFailure(QSCustomException e);
        void onResponse(Object obj);
    }


    public static void get(final String url, final ServiceCallback callback, String tag, final Context context) {
        if (NetworkUtil.isConnectivity(context)) {
            BaseRequest.get(url, new BaseRequest.BaseCallback() {
                @Override
                public void onFailure(Exception e) {
                    onFailureDefault(e, callback);
                }

                @Override
                public void onResponse(int code, String bodyStr) {
                    onResponseDefault(code, bodyStr, callback, context);
                }
            }, tag);
        } else {
            onFailureDefault(new QSCustomException("网络异常， 请检查网络"), callback);
        }
    }


    public static void post(final String url, Map<String,Object> params, boolean isFormType, final ServiceCallback callback, String tag, final Context context) {
        if (NetworkUtil.isConnectivity(context)) {
            BaseRequest.post(url, params, isFormType, new BaseRequest.BaseCallback() {
                @Override
                public void onFailure(Exception e) {
                    onFailureDefault(e, callback);
                }

                @Override
                public void onResponse(int code, String bodyStr) {
                    onResponseDefault(code, bodyStr, callback, context);
                }
            }, tag);
        } else {
            onFailureDefault(new QSCustomException("网络异常， 请检查网络"), callback);
        }
    }

    public static void post(final String url, JSONObject params, final ServiceCallback callback, String httpTag, final Context context) {
        if (NetworkUtil.isConnectivity(context)) {
            BaseRequest.post(url, params, new BaseRequest.BaseCallback() {
                @Override
                public void onFailure(Exception e) {
                    onFailureDefault(e, callback);
                }

                @Override
                public void onResponse(int code, String bodyStr) {
                    onResponseDefault(code, bodyStr, callback, context);
                }
            }, httpTag);
        } else {
            onFailureDefault(new QSCustomException("网络异常， 请检查网络"), callback);
        }
    }


    public static void post(final String url, Map<String,File> params, final ServiceCallback callback, String httpTag, final Context context) {
        if (NetworkUtil.isConnectivity(context)) {
            BaseRequest.post(url, params, new BaseRequest.BaseCallback() {
                @Override
                public void onFailure(Exception e) {
                    onFailureDefault(e, callback);
                }

                @Override
                public void onResponse(int code, String bodyStr) {
                    onResponseDefault(code, bodyStr, callback, context);
                }
            }, httpTag);
        } else {
            onFailureDefault(new QSCustomException("网络异常， 请检查网络"), callback);
        }
    }


    public static void patch(String url, Map<String,Object> params,final ServiceCallback callback, String tag, final Context context) {
        if (NetworkUtil.isConnectivity(context)) {
            BaseRequest.patch(url, params, new BaseRequest.BaseCallback() {
                @Override
                public void onFailure(Exception e) {
                    onFailureDefault(e, callback);
                }

                @Override
                public void onResponse(int code, String bodyStr) {
                    onResponseDefault(code, bodyStr, callback, context);
                }
            }, tag);
        } else {
            onFailureDefault(new QSCustomException("网络异常， 请检查网络"), callback);
        }
    }

    public static void put(String url, Map<String,Object> params,final ServiceCallback callback, String httpTag, final Context context) {
        if (NetworkUtil.isConnectivity(context)) {
            BaseRequest.put(url, params, new BaseRequest.BaseCallback() {
                @Override
                public void onFailure(Exception e) {
                    onFailureDefault(e, callback);
                }

                @Override
                public void onResponse(int code, String bodyStr) {
                    onResponseDefault(code, bodyStr, callback, context);
                }
            },httpTag);
        } else {
            onFailureDefault(new QSCustomException("网络异常， 请检查网络"), callback);
        }
    }


    /**
     *  根据httpTag取消通信
     * @param httpTag 通信标记
     */
    public static void cancelTag(String httpTag) {
        BaseRequest.cancelTag(httpTag);
    }


    /**
     * 默认错误处理
     * @param callback 回调
     * @param e 异常
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
                    } else if (e instanceof QSCustomException) {
                        //自定义异常
                        callback.onFailure((QSCustomException)e);
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
     * @param code http错误码
     * @param bodyStr 结果字符串
     * @param callback 回调
     */
    private static void onResponseDefault(final int code, final String bodyStr, final ServiceCallback callback, final Context context) {
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
                                    TokenTimeOutView.getInstance().show(context);
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
     * @param url 访问地址
     * @param params 参数
     * @return 拼接结果
     */
    public static String settingGetParams(String url, Map<String, Object> params) {
        StringBuilder result = new StringBuilder(url);

        if (params != null && params.size() > 0) {
//            if (!result.toString().contains("?")) {
//                result.append("?");
//            } else {
//
//            if (result.toString().contains("&")) {
//                result.append("&");
//            }

            if (!result.toString().contains("?")) {
                result.append("?");
            } else {
                result.append("&");
            }

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                result.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            //删除最后一个&
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }

        return result.toString();
    }


}
