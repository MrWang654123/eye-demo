package com.cheersmind.cheersgenie.features.dto;

/**
 * “我的”（带userId）
 */
public class MineDto extends BaseDto {

    //用户ID
    private long userId;

    public MineDto() {
    }

    public MineDto(long userId) {
        this.userId = userId;
    }

    public MineDto(int page, int size, long userId) {
        super(page, size);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
