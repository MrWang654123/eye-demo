package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRealm;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationType;

/**
 * 关注列表dto
 */
public class AttentionListDto extends BaseDto {

    public AttentionListDto(int page, int size) {
        super(page, size);
    }

    //关注类型
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
