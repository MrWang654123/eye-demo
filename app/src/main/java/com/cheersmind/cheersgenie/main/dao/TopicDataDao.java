package com.cheersmind.cheersgenie.main.dao;

import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/28.
 */

public class TopicDataDao {

    public static void updateAllBaseTopic(){
        DataSupport.deleteAll(TopicRootEntity.class);
    }

    //获取缓存基础主题列表
    public static List<TopicInfoEntity> getBaseTopicList(){
        List<TopicInfoEntity> entityList = DataSupport.findAll(TopicInfoEntity.class);
        if(entityList!=null && entityList.size()>0){
            return entityList;
        }
        return new ArrayList<TopicInfoEntity>();
    }


}
