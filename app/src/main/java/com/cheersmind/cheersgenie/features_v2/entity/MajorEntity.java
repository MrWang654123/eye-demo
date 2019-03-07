package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 专业
 */
public class MajorEntity extends AbstractExpandableItem<MajorEntity> implements MultiItemEntity, Serializable {

    public MajorEntity() {
        mExpandable = false;
    }

    @InjectMap(name = "id")
    private String id;

    //标题
    @InjectMap(name = "article_title")
    private String articleTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    private int level = 0;
    private int itemType = MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL0;
    //最里层的子项是否是兄弟中的最后一个
    private boolean isLastInMaxLevel;

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isLastInMaxLevel() {
        return isLastInMaxLevel;
    }

    public void setLastInMaxLevel(boolean lastInMaxLevel) {
        isLastInMaxLevel = lastInMaxLevel;
    }
}
