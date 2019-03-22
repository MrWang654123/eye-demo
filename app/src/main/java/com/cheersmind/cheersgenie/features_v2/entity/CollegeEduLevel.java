package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 大学的学历层级
 */
public class CollegeEduLevel extends DataSupport implements Serializable {

    //编码
    @InjectMap(name = "code")
    private String code;

    //名称
    @InjectMap(name = "name")
    private String name;

    //排名项（必须初始化，否则存储报错）
    @InjectMap(name = "ranking_items")
    private List<CollegeRankItem> ranking_items = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CollegeRankItem> getRanking_items() {
        return ranking_items;
    }

    public void setRanking_items(List<CollegeRankItem> ranking_items) {
        this.ranking_items = ranking_items;
    }
}
