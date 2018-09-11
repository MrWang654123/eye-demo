package com.cheersmind.cheersgenie.main.request;

import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by goodm on 2017/4/15.
 */
public class BaseRequest {

//    private static BaseRequest instance;
//    private BaseRequest (){}

    public static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");

//    public static BaseRequest getInstance() {
//        if (instance == null) {
//            instance = new BaseRequest();
//        }
//        return instance;
//    }

    private static String getHeader(String url,String method) {
        return getHeader(url, method, null);
    }

    private static String getHeader(String url,String method, Map<String,Object> params) {
        if (url.equals(HttpConfig.URL_LOGIN)
                || url.equals(HttpConfig.URL_CODE_INVATE)
                || url.equals(HttpConfig.URL_CODE_REGISTERS)
                || url.contains("check_child_invite")
                || url.contains(HttpConfig.URL_UC_THIRD_LOGIN)
                || url.contains(HttpConfig.URL_UC_REGISTER)
                || url.equals(HttpConfig.URL_UPDATE_NOTIFICATION)
                || url.equals(HttpConfig.URL_SERVER_TIME)
                || url.equals(HttpConfig.URL_PHONE_MESSAGE_REGISTER)
                || url.equals(HttpConfig.URL_PHONE_NUM_LOGIN)
                || url.equals(HttpConfig.URL_ACCOUNT_LOGIN)
                || url.equals(HttpConfig.URL_UC_THIRD_LOGIN_V2)
                || url.equals(HttpConfig.URL_RESET_PASSWORD)) {
            return "";
        }

        //发送短信验证码，其中绑定手机号的情况得传token
        if (url.equals(HttpConfig.URL_PHONE_CAPTCHA)) {
            if (params != null) {
                Integer type = (Integer)params.get("type");
                if (type != Dictionary.SmsType_Bind_Phone_Num) {
                    return "";
                }
            }
        }

        String host = url.startsWith(HttpConfig.API_HOST)?HttpConfig.API_HOST:HttpConfig.UC_HOST;
        String nonce = System.currentTimeMillis()+":";
        String[] randomStrs = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        for (int i=0;i<8;i++) {
            java.util.Random random = new java.util.Random();
            int randomInt = random.nextInt(randomStrs.length);// 返回[0,10)集合中的整数，注意不包括10
            nonce += randomStrs[randomInt];
        }

        String rawMac = nonce+"\n"+method+"\n"+url.replace(host,"")+"\n"+host.replace("http://","")+"\n";
        //rawMac = "ADFFJ323423JLKFDSFSDFE";
        String newMac = EncryptUtil.encryptHMac256(rawMac, UCManager.getInstance().getMacKey());

        //String strAuth = "MAC id=\""+UCManager.getInstance().getAcccessToken()+"\",nonce=\""+nonce+"\",mac=\""+newMac+"\"";
        String strAuth = "MAC id=\"{access_token}\",nonce=\"{nonce}\",mac=\"{mac}\"";
        String accessToken = UCManager.getInstance().getAcccessToken() != null ? UCManager.getInstance().getAcccessToken() : "";
        strAuth = strAuth.replace("{access_token}",accessToken).replace("{nonce}",nonce).replace("{mac}",newMac);
        strAuth = strAuth.replaceAll("\n","");
        //System.out.println("newMac:"+newMac+"|strAuth : "+strAuth);
        //strAuth = "MAC id=\"622533FCE445BD3B37D6E3FC894B976953DF5C31629C66C154341F166872F1DB9CEB31C1CE59FD78\",nonce=\"1483639450423:gZNZ6JRP\",mac=\"drN/ea5/CjwbR7tSEpI/NoTeUoMV38PowXL880d0kuU=\"";
        return strAuth;
    }

    public static void get (String url,final Callback callback){
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //请求超时设置
        mOkHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        //创建一个Request
        final Request request = new Request.Builder()
                .addHeader("Content-Type","application/json; charset=utf-8")
                .addHeader("Accept","application/json")
                .url(url)
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"GET"))
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (callback != null) {
                    callback.onFailure(call,e);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void post (String url, Map<String,Object> params, boolean isFormType, final Callback callback) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();

        //builder.add("xwdoor","xwdoor");
        RequestBody formBody;

        if (isFormType) {
            FormBody.Builder builder = new FormBody.Builder();

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
            formBody = builder.build();
        } else {
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                try {
                    obj.put(entry.getKey(),entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            formBody = RequestBody.create(MediaType_JSON, String.valueOf(obj));
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"POST", params))
                .post(formBody)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (callback != null) {
                    callback.onFailure(call,e);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void post (String url, JSONObject params, final Callback callback) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();

        //builder.add("xwdoor","xwdoor");
        RequestBody formBody;

        formBody = RequestBody.create(MediaType_JSON, String.valueOf(params));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"POST"))
                .post(formBody)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (callback != null) {
                    callback.onFailure(call,e);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void patch (String url, Map<String,Object> params,final Callback callback) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            try {
                obj.put(entry.getKey(),entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody fequestBody = RequestBody.create(MediaType_JSON, String.valueOf(obj));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",getHeader(url,"PATCH"))
                .patch(fequestBody)
                .build();

        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (callback != null) {
                    callback.onFailure(call,e);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }


    public static void put (String url, Map<String,Object> params,final Callback callback) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            try {
                obj.put(entry.getKey(),entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody fequestBody = RequestBody.create(MediaType_JSON, String.valueOf(obj));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",getHeader(url,"PUT"))
                .put(fequestBody)
                .build();

        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (callback != null) {
                    callback.onFailure(call,e);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}
