package com.cheersmind.cheersgenie.main.service;

import android.text.TextUtils;
import android.util.Log;

import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.AccountLoginDto;
import com.cheersmind.cheersgenie.features.dto.AnswerDto;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features.dto.CommentDto;
import com.cheersmind.cheersgenie.features.dto.MineDto;
import com.cheersmind.cheersgenie.features.dto.OpenDimensionDto;
import com.cheersmind.cheersgenie.features.dto.PhoneNumLoginDto;
import com.cheersmind.cheersgenie.features.dto.ResetPasswordDto;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
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
 * Created by Administrator on 2017/10/28.
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

    //获取首页推荐列表
    public void loadMainRecommend(String childId,String topicId,String examId,int offset,int limit,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_RECOMMEND_MAIN
                .replace("{child_id}",childId)
                .replace("{topic_id}",topicId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit))
                .replace("{exam_id}",examId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取基础主题列表
    public void loadBaseTopicList(int offset, int limit, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_TOPIC_LIST
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取孩子关注主题列表
    public void loadChildTopicList(String childId,int offset, int limit, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_TOPIC_LIST
                .replace("{child_id}", childId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 根据状态（未完成、已完成）获取孩子关注主题列表
     * @param childId 孩子ID
     * @param status 状态，0：未完成，1；已完成
     * @param page 页码
     * @param size 页长度
     * @param callback 回调
     */
    public void loadChildTopicListByStatus(String childId,int status, int page, int size, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_TOPIC_LIST_BY_STATUS
                .replace("{child_id}", childId)
                .replace("{status}", String.valueOf(status))
                .replace("{page}",String.valueOf(page))
                .replace("{size}",String.valueOf(size));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    //获取孩子关注主题列表（报告表头使用）
    public void loadChildTopicListReport(String childId,int offset, int limit, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_TOPIC_LIST_REPORT
                .replace("{child_id}", childId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取主题详情
    public void loadTopicInfo(String childId,String topicId){
        String url = HttpConfig.URL_CHILD_TOPIC_INFO.replace("{child_id}",childId).replace("{topic_id}",topicId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TopicInfoEntity entity = InjectionWrapperUtil.injectMap(dataMap, TopicInfoEntity.class);
                    if(entity!=null){

                    }
                }
            }
        });
    }

    //开始主题
    public void startTopic(String childId, String topicId,String examId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_TOPIC_FOLLOW
                .replace("{child_id}",childId)
                .replace("{topic_id}",topicId)
                .replace("{exam_id}",examId);
        Map<String,Object> map = new HashMap<>();
        BaseService.post(url, map,false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取主题下基础量表列表
    public void getBaseDimensions(String topicId, int offset, int limit, final BaseService.ServiceCallback callback) {
        String url = HttpConfig.URL_DIMENSION_LIST
                .replace("{topic_id}", topicId)
                .replace("{page}", String.valueOf(offset))
                .replace("{size}", String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取主题下孩子量表列表(带基础数据)
    public void getChildDimensions(String childId, String topicId,String examId, int offset, int limit, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_DIMENSION_LIST
                .replace("{child_id}",childId)
                .replace("{topic_id}",topicId)
                .replace("{exam_id}",examId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });

    }

    //获取所有主题下所有量表列表
    public void getAllChildDimensions(String childId,int offset,int limit,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_DIMENSION_LIST_ALL
                .replace("{child_id}",childId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });

    }

    //获取热门量表
    public void getHotDimension(String childId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DIMENSION_HOTE_LIST.replace("{child_id}",childId)
                .replace("{exam_id}", "0");
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });

    }

    //获取孩子最后一次使用量表
    public void getLatestDimension(String childId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DIMENSION_LATEST
                .replace("{child_id}",childId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取孩子最后一次使用量表 V2
    public void getLatestDimensionV2(String childId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DIMENSION_LATEST_V2
                .replace("{child_id}",childId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //开始孩子某个量表
    public void startChidlDimension(String childId,String topicId, String dimensionId,String examId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FDIMENSION_START
                .replace("{child_id}",childId)
                .replace("{topic_id}",topicId)
                .replace("{dimension_id}",dimensionId)
                .replace("{exam_id}",examId);
        Map<String,Object> map = new HashMap<>();
        BaseService.post(url, map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 开始孩子某个量表V2
     * @param openDimensionDto
     * @param callback
     */
    public void startChildDimensionV2(OpenDimensionDto openDimensionDto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_DIMENSION_START_V2
                .replace("{child_id}",openDimensionDto.getChildId())
                .replace("{exam_id}",openDimensionDto.getExamId())
                .replace("{topic_id}",openDimensionDto.getTopic())
                .replace("{dimension_id}",openDimensionDto.getDimensionId());
        Map<String,Object> map = new HashMap<>();

        BaseService.post(url, map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取基础因子列表
    public void getBaseFactorList(String dimensionId,int offset,int limit,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_FACTOR_LIST
                .replace("{dimension_id}",dimensionId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });

    }

    //获取孩子量表下因子列表
    public void getFactorList(String childId, String dimensionId,String childDimensionId, int offset, int limit, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FACTOR_LIST
                .replace("{child_id}",childId)
                .replace("{dimension_id}",dimensionId)
                .replace("{child_dimension_id}",childDimensionId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //开始孩子因子测评
    public void startChildFactor(String childId,String topicId,String dimensionId,String factorId,String examId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FACTOR_START
                .replace("{child_id}",childId)
                .replace("{exam_id}",examId)
                .replace("{topic_id}",topicId)
                .replace("{dimension_id}",dimensionId)
                .replace("{factor_id}",factorId);
        Map<String,Object> map = new HashMap<>();
        BaseService.post(url, map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取因子下基础题目列表
    public void getBaseQuestions(String factorId,int offset,int limit,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_QUESTION_LIST
                .replace("{factor_id}",factorId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });

    }

    //获取孩子因子题目列表
    public void getChildQuestions(String childId,String factorId,String childFactorId,int offset,int limit,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FACTOR_QUESTION
                .replace("{child_id}",childId)
                .replace("{factor_id}",factorId)
                .replace("{child_factor_id}",childFactorId)
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取孩子分量表下的题目列表V2
    public void getChildQuestionsV2(String child_dimension_id,int page,int size,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_DIMENSION_QUESTION_V2
                .replace("{child_dimension_id}",child_dimension_id)
                .replace("{page}",String.valueOf(page))
                .replace("{size}",String.valueOf(size));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 提交答题答案
     * @param childId
     * @param questionId
     * @param optionId
     * @param childFactorId
     * @param callback
     */
    public void commitQuestionSingle(String childId,String questionId,String optionId,String childFactorId,String optionText,int costedTime,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_QUESTION_SAVE_SINGLE
                .replace("{child_id}",childId)
                .replace("{question_id}",questionId);
        Map<String,Object> map = new HashMap<>();
        map.put("option_id",optionId);
        map.put("child_factor_id",childFactorId);
        map.put("option_text",optionText);
        map.put("costed_time",costedTime);
        BaseService.post(url, map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 提交某个孩子因子测评
     * @param childId
     * @param childFactorId
     * @param costedTime
     * @param costedTimeToken
     * @param callback
     */
    public void commitChildFactor(String childId,String childFactorId,int costedTime,String costedTimeToken,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FACTOR_COMMIT
                .replace("{child_id}",childId)
                .replace("{child_factor_id}",childFactorId);
        Map<String,Object> map = new HashMap<>();
        map.put("costed_time",String.valueOf(costedTime));
        map.put("costed_time_token",costedTimeToken);
        BaseService.post(url, map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取孩子因子排行数据
    public void getChildFactorRank(String childId,String topicId,String dimensionId,String factorId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FACTOR_RANKING
                .replace("{child_id}",childId)
                .replace("{topic_id}",topicId)
                .replace("{dimension_id}",dimensionId)
                .replace("{factor_id}",factorId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取孩子的因子统计报表数据
    public void getChildFactorRankReport(String childId,String childFactorId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CHILD_FACTOR_RANK_REPORT
                .replace("{child_id}",childId)
                .replace("{child_factor_id}",childFactorId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取孩子鲜花消费记录
    public void getChildFlowerRecord(int offset, int limit, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_FLOWER_CONSUME_RECORD
                .replace("{page}",String.valueOf(offset))
                .replace("{size}",String.valueOf(limit));
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取用户详情
    public void getUserDetails(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_USER_DETAILS;
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    //获取用户项目列表
    public void getUserProject(String childId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_PROJECTS_LIST .replace("{child_id}",childId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取主题报告(带量表报告）
     * @param examId
     * @param topicId
     */
    public void getTopicReport(String childId,String examId,String topicId,String sampleId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_TOPIC_REPORT
                .replace("{exam_id}",examId)
                .replace("{child_id}", childId)
                .replace("{topic_id}",topicId)
                .replace("{sample_id}",sampleId);
        Log.i("report_url:",url);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取主题报告或者量表报告V2
     * @param childId
     * @param examId
     * @param relationId 维度ID（必填）topicid,或者dimensionid
     * @param relationType 维度类型（dimension - 主题，topic - 话题 ，必填 ）
     * @param sampleId
     * @param callback
     */
    public void getTopicReportByRelation(String childId,String examId,String relationId,String relationType,String sampleId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_TOPIC_REPORT_V2
                .replace("{exam_id}",examId)
                .replace("{child_id}", childId)
                .replace("{relation_id}", relationId)
                .replace("{relation_type}", relationType)
                .replace("{sample_id}", sampleId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    public void getDimensionReport(String childId,String examId,String dimensionId,String sampleId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DIMENSION_REPORT
                .replace("{exam_id}",examId)
                .replace("{child_id}", childId)
                .replace("{dimension_id}",dimensionId)
                .replace("{sample_id}",sampleId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    public void getUpdateNotification(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_UPDATE_NOTIFICATION;
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    public void getServerTime(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_SERVER_TIME;
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取手机验证码
     * @param phoneNum 手机号
     * @param smsType 短信业务类型(必填)：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
     * @param callback
     */
    public void postRegisterCaptcha(String phoneNum,final int smsType, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_PHONE_CAPTCHA;
//        {
//            "mobile":"15808082221",         //手机号(必填)，检验手机的合法性
//                "type":"int",                   //短信业务类型(必填)：0:注册用户，1：短信登录
//                "tenant":"string",              //租户名称（选填）
//                "area_code":"string"            //区号（选填），中国：+86（默认）
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", phoneNum);
        map.put("type", smsType);
        map.put("tenant", Dictionary.Tenant_CheersMind);
        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 根据班级号获取班级信息
     * @param classNum
     * @param callback
     */
    public void getClassInfoByClassNum(String classNum, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CLASS_INFO
                .replace("{group_no}",classNum);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 绑定手机号
     * @param phoneNum 手机号
     * @param captcha 验证码
     * @param tenant 承租人
     * @param areaCode 手机号区域码
     * @param callback 回调
     */
    public void putBindPhoneNum(String phoneNum, String captcha, String tenant, String areaCode, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_BIND_PHONE_NUM;
//        {
//            "mobile":"string",            //手机号（必填）
//                "mobile_code":"string",       //短信验证码（必填）
//                "tenant":"string",            //租户名称（选填）
//                "area_code":"string"      //手机国际区号(选填)，中国：+86（默认）
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", phoneNum);
        map.put("mobile_code", captcha);
        map.put("tenant", tenant);
//        map.put("area_code", areaCode);
        BaseService.put(url,map, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 第三方登录
     * @param thirdLoginDto
     * @param callback
     */
    public void postUcThirdLogin(ThirdLoginDto thirdLoginDto, final BaseService.ServiceCallback callback){
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
        Map<String, Object> map = new HashMap<String, Object>();
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

        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 手机号登录（“手机号登录”都指的是手机短信登录）
     * @param phoneNumLoginDto
     * @param callback
     */
    public void postPhoneNumLogin(PhoneNumLoginDto phoneNumLoginDto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_PHONE_NUM_LOGIN;
//        {
//            "mobile":"string",          //手机号（必填）
//                "mobile_code":"string",     //短信验证码（必填）
//                "tenant":"string",          //租户名称（选填）
//                "device_type":"string",      //登录设备类型(可选)，如：ios\android\browser
//                "device_desc":"string",     //登录设备机器型号(可选)，如：小米6，苹果8
//                "device_id":"string"         //设备ID
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", phoneNumLoginDto.getMobile());
        map.put("mobile_code", phoneNumLoginDto.getMobile_code());
        map.put("tenant", phoneNumLoginDto.getTenant());
        map.put("device_type", phoneNumLoginDto.getDeviceType());
        map.put("device_desc", phoneNumLoginDto.getDeviceDesc());
        map.put("device_id", phoneNumLoginDto.getDeviceId());

        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 账号登录（用户名可以是手机号）
     * @param accountLoginDto
     * @param callback
     */
    public void postAccountLogin(AccountLoginDto accountLoginDto, final BaseService.ServiceCallback callback){
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
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", accountLoginDto.getAccount());
        map.put("password", pwdMd5);
        map.put("tenant", accountLoginDto.getTenant());
        map.put("device_type", accountLoginDto.getDeviceType());
        map.put("device_desc", accountLoginDto.getDeviceDesc());
        map.put("device_id", accountLoginDto.getDeviceId());

        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取文章列表
     * @param dto 文章dto
     * @param callback
     */
    public void getArticles(ArticleDto dto, final BaseService.ServiceCallback callback){
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
            params.put("filter", dto.getFilter());
        }

        //拼接参数
        url = BaseService.settingGetParams(url, params);

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取热门文章列表
     * @param baseDto 通用dto
     * @param callback
     */
    public void getHotArticles(BaseDto baseDto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_HOT_ARTICLES
                .replace("{page}", baseDto.getPage() +"")
                .replace("{size}", baseDto.getSize() + "");
//        参数：page、size

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取我的收藏
     * @param dto “我的”dto
     * @param callback
     */
    public void getMyFavorite(MineDto dto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_MINE_FAVORITE
                .replace("{user_id}", dto.getUserId() +"")
                .replace("{page}", dto.getPage() +"")
                .replace("{size}", dto.getSize() + "");

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取文章详情
     * @param articleId 文章ID
     * @param callback
     */
    public void getArticleDetail(String articleId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_ARTICLE_DETAIL
                .replace("{articleId}", articleId);

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取评论列表
     * @param dto 评论dto
     * @param callback
     */
    public void getCommentList(CommentDto dto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_ARTICLE_COMMENT
                .replace("{id}", dto.getId())
                .replace("{type}", dto.getType()+"")
                .replace("{page}", dto.getPage() +"")
                .replace("{size}", dto.getSize() + "");

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 收藏
     * @param articleId 文章ID
     * @param callback
     */
    public void postDoFavorite(String articleId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_FAVORITE
                .replace("{articleId}", articleId);

        BaseService.post(url,new HashMap<String, Object>(), false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 点赞
     * @param articleId 文章ID
     * @param callback
     */
    public void postDoLike(String articleId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_LIKE
                .replace("{articleId}", articleId);

        BaseService.post(url,new HashMap<String, Object>(), false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 提交评论
     * @param
     * @param callback
     */
    public void postDoComment(String articleId, String content, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DO_COMMENT
                .replace("{articleId}", articleId);
//        {
//            "comment_info":""
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("comment_info", content);

        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 提交答题答案（分量表下的所有问题）
     * @param childDimensionId
     * @param costedTime
     * @param answerDtos
     * @param callback
     */
    public void postQuestionAnswersSubmit(String childDimensionId, int costedTime, Collection<AnswerDto> answerDtos, final BaseService.ServiceCallback callback){
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

        BaseService.post(url, jsonObject, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 保存答题答案（分量表下的部分问题）
     * @param childDimensionId
     * @param costedTime
     * @param answerDtos
     * @param callback
     */
    public void postQuestionAnswersSave(String childDimensionId, int costedTime, Collection<AnswerDto> answerDtos, final BaseService.ServiceCallback callback){
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

        BaseService.post(url, jsonObject, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取用户详情
     * @param callback
     */
    public static void getUserInfo (final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_USER_INFO;
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 修改密码
     * @param passwordOld
     * @param passwordNew
     * @param callback
     */
    public void patchModifyPassword(String passwordOld, String passwordNew, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_MODIFY_PASSWORD;
//        {
//            "old_password":"",      //旧密码，加密
//                "new_password":""       //新密码，加密
//        }
        String passwordOldMd5 = EncryptUtil.encryptMD5_QS(passwordOld);
        String passwordNewMd5 = EncryptUtil.encryptMD5_QS(passwordNew);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("old_password", passwordOldMd5);
        map.put("new_password", passwordNewMd5);

        BaseService.patch(url,map, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 重置密码
     * @param dto
     * @param callback
     */
    public void patchResetPassword(ResetPasswordDto dto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_RESET_PASSWORD;
//        {
//            "mobile":"string",            //手机号（必填）
//                "mobile_code":"string",        //短信验证码（必填）
//                "tenant":"string",            //租户名称（选填）
//                "new_password":"string",       //密码（加密后）
//                "area_code":"string"          //手机国际区号(选填)，中国：+86（默认）
//        }
        String passwordNewMd5 = EncryptUtil.encryptMD5_QS(dto.getNew_password());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", dto.getMobile());
        map.put("mobile_code", dto.getMobile_code());
        map.put("tenant", dto.getTenant());
        map.put("new_password", passwordNewMd5);
        map.put("area_code", dto.getArea_code());

        BaseService.patch(url,map, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 查询已绑定的第三方平台
     * @param callback
     */
    public void getThirdBindPlatform(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_THIRD_PLATPORM
                .replace("{tenant}", Dictionary.Tenant_CheersMind);

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 第三方平台账号绑定
     * @param bindDto
     * @param callback
     */
    public void postThirdPlatBind(ThirdPlatBindDto bindDto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_BIND_THIRD_PLATPORM;
//        {
//            "open_id":"",               //第三方平台给用户分配的Id,如微信、QQ的openId
//                "plat_source":"",           //第三方登录来源，目前支持：1-qq 2-weixin
//                "app_id":"",                //QQ平台必填
//                "third_access_token":""      //第三方的access_token
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("open_id", bindDto.getOpenId());
        map.put("plat_source", bindDto.getPlatSource());
        map.put("third_access_token", bindDto.getThirdAccessToken());
        //我们应用在第三方平台注册的appId
        if (!TextUtils.isEmpty(bindDto.getAppId())) {
            map.put("app_id", bindDto.getAppId());
        }
        map.put("tenant", bindDto.getTenant());

        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 第三方平台账号解绑
     * @param bindDto
     * @param callback
     */
    public void postThirdPlatUnbind(ThirdPlatBindDto bindDto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_UNBIND_THIRD_PLATPORM;
//        {
//            "open_id":"",               //第三方平台给用户分配的Id,如微信、QQ的openId
//                "source_plat":"qq",          //第三方登录来源，目前支持：1-qq 2-weixin
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("open_id", bindDto.getOpenId());
        map.put("plat_source", bindDto.getPlatSource());

        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取分类列表
     * @param callback
     */
    public void getCategories(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CATEGORIES;

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 查询当前签到状态
     * @param callback
     */
    public void getDailySignInStatus(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DAILY_SIGN_IN_STATUS;

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 签到
     * @param callback
     */
    public void postDailySignIn(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_DAILY_SIGN_IN;

        Map<String, Object> params = new HashMap<String, Object>();

        BaseService.post(url, params, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取总积分
     * @param callback
     */
    public void getIntegralTotalScore(final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_INTEGRAL_TOTAL_SCORE;

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取积分列表
     * @param callback
     */
    public void getIntegralList(int page, int size, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_INTEGRALS
                .replace("{page}", page + "")
                .replace("{size}", size + "");

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 获取报告V2
     * @param childExamId 孩子测评ID
     * @param relationId 维度ID（必填）topicid,或者topic_dimension_id
     * @param relationType 维度类型（topic_dimension - 话题下的主题，topic - 话题 ，必填 ）
     * @param compareId 对比样本ID( 0-全国，1- 八大区，2-省，3-市，4-区)
     * @param callback
     */
    public void getReportV2(String childExamId,String relationId,String relationType,int compareId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_REPORT_V2
                .replace("{child_exam_id}",childExamId)
                .replace("{relation_id}", relationId)
                .replace("{relation_type}", relationType)
                .replace("{compare_id}", compareId +"");
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 获取话题的历史报告
     * @param topicId 话题ID
     * @param childId 孩子ID
     * @param callback
     */
    public void getHistoryReport(String topicId,String childId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_HISTORY_REPORT
                .replace("{topic_id}",topicId)
                .replace("{child_id}", childId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 获取话题详情
     * @param topicId
     * @param callback
     */
    public void getTopicDetail(String topicId,final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_TOPIC_INFO
                .replace("{topic_id}",topicId);
        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }

    /**
     * 获取消息列表
     * @param page
     * @param size
     * @param callback
     */
    public void getMessage(int page, int size, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_MESSAGE
                .replace("{page}", page + "")
                .replace("{size}", size + "");

        BaseService.get(url, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 标记消息为已读
     * @param messageId
     * @param callback
     */
    public void putMarkRead(long messageId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_MARK_READ
                .replace("{message_id}", messageId +"");
        Map<String, Object> map = new HashMap<String, Object>();
        BaseService.put(url,map, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 创建会话
     * @param dto
     * @param callback
     */
    public void postAccountsSessions(CreateSessionDto dto, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_CREATE_SESSION;
//        {
//            "session_type":"int",   //会话类型，0：注册(手机)，1：登录(帐号、密码登录)，2：手机找回密码，3：登录(短信登录)，4:下发短信验证码
//                "device_id":"string",   //设备唯一ID
//                "tenant":"string"       //租户名称（选填）
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("session_type", dto.getSessionType());
        map.put("device_id", dto.getDeviceId());
        map.put("tenant", dto.getTenant());
        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Object obj) {
                callback.onResponse(obj);
            }
        });
    }


    /**
     * 获取图形验证码
     * @param sessionId 会话ID
     * @param callback
     */
    public void getImageCaptcha(String sessionId, final BaseService.ServiceCallback callback){
        String url = HttpConfig.URL_IMAGE_CAPTCHA
                .replace("{session_id}", sessionId);

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
//                .addHeader("Authorization",getHeader(url,"GET"))
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
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
            public void onResponse(final Call call, final Response response) throws IOException {
                if (callback != null) {
                    try {
                        //返回结果信息
                        final InputStream inputStream = response.body().byteStream();
                        //返回结果信息
                        final String bodyStr = response.body().string();

                        QSApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //目前的业务错误机制：只要业务验证发生任何错误，http的code都不会是200出头的正确应答code，
                                    // 而只会返回什么400+，500+之类的，并且附带错误信息串
                                    if (response.code() == 200 || response.code() == 201) {
                                        callback.onResponse(inputStream);
                                    } else {
                                        callback.onFailure(new QSCustomException(bodyStr));
                                    }
                                } catch (Exception e) {
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
        });

//        BaseService.get(url, new BaseService.ServiceCallback() {
//            @Override
//            public void onFailure(QSCustomException e) {
//                callback.onFailure(e);
//            }
//
//            @Override
//            public void onResponse(Object obj) {
//                callback.onResponse(obj);
//            }
//        });
    }


}
