package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 首页Banner
 */
public class HomeBanner extends DataSupport implements Serializable, MultiItemEntity {

    @InjectMap(name = "id")
    private int id;

    //描述 不显示
    @InjectMap(name = "describe")
    private String describe;

    //主图
    @InjectMap(name = "img_url")
    private String img_url;

    //1 文章   2 TAB
    @InjectMap(name = "type")
    private int type;

    //收藏次数
    @InjectMap(name = "value")
    private String value;

    // 1 文章ID   2 TAB 位置 0,1,2,3
    @InjectMap(name = "test_count")
    private int testCount;

    @InjectMap(name = "status")
    private int status;

    @InjectMap(name = "create_time")
    private String create_time;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
