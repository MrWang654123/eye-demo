package com.cheersmind.cheersgenie.features.dto;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 关注Dto
 */
public class AttentionEntity extends DataSupport implements Serializable {


    @InjectMap(name="user_id")
    private String user_id;

    @InjectMap(name ="entity_id")
    private String entity_id;

    @InjectMap(name ="tag")
    private String tag;

    @InjectMap(name ="type")
    private int type;

    @InjectMap(name ="is_follow")
    private boolean is_follow;

    @InjectMap(name ="create_time")
    private Object create_time;

    @InjectMap(name ="update_time")
    private Object update_time;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isIs_follow() {
        return is_follow;
    }

    public void setIs_follow(boolean is_follow) {
        this.is_follow = is_follow;
    }

    public Object getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Object create_time) {
        this.create_time = create_time;
    }

    public Object getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Object update_time) {
        this.update_time = update_time;
    }
}
