package com.cheersmind.cheersgenie.features.dto;

/**
 * 评论dto
 */
public class CommentDto extends BaseDto {

    //评论的对象的ID（目前只有文章）
    private String id;

    //评论的对象类型，0：文章
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
