package com.cheersmind.smartbrain.main.service;

import android.util.Log;

import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.constant.HttpConfig;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

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

}
