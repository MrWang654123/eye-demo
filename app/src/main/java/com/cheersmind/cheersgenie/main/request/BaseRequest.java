package com.cheersmind.cheersgenie.main.request;

import com.cheersmind.cheersgenie.features.dto.ResponseDto;
import com.cheersmind.cheersgenie.features.interfaces.ResponseStringCallback;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by goodm on 2017/4/15.
 */
public class BaseRequest {


    public static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType MediaType_Image_Type = MediaType.parse("image/png");

//    public static BaseRequest getInstance() {
//        if (instance == null) {
//            instance = new BaseRequest();
//        }
//        return instance;
//    }

    public interface BaseCallback {
        public abstract void onFailure(Exception e);
        public abstract void onResponse(int code, String bodyStr);
    }

    private static String getHeader(String url,String method) {
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
                || url.equals(HttpConfig.URL_RESET_PASSWORD)
                || url.equals(HttpConfig.URL_CREATE_SESSION)
                || url.equals(HttpConfig.URL_PHONE_CAPTCHA)
                || url.contains("verification_code")
                //微信相关接口
                || url.contains("api.weixin.qq.com")) {
            return "";
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

    public static void get (String url,final BaseCallback callback, String tag){
        OkHttpUtils
                .get()
                .url(url)
                .tag(tag)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json; charset=utf-8")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"GET"))
//                .addParams("username", "hyman")
                .build()
                .execute(new ResponseStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (call.isCanceled()) return;
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDto dto, int id) {
                        if (callback != null) {
                            callback.onResponse(dto.getCode(), dto.getBodyStr());
                        }
                    }
                });
    }

    public static void post (String url, Map<String,Object> params, boolean isFormType, final BaseCallback callback, String tag) {
        OkHttpUtils
                .postString()
                .url(url)
                .tag(tag)
                .addHeader("Accept","application/json")
//                .addHeader("Content-Type","application/json; charset=utf-8")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"POST"))
                .content(new Gson().toJson(params))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new ResponseStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (call.isCanceled()) return;
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDto dto, int id) {
                        if (callback != null) {
                            callback.onResponse(dto.getCode(), dto.getBodyStr());
                        }
                    }
                });
    }

    /**
     * POST请求，外部直接传入Json对象作为参数
     * @param url
     * @param params
     * @param callback
     */
    public static void post (String url, JSONObject params, final BaseCallback callback, String tag) {

        OkHttpUtils
                .postString()
                .url(url)
                .tag(tag)
                .addHeader("Accept","application/json")
//                .addHeader("Content-Type","application/json; charset=utf-8")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"POST"))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(params.toString())
                .build()
                .execute(new ResponseStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (call.isCanceled()) return;
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDto dto, int id) {
                        if (callback != null) {
                            callback.onResponse(dto.getCode(), dto.getBodyStr());
                        }
                    }
                });
    }


    /**
     * POST请求，上传图片
     * @param url
     * @param params
     * @param callback
     */
    public static void post (String url, Map<String,File> params, final BaseCallback callback, String tag) {

        PostFormBuilder post = OkHttpUtils.post();//
        for (Map.Entry<String, File> entry : params.entrySet()) {
            File file = entry.getValue();
            post.addFile(entry.getKey(), file.getName(), file);
        }
        post.url(url)//
                .tag(tag)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","multipart/form-data")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"POST"))
                .build()//
                .execute(new ResponseStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (call.isCanceled()) return;
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDto dto, int id) {
                        if (callback != null) {
                            callback.onResponse(dto.getCode(), dto.getBodyStr());
                        }
                    }
                });

    }


    public static void patch (String url, Map<String,Object> params,final BaseCallback callback, String tag) {

        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            try {
                obj.put(entry.getKey(),entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType_JSON, String.valueOf(obj));

        OkHttpUtils
                .patch()
                .url(url)
                .tag(tag)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json; charset=utf-8")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"PATCH"))
                .requestBody(requestBody)
                .build()
                .execute(new ResponseStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (call.isCanceled()) return;
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDto dto, int id) {
                        if (callback != null) {
                            callback.onResponse(dto.getCode(), dto.getBodyStr());
                        }
                    }
                });

    }


    public static void put (String url, Map<String,Object> params,final BaseCallback callback, String tag) {

        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            try {
                obj.put(entry.getKey(),entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType_JSON, String.valueOf(obj));

        OkHttpUtils
                .put()
                .url(url)
                .tag(tag)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json; charset=utf-8")
                .addHeader("CHEERSMIND-APPID", Constant.API_APP_ID)
                .addHeader("Authorization",getHeader(url,"PUT"))
                .requestBody(requestBody)
                .build()
                .execute(new ResponseStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (call.isCanceled()) return;
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDto dto, int id) {
                        if (callback != null) {
                            callback.onResponse(dto.getCode(), dto.getBodyStr());
                        }
                    }
                });

    }


    /**
     *  根据tag取消通信
     * @param tag
     */
    public static void cancelTag(String tag) {
        OkHttpUtils.getInstance().cancelTag(tag);
    }


}

