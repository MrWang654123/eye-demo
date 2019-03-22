package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEduLevel;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeProvince;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankItem;

/**
 * 大学排名dto
 */
public class CollegeRankDto extends BaseDto {

    public CollegeRankDto(int page, int size) {
        super(page, size);
    }

    //省份
    private CollegeProvince province;

    //学历层次
    private CollegeEduLevel eduLevel;

    //主题排名项
    private CollegeRankItem rankItem;


    public CollegeProvince getProvince() {
        return province;
    }

    public void setProvince(CollegeProvince province) {
        this.province = province;
    }

    public CollegeEduLevel getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(CollegeEduLevel eduLevel) {
        this.eduLevel = eduLevel;
    }

    public CollegeRankItem getRankItem() {
        return rankItem;
    }

    public void setRankItem(CollegeRankItem rankItem) {
        this.rankItem = rankItem;
    }
}
