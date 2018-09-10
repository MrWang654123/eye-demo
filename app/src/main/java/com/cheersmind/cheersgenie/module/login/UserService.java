    package com.cheersmind.cheersgenie.module.login;

    import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
    import com.cheersmind.cheersgenie.main.constant.HttpConfig;
    import com.cheersmind.cheersgenie.main.service.BaseService;
    import com.cheersmind.cheersgenie.main.util.EncryptUtil;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.HashMap;
    import java.util.Map;

    /**
     * Created by goodm on 2017/4/15.
     */
    public class UserService {

        public static void logon(String loginName,String password,final BaseService.ServiceCallback callback) {
            String url = HttpConfig.URL_LOGIN;

            String pwdMd5 = EncryptUtil.encryptMD5_QS(password);
    //        String params;
    //        if (loginName.contains("@")) {
    //            params = "{\"login_name\":\""+loginName+"\",\"password\":\""+pwdMd5+"\"}";
    //        } else {
    //            params = "{\"login_name\":\""+loginName+"\",\"password\":\""+pwdMd5+"\",\"org_name\":\"cheersmind\"}";
    //        }

            Map<String,Object> param = new HashMap<String,Object>();
            param.put("account",loginName);
            param.put("password",pwdMd5);
            param.put("tenant","CHEERSMIND");

            BaseService.post(url, param, false, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

                @Override
                public void onResponse(Object obj) {
                    try {
                        String accessToken = ((JSONObject) obj).getString("access_token");
    //                    String macKey = ((JSONObject) obj).getString("mac_key");
                        String macKey = ((JSONObject) obj).getString("code");
                        String refreshToken = ((JSONObject) obj).getString("refresh_token");
                        long userId = ((JSONObject) obj).getLong("user_id");
                        UCManager.getInstance().setAcccessToken(accessToken);
                        UCManager.getInstance().setMacKey(macKey);
                        UCManager.getInstance().setRefreshToken(refreshToken);
                        UCManager.getInstance().setUserId(userId);

                        if (callback != null) {
                            callback.onResponse(obj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public static void getUserInfo (final BaseService.ServiceCallback callback){
            String url = HttpConfig.URL_USER_INFO.replace("{user_id}",UCManager.getInstance().getUserId()+"");
            BaseService.get(url, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

                @Override
                public void onResponse(Object obj) {
                    try {
                        String nickName = ((JSONObject) obj).getString("nick_name");
                        UCManager.getInstance().setNickName(nickName);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (callback != null) {
                        callback.onResponse(obj);
                    }
                }
            });
        }

        public static void getChildList(final BaseService.ServiceCallback callback) {
            String url = HttpConfig.URL_CHILD_LIST;
            BaseService.get(url, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

                @Override
                public void onResponse(Object obj) {
                    if (callback != null) {
                        callback.onResponse(obj);
                    }
                }
            });
        }

    //    public static void getChildInfo(long childId, final BaseService.ServiceCallback callback) {
    //        String url = HttpConfig.URL_CHILD_INFO.replace("{child_id}",String.valueOf(childId));
    //        BaseService.get(url, new BaseService.ServiceCallback() {
    //            @Override
    //            public void onFailure(QSCustomException e) {
    //                if (callback != null) {
    //                    callback.onFailure(e);
    //                }
    //            }
    //
    //            @Override
    //            public void onResponse(Object obj) {
    //                if (callback != null) {
    //                    callback.onResponse(obj);
    //                }
    //            }
    //        });
    //    }
    }
