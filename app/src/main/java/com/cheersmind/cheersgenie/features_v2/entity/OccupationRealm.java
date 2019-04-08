package com.cheersmind.cheersgenie.features_v2.entity;

import android.text.TextUtils;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 职业领域
 */
public class OccupationRealm extends DataSupport implements Serializable {

    private int id;

    //行业 - 领域
    @InjectMap(name = "realm")
    private String realm;

    //ACT - 六大人格分类
    @InjectMap(name = "personality_type")
    private String personality_type;

    //门类（必须初始化，否则存储报错）
    @InjectMap(name = "sub_items")
    private List<OccupationCategory> categories = new ArrayList<>();

    //职业类型
    private int type;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getPersonality_type() {
        return personality_type;
    }

    public void setPersonality_type(String personality_type) {
        this.personality_type = personality_type;
    }

    public List<OccupationCategory> getCategories() {
        return categories;
    }

    /**
     * 从数据库中获取List<OccupationCategory>
     * @return List<OccupationCategory>
     */
    public List<OccupationCategory> getCategoriesFromDB() {
        return DataSupport.where("OccupationRealm_id = ?", String.valueOf(id)).find(OccupationCategory.class);
    }

    public void setCategories(List<OccupationCategory> categories) {
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //统一的获取名称
    public String getName() {
        return !TextUtils.isEmpty(realm) ? realm : personality_type;
    }
}
