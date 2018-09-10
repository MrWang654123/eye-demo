package com.cheersmind.cheersgenie.features.dto;

/**
 * 文章dto
 */
public class ArticleDto extends BaseDto {

    //搜索文本
    private String filter;

    //种类ID
    private String category_id;

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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
