package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 简单文章实体
 */
public class SimpleArticleEntity extends DataSupport implements Serializable {

    @InjectMap(name = "id")
    private String id;

    //标题
    @InjectMap(name = "article_title")
    private String articleTitle;

    //主图
    @InjectMap(name = "article_img")
    private String articleImg;

    //浏览次数
    @InjectMap(name = "page_view")
    private int pageView;

    //收藏次数
    @InjectMap(name = "page_favorite")
    private int pageFavorite;

    //评测次数
    @InjectMap(name = "test_count")
    private int testCount;

    //是否关联测评，0 没有 1 有
    @InjectMap(name = "is_reference_test")
    private int isReferenceTest;

    //简介
    @InjectMap(name = "summary")
    private String summary;

    //标签集合
    @InjectMap(name = "tags")
    private List<ArticleTag> articleTags;

    //文章来源类型：1-原创，2-转载
    @InjectMap(name = "source_type")
    private int sourceType;

    //作者
    @InjectMap(name = "article_author")
    private String articleAuthor;

    //发布时间
    @InjectMap(name = "publish_date")
    private String publishDate;

    //文章类型：1-普通图文文章，2-视频文章，3-画册？
    @InjectMap(name = "content_type")
    private int contentType;

    //类型
    @InjectMap(name = "category")
    private ArticleCategory category;

    //是否收藏
    @InjectMap(name = "is_favorite")
    private boolean isFavorite;

    //是否点赞
    @InjectMap(name = "is_like")
    private boolean isLike;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleImg() {
        return articleImg;
    }

    public void setArticleImg(String articleImg) {
        this.articleImg = articleImg;
    }

    public int getIsReferenceTest() {
        return isReferenceTest;
    }

    public void setIsReferenceTest(int isReferenceTest) {
        this.isReferenceTest = isReferenceTest;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getPageView() {
        return pageView;
    }

    public void setPageView(int pageView) {
        this.pageView = pageView;
    }

    public int getPageFavorite() {
        return pageFavorite;
    }

    public void setPageFavorite(int pageFavorite) {
        this.pageFavorite = pageFavorite;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public List<ArticleTag> getArticleTags() {
        return articleTags;
    }

    public void setArticleTags(List<ArticleTag> articleTags) {
        this.articleTags = articleTags;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public void setArticleAuthor(String articleAuthor) {
        this.articleAuthor = articleAuthor;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public ArticleCategory getCategory() {
        return category;
    }

    public void setCategory(ArticleCategory category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
