package com.cheersmind.cheersgenie.features.modules.article.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.BaseAdapter;
import com.cheersmind.cheersgenie.features.adapter.CommentAdapter;
import com.cheersmind.cheersgenie.features.constant.VideoConstant;
import com.cheersmind.cheersgenie.features.dto.CommentDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.htmlImageGetter.URLImageParser;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ArticleEntity;
import com.cheersmind.cheersgenie.main.entity.CommentEntity;
import com.cheersmind.cheersgenie.main.entity.CommentRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 文章详情
 */
public class ArticleDetailActivity_bak extends BaseActivity {

    private static final String ARTICLE_ID = "article_id";
    //文章Id
    String articleId;

    //标题栏
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //内容模块
    @BindView(R.id.sv_main_block)
    NestedScrollView svMainBlock;
    //文章标题
    @BindView(R.id.tv_article_title)
    TextView tvArticleTitle;
    //作者
    @BindView(R.id.tv_author)
    TextView tvAuthor;
    //文章日期
    @BindView(R.id.tv_date)
    TextView tvDate;
    //阅读人数
    @BindView(R.id.tv_read_count)
    TextView tvReadCount;
    //文章内容（富文本）
//    @BindView(R.id.web_article_content)
//    @Nullable WebView webArticleContent;
    //文章内容（富文本）
    @BindView(R.id.tv_article_content)
    TextView tvArticleContent;

    //关联的评测模块
    @BindView(R.id.rl_evaluation)
    RelativeLayout rlEvaluation;
    //评测过的人数
    @BindView(R.id.tv_used_count)
    TextView tvUsedCount;
    //评测名称
    @BindView(R.id.tv_dimension_name)
    TextView tvDimensionName;
    //跳转到评测的按钮
    @BindView(R.id.btn_goto_evaluation)
    Button btnGotoEvaluation;

    //评论内容模块
    @BindView(R.id.ll_comment_content)
    LinearLayout llComment;
    //评论数
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    //评论列表布局recycleView
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //暂无评论
    @BindView(R.id.tv_no_comment)
    TextView tvNoComment;

    //评论编辑模块
    @BindView(R.id.ll_comment_edit)
    LinearLayout llCommentEdit;
    //评论返回按钮
    @BindView(R.id.btn_comment_back)
    Button btnCommentBack;
    //进入评论提示文本
    @BindView(R.id.tv_comment_tip)
    TextView tvCommentTip;
    //收藏
    @BindView(R.id.ibtn_favorite)
    ImageButton ibtnFavorite;
    //点赞
    @BindView(R.id.ibtn_like)
    ImageButton ibtnLike;
    //点赞数
    @BindView(R.id.tv_like_count)
    TextView tvLikeCount;

    @BindView(R.id.jz_video)
    JZVideoPlayerStandard jzVideo;

    //空布局模块
    @BindView(R.id.emptyLayout)
    XEmptyLayout xemptyLayout;

    //适配器的数据列表
    List<CommentEntity> recyclerItem = new ArrayList<>();
    //适配器
    CommentAdapter recyclerAdapter;
    BaseAdapter.RequestLoadMoreListener requestLoadMoreListener = new BaseAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            loadCommentData(articleId);
        }
    };

    //页长度
    private static final int PAGE_SIZE = 1;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //测试的文章内容
    String testArticleContent = "<div class=\"article\" id=\"article\">\t\t\t\t\n" +
            "<p>　　原标题：外交部今天再谈中非“全家福”，说了一句意味深长的话</p>\n" +
//            "<div class=\"img_wrapper\"><img style=\"max-width: 500px;\" src=\"http://n.sinaimg.cn/translate/784/w500h284/20180821/OHhj-hhzsnea3625408.jpg\" alt=\"\"></div>\n" +
            "<img style=\"max-width: 500px;\" src=\"http://n.sinaimg.cn/translate/784/w500h284/20180821/OHhj-hhzsnea3625408.jpg\" alt=\"\">" +
            "<p>　　[环球网综合报道]2018年8月21日外交部发言人陆慷主持例行记者会，有记者问到，问：第一，台湾方面认为中国与萨尔瓦多建交与大陆“金元外交”有关。外交部发言人对此有何回应？第二，今年年初，王毅国务委员兼外长表示希望在中非合作论坛北京峰会期间拍一张中非“全家福”。这次斯威士兰是否将与会？</p>\n" +
