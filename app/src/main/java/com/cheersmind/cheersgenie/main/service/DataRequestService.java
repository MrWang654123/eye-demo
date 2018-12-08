package com.cheersmind.cheersgenie.main.service;

import android.text.TextUtils;
import android.util.Log;

import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.AccountLoginDto;
import com.cheersmind.cheersgenie.features.dto.AnswerDto;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features.dto.BindPhoneNumDto;
import com.cheersmind.cheersgenie.features.dto.CommentDto;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.MineDto;
import com.cheersmind.cheersgenie.features.dto.OpenDimensionDto;
import com.cheersmind.cheersgenie.features.dto.PhoneNumLoginDto;
import com.cheersmind.cheersgenie.features.dto.RegisterDto;
import com.cheersmind.cheersgenie.features.dto.ResetPasswordDto;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.ResponseDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.features.interfaces.ResponseByteCallback;
import com.cheersmind.cheersgenie.features.interfaces.ResponseStringCallback;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.google.gson.JsonArray;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 数据请求服务
 */
public class DataRequestService {

    private static DataRequestService instance;

    private DataRequestService() {
    }

    public static synchronized DataRequestService getInstance() {
        if (instance == null) {
            instance = new DataRequestService();
        }
        return instance;
    }

    /**
     * 通用get请求操作
     * @param url 访问地址
     * @param callback 回调
     */
    private void doGet(String url, final BaseService.ServiceCallback callback, String httpTag) {
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
        }, httpTag);
    }


    /**
     * 通用post请求（参数map）
     * @param url 地址
     * @param params 参数map
     * @param isFormType 是否form
     * @param callback 回调
     * @param httpTag 通信标记
     */
    private void doPost(String url, Map<String,Object> params, boolean isFormType, final BaseService.ServiceCallback callback, String httpTag) {
        BaseService.post(url, params, isFormType, new BaseService.ServiceCallback() {
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
        },httpTag);
    }

    /**
     * 通用post请求（参数json）
     * @param url 地址
     * @param params 参数json
     * @param callback 回调
     * @param httpTag 通信标记
     */
    private void doPost(String url, JSONObject params, final BaseService.ServiceCallback callback, String httpTag) {
        BaseService.post(url, params, new BaseService.ServiceCallback() {
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
        }, httpTag);
    }


    /**
     * 通用post请求（参数map：value是File）
     * @param url 地址
     * @param params 参数map：value是File
     * @param callback 回调
     * @param httpTag 通信标记
     */
    private void doPost(String url, Map<String,File> params, final BaseService.ServiceCallback callback, String httpTag) {
        BaseService.post(url, params, new BaseService.ServiceCallback() {
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
        }, httpTag);
    }


    /**
     * 通用put请求
     * @param url 地址
     * @param params 参数map
     * @param callback 回调
     * @param httpTag 通信标记
     */
    private void doPut(String url, Map<String,Object> params, final BaseService.ServiceCallback callback, String httpTag) {
        BaseService.put(url,params, new BaseService.ServiceCallback() {
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
        }, httpTag);
    }

    /**
     * 通用patch请求
     * @param url 地址
     * @param params 参数map
     * @param callback 回调
     * @param httpTag 通信标记
     */
    private void doPatch(String url, Map<String,Object> params, final BaseService.ServiceCallback callback, String httpTag) {
        BaseService.patch(url,params, new BaseService.ServiceCallback() {
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
        }, httpTag);
    }


    //获取孩子关注主题列表
    public void loadChildTopicList(String childId,int offset, int limit, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CHILD_TOPIC_LIST
                .replace("{child_id}", childId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        doGet(url, callback, httpTag);
    }


    /**
     * 根据状态（未完成、已完成）获取孩子关注主题列表
     * @param childId 孩子ID
     * @param status 状态，0：未完成，1；已完成
     * @param page 页码
     * @param size 页长度
     * @param callback 回调 回调
     */
    public void loadChildTopicListByStatus(String childId,int status, int page, int size, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CHILD_TOPIC_LIST_BY_STATUS
                .replace("{child_id}", childId)
                .replace("{status}", String.valueOf(status))
                .replace("{page}",String.valueOf(page))
                .replace("{size}",String.valueOf(size));
        doGet(url, callback, httpTag);
    }
    

    //获取孩子最后一次使用量表 V2
    public void getLatestDimensionV2(String childId,final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_DIMENSION_LATEST_V2
                .replace("{child_id}",childId);
        doGet(url, callback, httpTag);
    }


    /**
     * 开始孩子某个量表V2
     * @param openDimensionDto dto
     * @param callback 回调
     */
    public void startChildDimensionV2(OpenDimensionDto openDimensionDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CHILD_DIMENSION_START_V2
                .replace("{child_id}",openDimensionDto.getChildId())
                .replace("{exam_id}",openDimensionDto.getExamId())
                .replace("{topic_id}",openDimensionDto.getTopic())
                .replace("{dimension_id}",openDimensionDto.getDimensionId());
        Map<String,Object> map = new HashMap<>();

        doPost(url, map, false, callback, httpTag);
    }


    //获取孩子分量表下的题目列表V2
    public void getChildQuestionsV2(String child_dimension_id,int page,int size,final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CHILD_DIMENSION_QUESTION_V2
                .replace("{child_dimension_id}",child_dimension_id)
                .replace("{page}",String.valueOf(page))
                .replace("{size}",String.valueOf(size));
        doGet(url, callback, httpTag);
    }


    /**
     * 获取主题报告或者量表报告V2
     * @param childId 孩子ID
     * @param examId 测评ID
     * @param relationId 维度ID（必填）topicid,或者dimensionid
     * @param relationType 维度类型（dimension - 主题，topic - 话题 ，必填 ）
     * @param sampleId 样本ID
     * @param callback 回调
     */
    public void getTopicReportByRelation(String childId, String examId,
                                         String relationId, String relationType, String sampleId,
                                         final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_TOPIC_REPORT_V2
                .replace("{exam_id}",examId)
                .replace("{child_id}", childId)
                .replace("{relation_id}", relationId)
                .replace("{relation_type}", relationType)
                .replace("{sample_id}", sampleId);
        doGet(url, callback, httpTag);
    }


    /**
     * 获取公告通知
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void getUpdateNotification(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_UPDATE_NOTIFICATION;
        doGet(url, callback, httpTag);
    }


    /**
     * 获取服务器时间
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void getServerTime(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_SERVER_TIME;
        doGet(url, callback, httpTag);
    }

    /**
     * 获取短信验证码
     * @param dto 传参
     * @param callback 回调
     * @param httpTag 通信标记
     * @throws QSCustomException 自定义异常
     */
    public void postSendMessageCaptcha(MessageCaptchaDto dto, final BaseService.ServiceCallback callback, String httpTag) throws QSCustomException {
        String url = HttpConfig.URL_PHONE_CAPTCHA;
//        {
//            "mobile":"15808082221",         //手机号(必填)，检验手机的合法性
//                "type":"int",                   //短信业务类型(必填)：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
//                "tenant":"string",              //租户名称（选填）
//                "area_code":"string",           //区号（选填），中国：+86（默认）
//                "session_id":"string",          //会话ID（必填）
//                "verification_code":"string"    //验证码
//        }
        Map<String, Object> map = new HashMap<>();
        //手机号
        if (TextUtils.isEmpty(dto.getMobile())) {
            throw new QSCustomException("手机号不能为空");
        }
        map.put("mobile", dto.getMobile());
        //短信业务类型(必填)：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
        map.put("type", dto.getType());
        //租户名
        if (TextUtils.isEmpty(dto.getTenant())) {
            dto.setTenant(Dictionary.Tenant_CheersMind);
        }
        map.put("tenant", dto.getTenant());
        //区号
        if (TextUtils.isEmpty(dto.getAreaCode())) {
            dto.setAreaCode(Dictionary.Area_Code_86);
        }
        map.put("area_code", dto.getAreaCode());
        //会话ID
        if (TextUtils.isEmpty(dto.getSessionId())) {
            throw new QSCustomException("会话ID不能为空");
        }
        map.put("session_id", dto.getSessionId());
        //图形验证码
        if (!TextUtils.isEmpty(dto.getImageCaptcha())) {
            map.put("verification_code", dto.getImageCaptcha());
        }

        doPost(url, map, false, callback, httpTag);
    }

    /**
     * 根据班级号获取班级信息
     * @param classNum 班级号
     * @param callback 回调
     */
    public void getClassInfoByClassNum(String classNum, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CLASS_INFO
                .replace("{group_no}",classNum);
        doGet(url, callback, httpTag);
    }


    /**
     * 绑定手机号
     * @param dto 传参
     * @param callback 回调
     */
    public void putBindPhoneNum(BindPhoneNumDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_BIND_PHONE_NUM;
//        {
//            "mobile":"string",            //手机号（必填）
//                "mobile_code":"string",       //短信验证码（必填）
//                "tenant":"string",            //租户名称（选填）
//                "area_code":"string",     //手机国际区号(选填)，中国：+86（默认）
//                "session_id":"string"     //会话ID
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", dto.getMobile());
        map.put("mobile_code", dto.getMobileCode());
        map.put("tenant", dto.getTenant());
        map.put("area_code", dto.getAreaCode());
        map.put("session_id", dto.getSessionId());

        doPut(url, map, callback, httpTag);
    }


    /**
     * 第三方登录
     * @param thirdLoginDto 传参
     * @param callback 回调
     */
    public void postUcThirdLogin(ThirdLoginDto thirdLoginDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_UC_THIRD_LOGIN_V2;
//        {
//            "open_id":"",                   //第三方平台ID(必填)
//                "plat_source":"",               //微信：weixin,(必填)
//                "third_access_token":"",        //第三方平台token(必填)
//                "app_id":"",                   //QQ平台必填
//                "tenant":"",                    //组织名称(必填)【改为租户命名】
//                "device_type":"string",         //登录设备类型(可选)，如：ios\android\browser
//                "device_desc":"string",         //登录设备机器型号(可选)，如：小米6，苹果8
//                "device_id":""                  //设备唯一ID    (可选)
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("open_id", thirdLoginDto.getOpenId());
        map.put("plat_source", thirdLoginDto.getPlatSource());
        map.put("third_access_token", thirdLoginDto.getThirdAccessToken());
        map.put("tenant", thirdLoginDto.getTenant());
        //我们应用在第三方平台注册的appId
        if (!TextUtils.isEmpty(thirdLoginDto.getAppId())) {
            map.put("app_id", thirdLoginDto.getAppId());
        }
        map.put("device_type", thirdLoginDto.getDeviceType());
        map.put("device_desc", thirdLoginDto.getDeviceDesc());
        map.put("device_id", thirdLoginDto.getDeviceId());

        doPost(url, map, false, callback, httpTag);
    }


    /**
     * 手机号登录（“手机号登录”都指的是手机短信登录）
     * @param phoneNumLoginDto 传参
     * @param callback 回调
     */
    public void postPhoneNumLogin(PhoneNumLoginDto phoneNumLoginDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_PHONE_NUM_LOGIN;
//        {
//            "mobile":"string",          //手机号（必填）
//                "mobile_code":"string",     //短信验证码（必填）
//                "tenant":"string",          //租户名称（必填）
//                "device_type":"string",      //登录设备类型(可选)，如：ios\android\browser
//                "device_desc":"string",     //登录设备机器型号(可选)，如：小米6，苹果8
//                "device_id":"string",         //设备ID（必填）
//                "session_id":"string"       //会话ID
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", phoneNumLoginDto.getMobile());
        map.put("mobile_code", phoneNumLoginDto.getMobile_code());
        map.put("tenant", phoneNumLoginDto.getTenant());
        map.put("device_type", phoneNumLoginDto.getDeviceType());
        map.put("device_desc", phoneNumLoginDto.getDeviceDesc());
        map.put("device_id", phoneNumLoginDto.getDeviceId());
        map.put("session_id", phoneNumLoginDto.getSessionId());

        doPost(url, map, false, callback, httpTag);
    }

    /**
     * 账号登录（用户名可以是手机号）
     * @param accountLoginDto 传参
     * @param callback 回调
     */
    public void postAccountLogin(AccountLoginDto accountLoginDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_ACCOUNT_LOGIN;
//        {
//            "account":"",               //用户名或工号
//                "password":"",              //密码(需要加密)
//                "tenant":"",                //组织登录名称，【改为租户命名】
//                "device_type":"string",     //登录设备类型(可选)，如：ios\android\browser
//                "device_desc":"string",     //登录设备机器型号(可选)，如：小米6，苹果8
//                "device_id":"",            //设备唯一ID (可选)
//                "session_id":"",            //会话ID
//                "verification_code":""       //验证码
//        }
        //密码加密
        String pwdMd5 = EncryptUtil.encryptMD5_QS(accountLoginDto.getPassword());
        Map<String, Object> map = new HashMap<>();
        map.put("account", accountLoginDto.getAccount());
        map.put("password", pwdMd5);
        map.put("session_id", accountLoginDto.getSessionId());
        //图形验证码
        if (!TextUtils.isEmpty(accountLoginDto.getVerificationCode())) {
            map.put("verification_code", accountLoginDto.getVerificationCode());
        }
        map.put("tenant", accountLoginDto.getTenant());
        map.put("device_type", accountLoginDto.getDeviceType());
        map.put("device_desc", accountLoginDto.getDeviceDesc());
        map.put("device_id", accountLoginDto.getDeviceId());

        doPost(url, map, false, callback, httpTag);
    }


    /**
     * 账号注册
     * @param dto 传参
     * @param callback 回调
     */
    public void postRegister(RegisterDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_PHONE_MESSAGE_REGISTER;
//        {
//            "mobile":"string",            //手机号(必填)
//                "mobile_code":"string",       //手机验证码(必填)，
//                "password":"string",      //登录密码(必填)
//                "area_code":"string",     //手机国际区号(选填)，中国：+86（默认）
//                "tenant":"string",        //租户名称（必填）
//                "open_id":"",             //第三方平台ID(第三方+手机号时必填)
//                "app_id":"",              //QQ平台必填
//                "plat_source":"",         //微信：weixin,(第三方+手机号时必填)
//                "third_access_token":"",   //第三方平台token(第三方+手机号时必填)
//                "session_id":"string"       //会话ID（必填）
//        }

        Map<String, Object> map = new HashMap<>();
        map.put("mobile", dto.getMobile());
        map.put("mobile_code", dto.getMobileCode());
        //密码加密
        String pwdMd5 = EncryptUtil.encryptMD5_QS(dto.getPassword());
        map.put("password", pwdMd5);
        map.put("tenant", dto.getTenant());
        //第三方登录的初次注册
        ThirdLoginDto thirdLoginDto = dto.getThirdLoginDto();
        if (thirdLoginDto != null) {
            map.put("open_id",thirdLoginDto.getOpenId());
            map.put("plat_source",thirdLoginDto.getPlatSource());
            map.put("third_access_token",thirdLoginDto.getThirdAccessToken());
            //我们的应用在第三方平台注册的ID
            if (!TextUtils.isEmpty(thirdLoginDto.getAppId())) {
                map.put("app_id", thirdLoginDto.getAppId());
            }
        }
        map.put("session_id", dto.getSessionId());

        doPost(url, map, false, callback, httpTag);
    }
    

    /**
     * 获取文章列表
     * @param dto 文章dto
     * @param callback 回调 回调
     * @param httpTag 通信标记
     */
    public void getArticles(ArticleDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_ARTICLES;

        Map<String, Object> params = new HashMap<>();
        params.put("page", dto.getPage());
        params.put("size", dto.getSize());
        //分类
        if (!TextUtils.isEmpty(dto.getCategoryId())) {
            params.put("category_id", dto.getCategoryId());
        }
        //搜索文本
        if (!TextUtils.isEmpty(dto.getFilter())) {
            try {
                //输入中文的地方加上 URLEncoder.encode 处理
                String encode = URLEncoder.encode(dto.getFilter(), "utf-8");
                params.put("filter", encode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        //拼接参数
        url = BaseService.settingGetParams(url, params);

        doGet(url, callback, httpTag);
    }

    /**
     * 获取热门文章列表
     * @param baseDto 通用dto
     * @param callback 回调
     */
    public void getHotArticles(BaseDto baseDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_HOT_ARTICLES
                .replace("{page}", baseDto.getPage() +"")
                .replace("{size}", baseDto.getSize() + "");
//        参数：page、size

        doGet(url, callback, httpTag);
    }

    /**
     * 获取我的收藏
     * @param dto “我的”dto
     * @param callback 回调
     */
    public void getMyFavorite(MineDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_MINE_FAVORITE
                .replace("{user_id}", dto.getUserId() +"")
                .replace("{page}", dto.getPage() +"")
                .replace("{size}", dto.getSize() + "");

        doGet(url, callback, httpTag);
    }

    /**
     * 获取文章详情
     * @param articleId 文章ID
     * @param callback 回调
     */
    public void getArticleDetail(String articleId, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_ARTICLE_DETAIL
                .replace("{articleId}", articleId);

        doGet(url, callback, httpTag);
    }

    /**
     * 获取评论列表
     * @param dto 评论dto
     * @param callback 回调
     */
    public void getCommentList(CommentDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_ARTICLE_COMMENT
                .replace("{id}", dto.getId())
                .replace("{type}", dto.getType()+"")
                .replace("{page}", dto.getPage() +"")
                .replace("{size}", dto.getSize() + "");

        doGet(url, callback, httpTag);
    }
    

    /**
     * 收藏
     * @param articleId 文章ID
     * @param callback 回调 回调
     * @param httpTag 通信标记
     */
    public void postDoFavorite(String articleId, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_FAVORITE
                .replace("{articleId}", articleId);

        doPost(url, new HashMap<String, Object>(), false, callback, httpTag);
    }

    /**
     * 点赞
     * @param articleId 文章ID
     * @param callback 回调
     */
    public void postDoLike(String articleId, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_LIKE
                .replace("{articleId}", articleId);

        doPost(url, new HashMap<String, Object>(), false, callback, httpTag);
    }


    /**
     * 提交评论
     * @param articleId 文章ID
     * @param content 评论内容
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void postDoComment(String articleId, String content, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_DO_COMMENT
                .replace("{articleId}", articleId);
//        {
//            "comment_info":""
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("comment_info", content);

        doPost(url, map, false, callback, httpTag);
    }


    /**
     * 提交答题答案（分量表下的所有问题）
     * @param childDimensionId 孩子量表ID
     * @param costedTime 耗时
     * @param answerDtos 答案dto
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void postQuestionAnswersSubmit(String childDimensionId, int costedTime,
                                          Collection<AnswerDto> answerDtos, final BaseService.ServiceCallback callback,
                                          String httpTag){
        String url = HttpConfig.URL_QUESTIONS_ANSWERS_SUBMIT
                .replace("{child_dimension_id}",childDimensionId);
        Map<String,Object> map = new HashMap<>();
        map.put("costed_time",costedTime);
        JsonArray asJsonArray = JsonUtil.toJsonArray(answerDtos);
        map.put("answers",asJsonArray);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("costed_time", costedTime);
            JSONArray jsonArray = new JSONArray(asJsonArray.toString());
            jsonObject.put("answers", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        doPost(url, jsonObject, callback, httpTag);
    }


    /**
     * 保存答题答案（分量表下的部分问题）
     * @param childDimensionId 孩子量表ID
     * @param costedTime 耗时
     * @param answerDtos 答案dto
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void postQuestionAnswersSave(String childDimensionId, int costedTime,
                                        Collection<AnswerDto> answerDtos, final BaseService.ServiceCallback callback,
                                        String httpTag){
        String url = HttpConfig.URL_QUESTIONS_ANSWERS_SAVE
                .replace("{child_dimension_id}",childDimensionId);
        Map<String,Object> map = new HashMap<>();
        map.put("costed_time",costedTime);
        JsonArray asJsonArray = JsonUtil.toJsonArray(answerDtos);
        map.put("answers",asJsonArray);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("costed_time", costedTime);
            JSONArray jsonArray = new JSONArray(asJsonArray.toString());
            jsonObject.put("answers", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        doPost(url, jsonObject, callback, httpTag);
    }

    /**
     * 获取用户详情V2
     * @param callback 回调
     */
    public void getUserInfoV2 (final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_USER_INFO_V2;
        doGet(url, callback, httpTag);
    }

    /**
     * 获取孩子列表V2
     * @param callback 回调
     */
    public void getChildListV2(final BaseService.ServiceCallback callback, String httpTag) {
        String url = HttpConfig.URL_CHILD_LIST_V2;
        doGet(url, callback, httpTag);
    }


    /**
     * 获取孩子的量表对象
     * @param childId 孩子ID
     * @param topicId 话题ID
     * @param dimensionId 量表ID
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void getChildDimension(String childId, String topicId, String dimensionId,
                                  final BaseService.ServiceCallback callback, String httpTag) {
        String url = HttpConfig.URL_CHILD_DIMENSION
                .replace("{children}", childId)
                .replace("{topics}", topicId)
                .replace("{dimensions}", dimensionId);

        doGet(url, callback, httpTag);
    }


    /**
     * 修改密码
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param callback 回调
     * @param httpTag 通信标记
     */
    public void patchModifyPassword(String passwordOld, String passwordNew,
                                    final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_MODIFY_PASSWORD;
//        {
//            "old_password":"",      //旧密码，加密
//                "new_password":""       //新密码，加密
//        }
        String passwordOldMd5 = EncryptUtil.encryptMD5_QS(passwordOld);
        String passwordNewMd5 = EncryptUtil.encryptMD5_QS(passwordNew);

        Map<String, Object> map = new HashMap<>();
        map.put("old_password", passwordOldMd5);
        map.put("new_password", passwordNewMd5);

        doPatch(url, map, callback, httpTag);
    }


    /**
     * 重置密码
     * @param dto 传参
     * @param callback 回调
     */
    public void patchResetPassword(ResetPasswordDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_RESET_PASSWORD;
//        {
//            "mobile":"string",            //手机号（必填）
//                "mobile_code":"string",        //短信验证码（必填）
//                "tenant":"string",            //租户名称（选填）
//                "new_password":"string",       //密码（加密后）
//                "area_code":"string",         //手机国际区号(选填)，中国：+86（默认）
//                "session_id":"string"     //会话ID
//        }
        String passwordNewMd5 = EncryptUtil.encryptMD5_QS(dto.getNew_password());

        Map<String, Object> map = new HashMap<>();
        map.put("mobile", dto.getMobile());
        map.put("mobile_code", dto.getMobile_code());
        map.put("tenant", dto.getTenant());
        map.put("new_password", passwordNewMd5);
        map.put("area_code", dto.getArea_code());
        map.put("session_id", dto.getSessionId());

        doPatch(url, map, callback, httpTag);
    }


    /**
     * 查询已绑定的第三方平台
     * @param callback 回调
     */
    public void getThirdBindPlatform(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_THIRD_PLATPORM
                .replace("{tenant}", Dictionary.Tenant_CheersMind);

        doGet(url, callback, httpTag);
    }


    /**
     * 第三方平台账号绑定
     * @param bindDto 传参
     * @param callback 回调
     */
    public void postThirdPlatBind(ThirdPlatBindDto bindDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_BIND_THIRD_PLATPORM;
//        {
//            "open_id":"",               //第三方平台给用户分配的Id,如微信、QQ的openId
//                "plat_source":"",           //第三方登录来源，目前支持：1-qq 2-weixin
//                "app_id":"",                //QQ平台必填
//                "third_access_token":""      //第三方的access_token
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("open_id", bindDto.getOpenId());
        map.put("plat_source", bindDto.getPlatSource());
        map.put("third_access_token", bindDto.getThirdAccessToken());
        //我们应用在第三方平台注册的appId
        if (!TextUtils.isEmpty(bindDto.getAppId())) {
            map.put("app_id", bindDto.getAppId());
        }
        map.put("tenant", bindDto.getTenant());

        doPost(url, map, false, callback, httpTag);
    }


    /**
     * 第三方平台账号解绑
     * @param bindDto 传参
     * @param callback 回调
     */
    public void postThirdPlatUnbind(ThirdPlatBindDto bindDto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_UNBIND_THIRD_PLATPORM;
//        {
//            "open_id":"",               //第三方平台给用户分配的Id,如微信、QQ的openId
//                "source_plat":"qq",          //第三方登录来源，目前支持：1-qq 2-weixin
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("open_id", bindDto.getOpenId());
        map.put("plat_source", bindDto.getPlatSource());

        doPost(url, map, false, callback, httpTag);
    }

    /**
     * 获取分类列表
     * @param callback 回调
     */
    public void getCategories(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CATEGORIES;
        doGet(url, callback, httpTag);
    }

    /**
     * 查询当前签到状态
     * @param callback 回调
     */
    public void getDailySignInStatus(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_DAILY_SIGN_IN_STATUS;
        doGet(url, callback, httpTag);
    }

    /**
     * 签到
     * @param callback 回调
     */
    public void postDailySignIn(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_DAILY_SIGN_IN;
        doPost(url, new HashMap<String, Object>(), false, callback, httpTag);
    }

    /**
     * 获取总积分
     * @param callback 回调
     */
    public void getIntegralTotalScore(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_INTEGRAL_TOTAL_SCORE;
        doGet(url, callback, httpTag);
    }

    /**
     * 获取积分列表
     * @param callback 回调
     */
    public void getIntegralList(int page, int size, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_INTEGRALS
                .replace("{page}", page + "")
                .replace("{size}", size + "");
        doGet(url, callback, httpTag);
    }
    

    /**
     * 获取报告V2
     * @param childExamId 孩子测评ID
     * @param relationId 维度ID（必填）topicid,或者topic_dimension_id
     * @param relationType 维度类型（topic_dimension - 话题下的主题，topic - 话题 ，必填 ）
     * @param compareId 对比样本ID( 0-全国，1- 八大区，2-省，3-市，4-区，5-学校，6-年级，7-班级)
     * @param httpTag 通信标记
     * @param callback 回调 回调
     */
    public void getReportV2(String childExamId,String relationId,String relationType,int compareId,final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_REPORT_V2
                .replace("{child_exam_id}",childExamId)
                .replace("{relation_id}", relationId)
                .replace("{relation_type}", relationType)
                .replace("{compare_id}", compareId +"");
        doGet(url, callback, httpTag);
    }

    /**
     * 获取报告推荐文章
     * @param childExamId 孩子测评ID
     * @param relationId 维度ID（必填）topicid,或者topic_dimension_id
     * @param relationType 维度类型（topic_dimension - 话题下的主题，topic - 话题 ，必填 ）
     * @param compareId 对比样本ID( 0-全国，1- 八大区，2-省，3-市，4-区，5-学校，6-年级，7-班级)
     * @param httpTag 通信标记
     * @param callback 回调 回调
     */
    public void getReportRecommendArticle(String childExamId,String relationId,String relationType,int compareId,
                                          final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_REPORT_RECOMMEND_ARTICLE
                .replace("{child_exam_id}",childExamId)
                .replace("{relation_id}", relationId)
                .replace("{relation_type}", relationType)
                .replace("{compare_id}", compareId +"");
        doGet(url, callback, httpTag);
    }


    /**
     * 获取话题的历史报告
     * @param topicId 话题ID
     * @param childId 孩子ID
     * @param callback 回调
     */
    public void getHistoryReport(String topicId,String childId,
                                 final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_HISTORY_REPORT
                .replace("{topic_id}",topicId)
                .replace("{child_id}", childId);
        doGet(url, callback, httpTag);
    }


    /**
     * 获取话题详情
     * @param topicId 话题ID
     * @param callback 回调
     */
    public void getTopicDetail(String topicId,final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_TOPIC_INFO
                .replace("{topic_id}",topicId);
        doGet(url, callback, httpTag);
    }

    /**
     * 获取消息列表
     * @param page 页码
     * @param size 页长
     * @param callback 回调
     */
    public void getMessage(int page, int size, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_MESSAGE
                .replace("{page}", page + "")
                .replace("{size}", size + "");
        doGet(url, callback, httpTag);
    }


    /**
     * 获取新消息条数
     * @param callback 回调
     */
    public void getNewMessageCount(final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_NEW_MESSAGE_COUNT;
        doGet(url, callback, httpTag);
    }


    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @param callback 回调
     */
    public void putMarkRead(long messageId, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_MARK_READ
                .replace("{message_id}", messageId +"");
        doPut(url, new HashMap<String, Object>(), callback, httpTag);
    }


    /**
     * 创建会话
     * @param dto 传参
     * @param callback 回调
     */
    public void postAccountsSessions(CreateSessionDto dto, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_CREATE_SESSION;
//        {
//            "session_type":"int",   //会话类型，0：注册(手机)，1：登录(帐号、密码登录)，2：手机找回密码，3：登录(短信登录)，4:下发短信验证码
//                "device_id":"string",   //设备唯一ID
//                "tenant":"string"       //租户名称（选填）
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("session_type", dto.getSessionType());
        map.put("device_id", dto.getDeviceId());
        map.put("tenant", dto.getTenant());

        doPost(url, map, false, callback, httpTag);
    }


    /**
     * 获取图形验证码
     * @param sessionId 会话ID
     */
    public void getImageCaptcha(String sessionId, final BaseService.ServiceCallback callback, String httpTag) {
        String url = HttpConfig.URL_IMAGE_CAPTCHA
                .replace("{session_id}", sessionId);

        OkHttpUtils
                .get()
                .url(url)
                .tag(httpTag)
//                .addParams("username", "hyman")
                .build()
                .execute(new ResponseByteCallback() {
                    @Override
                    public void onError(Call call,final Exception e, int id) {
                        if (call.isCanceled()) return;
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

                    @Override
                    public void onResponse(final ResponseDto dto, int id) {

                        if (callback != null) {
                            //返回结果信息
                            final byte[] bodyBytes = dto.getData();

                            QSApplication.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    int code = dto.getCode();
                                    //目前的业务错误机制：只要业务验证发生任何错误，http的code都不会是200出头的正确应答code，
                                    // 而只会返回什么400+，500+之类的，并且附带错误信息串
                                    if (code == 200 || code == 201) {
                                        if (bodyBytes.length == 0) {
                                            callback.onFailure(new QSCustomException("获取验证码失败"));
                                            return;
                                        }
                                        callback.onResponse(bodyBytes);

                                    } else if (code == 400) {
                                        try {
                                            JSONObject respObj = new JSONObject(new String(bodyBytes));
                                            callback.onFailure(new QSCustomException(respObj.toString()));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            callback.onFailure(new QSCustomException("服务器返回数据异常"));
                                        }

                                    } else {
                                        callback.onFailure(new QSCustomException("获取验证码失败."));
                                    }
                                }
                            });
                        }
                    }
                });
    }
    /*public void getImageCaptcha(String sessionId, final BaseService.ServiceCallback callback) {
        String url = HttpConfig.URL_IMAGE_CAPTCHA
                .replace("{session_id}", sessionId);
        Request request = new Request.Builder()
                .url(url)
                .build();
        //创建okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //请求超时设置
        okHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
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

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (callback != null) {
                    try {
                        //返回结果信息
                        final byte[] bodyBytes = response.body().bytes();

                        QSApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                //目前的业务错误机制：只要业务验证发生任何错误，http的code都不会是200出头的正确应答code，
                                // 而只会返回什么400+，500+之类的，并且附带错误信息串
                                if (response.code() == 200 || response.code() == 201) {
                                    if (bodyBytes.length == 0) {
                                        callback.onFailure(new QSCustomException("获取验证码失败"));
                                    }
                                    callback.onResponse(bodyBytes);

                                } else if (response.code() == 400) {
                                    JSONObject respObj = null;
                                    try {
                                        respObj = new JSONObject(new String(bodyBytes));
                                        callback.onResponse(respObj);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        callback.onFailure(new QSCustomException("服务器返回数据异常"));
                                    }

                                } else {
                                    callback.onFailure(new QSCustomException("获取验证码失败"));
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onFailure(new QSCustomException("返回数据异常"));
                    }
                }
            }

        });
    }*/


    /**
     * 获取用户的手机号
     * @param callback 回调
     */
    public void getUserPhoneNum (final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_USER_PHONE_NUM;
        doGet(url, callback, httpTag);
    }


    /**
     * 修改用户头像
     * @param file 图片文件
     * @param callback 回调
     */
    public void postModifyProfile(File file, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_MODIFY_PROFILE;

//        （multipart/form-data）
//        {
//            "image"：图片文件
//        }

        Map<String, File> map = new HashMap<>();
        map.put("image", file);

        doPost(url, map, callback, httpTag);
    }

    /**
     * 获取微信token
     * @param appId appId
     * @param appSecret appSecret
     * @param code code
     * @param callback 回调
     */
    public void getWeChartToken(String appId,String appSecret,String code,final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_WX_GET_TOKEN
                .replace("{appid}", appId)
                .replace("{secret}", appSecret)
                .replace("{code}", code);

        doGet(url, callback, httpTag);
    }


    /**
     * 修改昵称
     * @param nickname 昵称
     * @param callback 回调
     */
    public void patchModifyNickname(String nickname, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_MODIFY_USER_INFO;
//        {
//            "nick_name":"string"        //用户昵称（必填），3-8位，数字、英文、中文
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("nick_name", nickname);

        doPatch(url, map, callback, httpTag);
    }


    /**
     * 获取视频真实地址
     * @param videoId 视频ID
     * @param sign 签名
     * @param curTimestamp 当前时间戳
     * @param callback 回调 回调
     */
    public void getVideoRealUrl(String videoId, String sign, String curTimestamp,
                                final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_VIDEO_REAL_URL
                .replace("{video_id}",videoId)
                .replace("{sign}",sign)
                .replace("{t}",curTimestamp);

        doGet(url, callback, httpTag);
    }


    /**
     * 获取任务列表
     * @param childId 孩子ID
     * @param callback 回调 回调
     * @param httpTag 通信标记
     */
    public void getTaskList(String childId, final BaseService.ServiceCallback callback, String httpTag){
        String url = HttpConfig.URL_TASK_LIST
                .replace("{child_id}",childId);
        doGet(url, callback, httpTag);
    }

}
