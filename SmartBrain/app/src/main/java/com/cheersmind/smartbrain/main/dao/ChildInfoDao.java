package com.cheersmind.smartbrain.main.dao;

import com.cheersmind.smartbrain.main.entity.ChildInfoEntity;
import com.cheersmind.smartbrain.module.login.UCManager;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */

public class ChildInfoDao {

    /**
     * 获取默认孩子
     * @return
     */
    public static ChildInfoEntity getDefaultChild(){
        List<ChildInfoEntity> mList = DataSupport.findAll(ChildInfoEntity.class);
        if(mList==null || mList.size()==0){
            return UCManager.getInstance().getDefaultChild();
        }
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).isDefaultChild()){
                return mList.get(i);
            }
        }
        return mList.get(0);
    }

    /**
     * 获取所有孩子
     * @return
     */
    public static List<ChildInfoEntity> getAllChild(){
        List<ChildInfoEntity> list = DataSupport.findAll(ChildInfoEntity.class);
        return list;
    }

    public static String getDefaultChildId() {
        if (getDefaultChild() != null) {
            return getDefaultChild().getChildId();
        } else {
            return "";
        }
    }

    public static void deleteAllChild(){
        DataSupport.deleteAll(ChildInfoEntity.class);
    }

}