//            "<p>　　陆慷回应，关于第一个问题，相信你已经关注到了今天上午王毅国务委员兼外长与卡斯塔内达外长共见记者的详细情况。按说对台湾岛内某些人的表态，应由中央政府主管部门作回应，不是外交部的职责。但就这一问题而言，我可以强调一下，中国和萨尔瓦多建立外交关系是政治决断，绝不是台湾方面某些人想象的所谓“交易筹码”。萨尔瓦多政府决定同中国建交，是出于对一个中国原则的认同，是一项政治决断，没有任何经济前提。中方高度赞赏萨方的立场。</p>\n" +
//            "<p>　　我也想提醒台湾方面某些人，世界各国同中华人民共和国建立和发展正常友好的国家关系是大势所趋、民心所向，希望岛内某些人能够看清世界大势，不要把任何问题都理解为钱的问题。</p>\n" +
            "<p>　　至于你提到现在还有一个非洲国家仍然没有放弃同台湾的所谓“外交关系”，我们已经多次表明，中国政府有意愿在和平共处五项原则和一个中国原则基础上，同世界上所有国家发展友好合作关系。我们相信这样的关系符合彼此利益，希望有关国家能够认清世界大势。</p>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t<div class=\"wap_special\" data-sudaclick=\"content_relativetopics_p\">\n" +
            "        <div class=\"tlt\">点击进入专题：</div>\n" +
            "\t<a href=\"http://news.sina.cn/zt_d/zfhzlt\" target=\"_blank\">习近平将主持2018年中非合作论坛北京峰会</a></div>\n" +
            "  \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "<p class=\"show_author\">责任编辑：张建利 </p>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t</div>";

    /**
     * 开启文章详情页面
     *
     * @param articleId
     */
    public static void startArticleDetailActivity(Context context, String articleId) {
        Intent intent = new Intent(context, ArticleDetailActivity_bak.class);
        Bundle extras = new Bundle();
        extras.putString(ARTICLE_ID, articleId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_article_detail_bak;
    }

    @Override
    protected String settingTitle() {
        return "文章详情";
    }

    @Override
    public void onInitView() {
        //正在加载提示
        xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        //重载点击监听
        xemptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //加载文章详情
                loadArticleDetail(articleId);
            }
        });

        //隐藏评论模块
        hideCommentBlock();

        //初始化webview
//        initWebView();

        recyclerAdapter = new CommentAdapter(ArticleDetailActivity_bak.this, R.layout.recycleritem_comment, recyclerItem);
        //加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(requestLoadMoreListener, recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(ArticleDetailActivity_bak.this));
        //禁用内嵌的recycleView滚动
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setAdapter(recyclerAdapter);

        //nestedscrollview滑动监听（禁止了内部的recyclerview的滑动，所以上拉加载得监听它）
        svMainBlock.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    recyclerAdapter.notifyLoadMoreToLoading();
                }
            }
        });

        //收藏按钮点击监听
        ibtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(articleId)) {
                    return;
                }
                doFavorite(articleId);
            }
        });

        //点赞按钮监听
        ibtnLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(articleId)) {
                    return;
                }
                doLike(articleId);
            }
        });

        //评论编辑模块中的返回按钮监听
        btnCommentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //进入评论提示文本的点击监听
        tvCommentTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出评论编辑弹窗
                popupCommentEditWindows();
            }
        });

    }


    /**
     * 初始化数据
     */
    @Override
    public void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(ArticleDetailActivity_bak.this, "数据传递有误");
            return;
        }

        articleId = getIntent().getExtras().getString(ARTICLE_ID);

        //加载文章详情
        loadArticleDetail(articleId);

        //加载评论数据
        articleId = "1";
//        loadCommentData(articleId);

        //处理图片宽高
