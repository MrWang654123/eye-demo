package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRealm;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationType;

/**
 * 职业列表dto
 */
public class OccupationDto extends BaseDto {

    public OccupationDto(int page, int size) {
        super(page, size);
    }

    //搜索文本
    private String searchText;

    //职业类型
    private OccupationType type;

    //职业领域
    private OccupationRealm realm;

    //行业门类
    private OccupationCategory category;

    public OccupationType getType() {
        return type;
    }

    public void setType(OccupationType type) {
        this.type = type;
    }

    public OccupationRealm getRealm() {
        return realm;
    }

    public void setRealm(OccupationRealm realm) {
        this.realm = realm;
    }

    public OccupationCategory getCategory() {
        return category;
    }

    public void setCategory(OccupationCategory category) {
        this.category = category;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
