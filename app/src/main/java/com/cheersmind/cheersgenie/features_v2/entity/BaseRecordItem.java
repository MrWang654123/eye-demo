package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 基础的档案子项
 */
public class BaseRecordItem extends AbstractExpandableItem<BaseRecordItem> implements MultiItemEntity {

    //是否是兄弟中的最后一个
    private boolean lastInBrother;

    //是否完成
    private boolean finish;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public BaseRecordItem setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    private int level = 0;

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isLastInBrother() {
        return lastInBrother;
    }

    public void setLastInBrother(boolean lastInBrother) {
        this.lastInBrother = lastInBrother;
    }
}