//        String tempContent = HtmlUtil.getNewContentByHandleImage(testArticleContent);
//        webArticleContent.loadDataWithBaseURL(null, tempContent, "text/html","utf-8",null);

        jzVideo.setUp(VideoConstant.videoUrlList[0]
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子不信");
        Glide.with(this)
                .load(VideoConstant.videoThumbList[0])
                .into(jzVideo.thumbImageView);

        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();

        //Change these two variables back
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (jzVideo.backPress()) {
            return;
        }
        super.onBackPressed();
    }


    /**
     * 加载文章详情
     * @param articleId
     */
    private void loadArticleDetail(final String articleId) {

        DataRequestService.getInstance().getArticleDetail(articleId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                xemptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ArticleEntity articleEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleEntity.class);
                    //刷新文章视图
                    refreshArticleView(articleEntity);
                    //刷新收藏视图
                    refreshFavoriteView(articleEntity.isFavorite());
                    //刷新点赞视图
                    refreshLikeView(articleEntity.isLike(), articleEntity.getPageLike());

                    //有关联的测评，则刷新关联测评的视图
                    if (articleEntity.getIsReferenceTest() == 1) {
                        refreshRelativeExamView(articleEntity);
                    }
                    //文章评论模块开启则加载评论信息
                    if (!articleEntity.isCommentDisabled()) {
                        loadCommentData(articleId);
                    }

                } catch (Exception e) {
                    //视为找不到文章数据
                    xemptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                }
            }
        });
    }


    /**
     * 点赞操作
     * @param articleId
     */
    private void doLike(String articleId) {
        DataRequestService.getInstance().postDoLike(articleId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //刷新点赞视图
                    refreshLikeView((Boolean) dataMap.get("like"), new Double(dataMap.get("page_like").toString()).intValue());

                } catch (Exception e) {
                    //操作失败
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 刷新点赞视图
     * @param isLike
     * @param likeCount
     */
    private void refreshLikeView(boolean isLike, int likeCount) {
        if (isLike) {
            //点赞状态
            ibtnLike.setBackgroundResource(R.drawable.like_do);

        } else {
            //未点赞状态
            ibtnLike.setBackgroundResource(R.drawable.like_not);
        }

        //点赞数量
        tvLikeCount.setText(likeCount + "");
    }

    /**
     * 收藏操作
     * @param articleId
     */
    private void doFavorite(String articleId) {
        DataRequestService.getInstance().postDoFavorite(articleId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //刷新收藏视图
                    refreshFavoriteView((Boolean) dataMap.get("favorite"));

                } catch (Exception e) {
                    //操作失败
                    e.printStackTrace();
                    onFailure(new QSCustomException("操作失败"));
                }
            }
        });
    }


    /**
     * 刷新收藏视图
     * @param isFavorite
     */
    private void refreshFavoriteView(boolean isFavorite) {
        if (isFavorite) {
            //收藏状态
            ibtnFavorite.setBackgroundResource(R.drawable.favorite_do);
        } else {
            //未收藏状态
            ibtnFavorite.setBackgroundResource(R.drawable.favorite_not);
        }
    }

    /**
     * 刷新关联测评的视图
     * @param articleEntity
     */
    private void refreshRelativeExamView(ArticleEntity articleEntity) {

    }

    /**
     * 刷新文章视图
     * @param articleEntity
     */
    private void refreshArticleView(ArticleEntity articleEntity) {
        if (articleEntity == null) return;
        //标题
        tvArticleTitle.setText(articleEntity.getArticleTitle());
        //作者
        if (articleEntity.getUserData() != null) {
            tvAuthor.setText(articleEntity.getUserData().getUserName());
        }
        //发布日期
        tvDate.setText(articleEntity.getPublishDate());
        //阅读数
        tvReadCount.setText(articleEntity.getPageView() +"");

        //文章内容（富文本）
        //处理图片宽高
        String tempContent = testArticleContent;
//        String tempContent = articleEntity.getArticleContent();
        tempContent = tempContent.replaceAll("\t", "");
        tempContent = tempContent.replaceAll("\n", "");
        tvArticleContent.setText(Html.fromHtml(tempContent, new URLImageParser(ArticleDetailActivity_bak.this, tvArticleContent), null));

//        tempContent = HtmlUtil.getNewContentByHandleImage(tempContent);
//        webArticleContent.loadDataWithBaseURL(null, tempContent, "text/html","utf-8",null);
    }


    /**
     * 加载评论列表
     * @param articleId
     */
    private void loadCommentData(String articleId) {
        //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中

        CommentDto dto = new CommentDto();
        dto.setId(articleId);
        dto.setPage(pageNum);
        dto.setSize(PAGE_SIZE);
        dto.setType(0);//类型：文章
        DataRequestService.getInstance().getCommentList(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //上拉加载
                if (ArrayListUtil.isNotEmpty(recyclerItem)) {
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }

                //设置空布局：当前列表还没有数据的情况，提示：网络连接有误，或者请求失败
                if (ArrayListUtil.isEmpty(recyclerItem)) {
//                    xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onResponse(Object obj) {
                //设置空布局
//                xemptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CommentRootEntity commentRootEntity = InjectionWrapperUtil.injectMap(dataMap, CommentRootEntity.class);

                    totalCount = commentRootEntity.getTotal();
                    List<CommentEntity> dataList = commentRootEntity.getItems();
                    //非空
                    if (ArrayListUtil.isEmpty(dataList)) {
                        throw new Exception();
                    }

                    //添加数据
                    recyclerItem.addAll(dataList);

                    //判断是否全部加载结束
                    if (recyclerItem.size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //刷新评论内容模块的header
                    refreshCommentContentBlockHeaderView(totalCount);

                    //页码+1
                    pageNum++;

                    //显示评论模块
                    showCommentBlock();

                } catch (Exception e) {
                    //设置空布局：当前列表还没有数据的情况，提示：没有数据
                    if (ArrayListUtil.isEmpty(recyclerItem)) {
//                        xemptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                        //暂无评论
                        settingNoCommentView();
                    }
                }

            }
        });
    }

    /**
     * 刷新评论内容模块的header部分的视图
     */
    private void refreshCommentContentBlockHeaderView(int totalCount) {
        //评论总数
        tvCommentCount.setText(getResources().getString(R.string.comment_count, totalCount +""));
    }


    /**
     * 隐藏评论模块
     */
    private void hideCommentBlock() {
        llComment.setVisibility(View.GONE);
        llCommentEdit.setVisibility(View.GONE);
    }

    /**
     * 显示评论模块
     */
    private void showCommentBlock() {
        llComment.setVisibility(View.VISIBLE);
        llCommentEdit.setVisibility(View.VISIBLE);
    }

    /**
     * 没有评论内容时的视图设置
     */
    private void settingNoCommentView() {
        llComment.setVisibility(View.GONE);
        //提示信息
        tvNoComment.setVisibility(View.VISIBLE);
    }

    /**
     * 弹出评论对话框
     */
    private void popupCommentEditWindows() {
        View view = LayoutInflater.from(ArticleDetailActivity_bak.this).inflate(R.layout.popup_window_comment_edit, null);
        final Dialog dialog = new Dialog(this, R.style.dialog_bottom_full);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(view);

        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度占满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialog_Animation);//动画
        dialog.show();

        //评论编辑框
        final EditText etComment = view.findViewById(R.id.et_comment);

        //发送按钮
        final Button btnSend = view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //提交评论
                doComment(articleId, etComment.getText().toString());
            }
        });
        //关闭按钮
        final Button btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //没输入，则发送按钮禁用
                if (s.length() == 0) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }
        });

    }

    /**
     * 提交评论
     * @param articleId
     * @param content
     */
    private void doComment(String articleId, String content) {
        DataRequestService.getInstance().postDoComment(articleId, content, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                ToastUtil.showShort(getApplicationContext(), "评论失败");
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CommentEntity commentEntity = InjectionWrapperUtil.injectMap(dataMap, CommentEntity.class);
                    //目前不刷新视图，只做提示
                    ToastUtil.showShort(getApplicationContext(), "评论成功");

                } catch (Exception e) {
                    //操作失败
                    e.printStackTrace();
                    ToastUtil.showShort(getApplicationContext(), "评论失败");
                }
            }
        });
    }

}

