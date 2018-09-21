package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 文章详情
 */
public class ArticleEntity extends DataSupport implements Serializable {

    @InjectMap(name = "id")
    private String id;

    //标题
    @InjectMap(name = "article_title")
    private String articleTitle;

    //内容 富文本
    @InjectMap(name = "article_content")
    private String articleContent;

    //主图
    @InjectMap(name = "article_img")
    private String articleImg;

    //文章的视频地址，需要按按算法生成最终可用的地址
    @InjectMap(name = "article_video")
    private String articleVideo;

    //文章来源类型：1-原创，2-转载
    @InjectMap(name = "source_type")
    private int sourceType;

    //文章来源名称，当source_type=2时需要使用
    @InjectMap(name = "source_name")
    private String sourceName;

    //文章来源的web地址，当source_type=2时需要使用
    @InjectMap(name = "source_web_url")
    private int sourceWebUrl;

    //文章来源的app url scheme，当source_type=2时需要使用
    @InjectMap(name = "source_app_url")
    private int sourceAppUrl;

    //浏览次数
    @InjectMap(name = "page_view")
    private int pageView;

    //收藏次数
    @InjectMap(name = "page_favorite")
    private int pageFavorite;

    //点赞次数
    @InjectMap(name = "page_like")
    private int pageLike;

    //评测次数
    @InjectMap(name = "test_count")
    private int testCount;

    //是否关联测评，0 没有 1 有
    @InjectMap(name = "is_reference_test")
    private int isReferenceTest;

    //来源
    @InjectMap(name = "source")
    private String source;

    //用户（作者）
    @InjectMap(name = "user_data")
    private UserEntity userData;

    //是否显示评论
    @InjectMap(name = "show_comment")
    private boolean showComment;

    //发布时间
    @InjectMap(name = "publish_date")
    private String publishDate;

    //话题信息（场景）
    @InjectMap(name = "topic_info")
    private TopicInfo topicInfo;

    //是否收藏
    @InjectMap(name = "favorite")
    private boolean favorite;

    //是否点赞
    @InjectMap(name = "like")
    private boolean like;

    //内容类型（0 文章 1 视频,）
    @InjectMap(name = "content_type")
    private int contentType;


    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

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

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleVideo() {
        return articleVideo;
    }

    public void setArticleVideo(String articleVideo) {
        this.articleVideo = articleVideo;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getSourceWebUrl() {
        return sourceWebUrl;
    }

    public void setSourceWebUrl(int sourceWebUrl) {
        this.sourceWebUrl = sourceWebUrl;
    }

    public int getSourceAppUrl() {
        return sourceAppUrl;
    }

    public void setSourceAppUrl(int sourceAppUrl) {
        this.sourceAppUrl = sourceAppUrl;
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

    public int getPageLike() {
        return pageLike;
    }

    public void setPageLike(int pageLike) {
        this.pageLike = pageLike;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getIsReferenceTest() {
        return isReferenceTest;
    }

    public void setIsReferenceTest(int isReferenceTest) {
        this.isReferenceTest = isReferenceTest;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UserEntity getUserData() {
        return userData;
    }

    public void setUserData(UserEntity userData) {
        this.userData = userData;
    }

    public boolean isShowComment() {
        return showComment;
    }

    public void setShowComment(boolean showComment) {
        this.showComment = showComment;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(TopicInfo topicInfo) {
        this.topicInfo = topicInfo;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getArticleImg() {
        return articleImg;
    }

    public void setArticleImg(String articleImg) {
        this.articleImg = articleImg;
    }
}
