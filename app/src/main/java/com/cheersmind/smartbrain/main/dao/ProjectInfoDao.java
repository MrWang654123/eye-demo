package com.cheersmind.smartbrain.main.dao;

import android.content.ContentValues;

import com.cheersmind.smartbrain.main.entity.ProjectEntity;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/6/29.
 */

public class ProjectInfoDao {

    /**
     * 获取所有孩子所有项目列表
     * @return
     */
    public static List<ProjectEntity> getAllProjects(){
        List<ProjectEntity> list = DataSupport.findAll(ProjectEntity.class);
        return list;
    }

    /**
     * 设置默认项目
     * @param project
     */
    public static void setDefaultProject(ProjectEntity project){
        ContentValues values = new ContentValues();
        values.put("curSelect", 0);
        DataSupport.updateAll(ProjectEntity.class,values);

        ContentValues values1 = new ContentValues();
        values.put("curSelect", 1);
        DataSupport.updateAll(ProjectEntity.class, values1, "project_id = ?", project.getProjectId());

    }

    /**
     * 获取默认项目
     * @return
     */
    public static ProjectEntity getDefaultProject(){
        List<ProjectEntity> list = getAllProjects();
        if(list!=null && list.size()>0){
            for(ProjectEntity projectEntity:list){
                if(projectEntity.getCurSelect() == 1){
                    return  projectEntity;
                }
            }
        }

        return null;
    }


}
