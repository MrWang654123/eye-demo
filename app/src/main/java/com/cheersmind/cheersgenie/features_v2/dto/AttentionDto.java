package com.cheersmind.cheersgenie.features_v2.dto;

/**
 * 关注dto
 */
public class AttentionDto {

    //关注对象ID
    private String entity_id;

    //类型：0:学校 1:专业 2：职业
    private int type;

    //标签（目前传关注对象的名称）
    private String tag;

    //是否关注 true 关注 false 取消关注
    private boolean follow;

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
