package com.cheersmind.cheersgenie.features.dto;

/**
 * 文章dto
 */
public class ArticleDto extends BaseDto {

    //搜索文本
    private String filter;

    //种类ID
    private String categoryId;

    public ArticleDto() {
    }

    public ArticleDto(int page, int size) {
        super(page, size);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
