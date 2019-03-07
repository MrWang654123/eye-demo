package com.cheersmind.cheersgenie.features.modules.article.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.CustomMediaPlayer.JZExoPlayer;
import com.cheersmind.cheersgenie.features.adapter.BaseAdapter;
import com.cheersmind.cheersgenie.features.adapter.CommentAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.CommentDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.features.view.video.JZVideoPlayerStandardHorizontal;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.features.entity.ArticleEntity;
import com.cheersmind.cheersgenie.features.entity.CommentEntity;
import com.cheersmind.cheersgenie.features.entity.CommentRootEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfo;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.EnvHostManager;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 文章详情
 */
public class ArticleDetailActivity extends BaseActivity {

    public static final String VIEW_NAME_HEADER_IMAGE = "VIEW_NAME_HEADER_IMAGE";
    public static final String VIEW_NAME_HEADER_TITLE = "VIEW_NAME_HEADER_TITLE";

    //主图url
    private static final String IV_MAIN_URL = "ivMainUrl";
    //文章标题
    private static final String ARTICLE_TITLE = "articleTitle";
    //文章ID
    private static final String ARTICLE_ID = "article_id";
    //文章Id
    String articleId;
    //主图Url
    String ivMainUrl;
    //文章标题
    String articleTitle;

    //标题栏
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    //文章标题
    @BindView(R.id.iv_main)
    SimpleDraweeView ivMain;

    //内容模块
    @BindView(R.id.sv_main_block)
    NestedScrollView svMainBlock;
    //文章标题
    @BindView(R.id.tv_article_title)
    TextView tvArticleTitle;

    //信息布局
    @BindView(R.id.ll_info)
    LinearLayout llInfo;
    //作者
    @BindView(R.id.tv_author)
    TextView tvAuthor;
    //文章日期
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_read_count)
    LinearLayout llReadCount;
    //阅读人数
    @BindView(R.id.tv_read_count)
    TextView tvReadCount;

    //文章内容（富文本）
    @BindView(R.id.web_article_content)
    WebView webArticleContent;
    //正在初始化富文本的提示
    @BindView(R.id.tv_init_content)
    TextView tvInitContent;
    //正在初始化富文本的提示布局
    @BindView(R.id.ll_init_content_tip)
    LinearLayout llInitContentTip;
    //正在初始化富文本的提示控件
    @BindView(R.id.ldv_init_count_tip)
    AVLoadingIndicatorView ldvInitCountTip;
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
    LinearLayout llCommentContent;
    //我的评论模块
    @BindView(R.id.ll_comment_mine)
    LinearLayout llCommentMine;
    //我的评论模块
    @BindView(R.id.ll_comment_mine_content)
    LinearLayout llCommentMineContent;
    //文章评论数
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    //评论列表布局recycleView
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //暂无评论
    @BindView(R.id.tv_no_comment)
    TextView tvNoComment;

    //评论编辑模块
    @BindView(R.id.cl_comment_edit)
    ConstraintLayout clCommentEdit;
    //评论总数
    @BindView(R.id.tv_comment_total_count)
    TextView tvCommentTotalCount;

    //收藏
    @BindView(R.id.iv_favorite)
    ImageView ivFavorite;
    //收藏数
    @BindView(R.id.tv_favorite_count)
    TextView tvFavoriteCount;
    //点赞
    @BindView(R.id.iv_like)
    ImageView ivLike;
    //点赞数
    @BindView(R.id.tv_like_count)
    TextView tvLikeCount;

    @BindView(R.id.jz_video)
    JZVideoPlayerStandardHorizontal jzVideo;

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
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //默认Glide配置
    RequestOptions optionsCircle;
//    RequestOptions defaultOptions;

    //话题信息
    TopicInfo topicInfo;
    //关联的测评
    DimensionInfoEntity dimension;

    //视频真实url
    String videoRealUrl;
    //最后加载url的时间戳
    long lastLoadUrlTimestamp = 0;
    //失效间隔
    private static final long INVALID_INTERVAL = 30 * 60 * 1000;

    //最后一次未发送的评论
    private String lastNotSendComment;

    //播放器
    JZExoPlayer jzExoPlayer;


    /**
     * 开启文章详情页面
     *
     * @param articleId
     */
//    public static void startArticleDetailActivity(Context context, String articleId) {
//        startArticleDetailActivity(context, articleId, null, null);
//    }


    /**
     * 开启文章详情页面
     *
     * @param articleId
     */
    public static void startArticleDetailActivity(Context context, String articleId, String ivMainUrl, String articleTitle) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putString(ARTICLE_ID, articleId);
        extras.putString(IV_MAIN_URL, ivMainUrl);
        extras.putString(ARTICLE_TITLE, articleTitle);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_article_detail;
    }

    @Override
    protected String settingTitle() {
        return "文章详情";
    }

    @Override
    public void onInitView() {
        //默认白色
//        setStatusBarBackgroundColor(ArticleDetailActivity.this, Color.parseColor("#ffffff"));
        setStatusBarBackgroundColor(this, Color.TRANSPARENT);
        setStatusBarTextColor();

        //正在加载提示
//        xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        //重载点击监听
        xemptyLayout.setOnReloadListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //正在加载提示
                xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                //加载文章详情
                loadArticleDetail(articleId);
            }
        });

        //隐藏评论模块
        hideCommentBlock();
        //隐藏关联测评模块
        hideRelativeExamView();

        recyclerAdapter = new CommentAdapter(ArticleDetailActivity.this, R.layout.recycleritem_comment, recyclerItem);
        //加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(requestLoadMoreListener, recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(ArticleDetailActivity.this));
        //禁用内嵌的recycleView滚动
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(ArticleDetailActivity.this,DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(ArticleDetailActivity.this,R.drawable.recycler_divider_line_eeeeee));
//        recycleView.addItemDecoration(divider);

        //nestedscrollview滑动监听（禁止了内部的recyclerview的滑动，所以上拉加载得监听它）
        svMainBlock.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    //数量包含加载更多视图
                    if (recyclerAdapter.getItemCount() > 1) {
                        recyclerAdapter.notifyLoadMoreToLoading();
                    }
                }
            }
        });

        optionsCircle = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存

        //默认Glide处理参数
//        defaultOptions = new RequestOptions()
////                .transform(multi)
//                .centerCrop()
//                .skipMemoryCache(false)//不忽略内存
//                .placeholder(R.drawable.default_image_round_article_list)//占位图
//                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
//                .diskCacheStrategy(DiskCacheStrategy.ALL);

        //初始化webView
        initWebView();

        //初始隐藏信息布局
        llInfo.setVisibility(View.INVISIBLE);

//        svMainBlock.setNestedScrollingEnabled(false);
//        svMainBlock.setEnabled(false);
    }


    /**
     * 初始化数据
     */
    @Override
    public void onInitData() {
        articleId = getIntent().getStringExtra(ARTICLE_ID);
        if (TextUtils.isEmpty(articleId)) {
            ToastUtil.showShort(getApplication(), "数据传递有误");
            return;
        }

        ivMainUrl = getIntent().getStringExtra(IV_MAIN_URL);
        articleTitle = getIntent().getStringExtra(ARTICLE_TITLE);
//        if (TextUtils.isEmpty(ivMainUrl) || TextUtils.isEmpty(articleTitle)) {
//            ToastUtil.showShort(ArticleDetailActivity.this, "数据传递有误");
//            return;
//        }

        //主图
        /*Glide.with(this)
                .load(ivMainUrl)
                .apply(defaultOptions)
                .into(ivMain);*/
        //主图
//        if (!TextUtils.isEmpty(ivMainUrl)) {
//            ivMain.setImageURI(ivMainUrl);
////            ivMain.setImageURI("http://img5.imgtn.bdimg.com/it/u=415293130,2419074865&fm=26&gp=0.jpg");
//        } else {
//            ivMain.setActualImageResource(R.drawable.default_image_round_article_list);
//        }
        ivMain.setImageURI(ivMainUrl);
        //文章标题
        tvArticleTitle.setText(articleTitle);

        //加载文章详情
        loadArticleDetail(articleId);

        //加载评论数据
//        articleId = "1";
//        loadCommentData(articleId);

        //处理图片宽高
//        String tempContent = HtmlUtil.getNewContentByHandleImage(testArticleContent);
//        webArticleContent.loadDataWithBaseURL(null, tempContent, "text/html","utf-8",null);

//        initVideo();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //滚动监听
        svMainBlock.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
        //退出全屏监听
        JZVideoPlayerStandardHorizontal.exitFullScreenListener = null;
        //加载更多监听
        recyclerAdapter.setOnLoadMoreListener(null, null);
        requestLoadMoreListener = null;
        //webViewClient监听
        webArticleContent.setWebViewClient(null);
        //重载点击
        xemptyLayout.setOnReloadListener(null);

        //释放资源
//        if (JZMediaManager.instance().jzMediaInterface != null && JZMediaManager.instance().jzMediaInterface instanceof JZExoPlayer) {
//            JZMediaManager.instance().jzMediaInterface.release();
//        }

        if (jzExoPlayer != null) {
            try {
                jzExoPlayer.release();
                jzExoPlayer = null;
                JZVideoPlayer.setMediaInterface(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        if (JZMediaManager.instance().jzMediaInterface != null && JZMediaManager.instance().jzMediaInterface instanceof JZExoPlayer) {
//            JZMediaManager.instance().jzMediaInterface.release();
//        }
    }


    /**
     * 设置状态栏背景颜色
     * （目前只支持5.0以上，4.4到5.0之间由于各厂商存在兼容问题，故暂不考虑）
     * @param activity 页面
     * @param colorStatus 颜色
     */
    protected void setStatusBarBackgroundColor(Activity activity, int colorStatus) {
        //6.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.TRANSPARENT);
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

//            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
//            View mChildView = mContentView.getChildAt(0);
//            if (mChildView != null) {
//                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
//                mChildView.setFitsSystemWindows(true);
//            }

            //5.0及以上
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarBackgroundColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(getResources().getColor(R.color.color_000000));//Color.TRANSPARENT半透明
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }

            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            /*Window window = activity.getWindow();
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusBarView = new View(window.getContext());
            int statusBarHeight = getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            statusBarView.setBackgroundColor(colorStatus);
            decorViewGroup.addView(statusBarView);

            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }*/
        }
    }


    /**
     * 设置状态栏文字以及图标颜色
     */
    protected void setStatusBarTextColor() {
        //设置状态栏文字颜色及图标为深色
        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 是从API 16开始启用，实现效果：
        //视图延伸至状态栏区域，状态栏悬浮于视图之上
        //View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 是从 API 23开始启用，实现效果：
        //设置状态栏图标和状态栏文字颜色为深色，为适应状态栏背景为浅色调，该Flag只有在使用了FLAG_DRWS_SYSTEM_BAR_BACKGROUNDS，
        // 并且没有使用FLAG_TRANSLUCENT_STATUS时才有效，即只有在透明状态栏时才有效。
        //6.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            //设置状态栏文字颜色及图标为浅色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            //4.1及以上
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }


    /**
     * 初始化webView
     */
    private void initWebView() {

        final WebSettings webSettings = webArticleContent.getSettings();
        // 让WebView能够执行javaScript
//        webSettings.setJavaScriptEnabled(true);
        // 让JavaScript可以自动打开windows
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
//        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
//        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
//        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
//        webSettings.setDisplayZoomControls(true);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(18);

        //先阻塞加载图片
        webSettings.setBlockNetworkImage(true);
        //开启硬件加速
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);


        webArticleContent.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return super.shouldOverrideUrlLoading(view, request);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                tvInitContent.setVisibility(View.VISIBLE);
                llInitContentTip.setVisibility(View.VISIBLE);
                ldvInitCountTip.show();

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //不阻塞加载图片
                webSettings.setBlockNetworkImage(false);
                //判断webView是否加载了，图片资源
                if (!webSettings.getLoadsImagesAutomatically()) {
                    //设置wenView加载图片资源
                    webSettings.setLoadsImagesAutomatically(true);
                }

                //                tvInitContent.setVisibility(View.GONE);
                if (llInitContentTip.getVisibility() == View.VISIBLE) {
                    int delay = 200;//默认延迟关闭等待提示布局
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        delay = 200;
                    } else {
                        delay = 400;
                    }
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                                tvInitContent.setVisibility(View.GONE);
                            llInitContentTip.setVisibility(View.GONE);
                            ldvInitCountTip.hide();
                        }
                    }, delay);
                }

                super.onPageFinished(view, url);
            }
        });

//        webArticleContent.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//
//                //会被调用多次
//                if (newProgress == 100) {
//
//                }
//            }
//
//
//        });

    }


    /**
     * 初始化视频
     * @param article
     */
    private void initVideo(final ArticleEntity article) {
        if (article == null || TextUtils.isEmpty(article.getArticleVideo())) {
            jzVideo.setVisibility(View.GONE);
            return;
        } else {
            jzVideo.setVisibility(View.VISIBLE);
        }

        //初始化播放引擎
        if (jzExoPlayer == null) {
            jzExoPlayer = new JZExoPlayer();
        }
        JZVideoPlayer.setMediaInterface(jzExoPlayer);

        //视频地址
        String videoUrl = article.getArticleVideo();
        videoUrl = signVideoUrl(videoUrl);
        final String tempVideoUrl = videoUrl;

        //视频封面
//        String cover = VideoConstant.videoThumbList[0];
        String cover = article.getArticleImg();

        //标题
//        String title  = "饺子不信";
        final String title  = article.getArticleTitle();

//        JZVideoPlayer.releaseAllVideos();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //播放引擎
//                //IjkPlayer
////                JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());
//                //ExoPlayer
//                JZVideoPlayer.setMediaInterface(new JZExoPlayer());
//                //MediaSystem（默认）
////                JZVideoPlayer.setMediaInterface(new JZMediaSystem());
//            }
//        }, 200);
//        JZVideoPlayer.setMediaInterface(new JZExoPlayer());

        jzVideo.setUp(videoUrl
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, title);
//        jzVideo.setUp(VideoConstant.videoUrlList[0]
//                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子不信");

                ;
//        Glide.with(this)
//                .load(cover)
//                .apply(defaultOptions)
//                .into(jzVideo.thumbImageView);

        //封面图
//        if (!TextUtils.isEmpty(ivMainUrl)) {
//            jzVideo.ivMain.setImageURI(ivMainUrl);
//        } else {
//            jzVideo.ivMain.setActualImageResource(R.drawable.default_image_round_article_list);
//        }
        jzVideo.ivMain.setImageURI(ivMainUrl);

//        播放时4GDialog提示
        JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED=false;
        //屏幕方向
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        //视频ID非空
        if (TextUtils.isEmpty(article.getVideoId())) {
            jzVideo.setVisibility(View.GONE);
        } else {
            //设置视频ID和标题、通信标记
            jzVideo.setVideoId(article.getVideoId());
            jzVideo.setTitle(article.getArticleTitle());
            jzVideo.setHttpTag(httpTag);
        }

        //退出全屏监听
        JZVideoPlayerStandardHorizontal.exitFullScreenListener = new JZVideoPlayerStandardHorizontal.ExitFullScreenListener() {
            @Override
            public void onExitFullScreen() {
                //退出全屏，避免滑动偏移
                svMainBlock.scrollTo(0, 0);
            }
        };
//        jzVideo.setExitFullScreenListener();

//        jzVideo.startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                JZVideoPlayerStandard.startFullscreen(ArticleDetailActivity.this, JZVideoPlayerStandard.class, tempVideoUrl, title);
////                mJZVideoPlayerStandard.startButton.performClick();
////                myJZVideoPlayerStandard.startVideo();
////                jzVideo.startWindowFullscreen();
////                jzVideo.startVideo();
//            }
//        });
//        jzVideo.startWindowFullscreen();

//        jzVideo.startButton.setOnClickListener(new OnMultiClickListener() {
//            @Override
//            public void onMultiClick(View view) {
//                jzVideo.onStatePreparing();
//                jzVideo.startButton.performClick();
//                //请求video真实地址
//                queryVideoRealUrl(article.getVideoId(), article.getArticleTitle());
//            }
//        });

//        jzVideo.mRetryBtn.setOnClickListener(new OnMultiClickListener() {
//            @Override
//            public void onMultiClick(View view) {
//                jzVideo.onStatePreparing();
//            }
//        });
    }

    /**
     * 请求视频真实地址
     * @param videoId
     */
    private void queryVideoRealUrl(String videoId, final String title) {
        long curTimestamp = System.currentTimeMillis() / 1000;
        String curTimestampStr = String.valueOf(curTimestamp);
        String key = EnvHostManager.getInstance().getVideoSignKey();
        String sign = signVideoUrl(videoId, key, curTimestampStr);
        //请求视频真实地址
        DataRequestService.getInstance().getVideoRealUrl(videoId, sign, curTimestampStr, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                try {
                    String bodyStr = e.getMessage();
                    Map map = JsonUtil.fromJson(bodyStr, Map.class);
                    ErrorCodeEntity errorCodeEntity = InjectionWrapperUtil.injectMap(map, ErrorCodeEntity.class);

                    //参数错误，若出现错误可获取错误中的时间字段server_time，并转为时间戳使用。
                    if (errorCodeEntity != null && ErrorCode.PSY_INVALID_PARAM.equals(errorCodeEntity.getCode())) {


                    }

                } catch (Exception err) {
                    err.printStackTrace();

                } finally {
                    videoRealUrl = null;
                    jzVideo.onStateError();
                }

            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    String videoRealUrl = (String) dataMap.get("display_url");
                    if (TextUtils.isEmpty(videoRealUrl)) {
                        throw new NullPointerException();
                    }

                    ArticleDetailActivity.this.videoRealUrl = videoRealUrl;
                    jzVideo.setUp(videoRealUrl
                            , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, title);
                    jzVideo.startVideo();

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, ArticleDetailActivity.this);
    }

    /**
     * 获取视频地址的请求签名
     * @param videoId
     * @param key
     * @param timestamp
     * @return
     */
    private String signVideoUrl(String videoId, String key, String timestamp) {
        return EncryptUtil.md5(key + videoId+ timestamp).toLowerCase();
    }

    /**
     * 签名视频url
     *
     * @param url
     * @return
     */
    private String signVideoUrl(String url) {
        String res = "";
        String key = EnvHostManager.getInstance().getVideoSignKey();
        String path = url.substring(url.lastIndexOf("/"));
        long time = System.currentTimeMillis() / 1000;
        time += (1 * 60 * 60);
        int var = (int) time;
        String hex = Integer.toHexString(var).toLowerCase();
        String t = hex;

        String content = key + path + t;
        String sign = EncryptUtil.md5(content).toLowerCase();

        res = url + "?sign=" + sign + "&t=" + t;

        return res;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (jzExoPlayer != null) {
            JZVideoPlayer.releaseAllVideos();
        }

        //Change these two variables back
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
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
        if (JZVideoPlayer.backPress()) {
            //退出全屏，避免滑动偏移
            svMainBlock.scrollTo(0, 0);
            return;
        }
        super.onBackPressed();
    }


    /**
     * 加载文章详情
     *
     * @param articleId
     */
    private void loadArticleDetail(final String articleId) {

        DataRequestService.getInstance().getArticleDetail(articleId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                xemptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    final ArticleEntity articleEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleEntity.class);

                    //视频视图处理
                    initVideo(articleEntity);

                    //刷新文章视图
                    refreshArticleView(articleEntity);
                    //刷新收藏视图
                    refreshFavoriteView(articleEntity.isFavorite(), articleEntity.getPageFavorite());
                    //刷新点赞视图
                    refreshLikeView(articleEntity.isLike(), articleEntity.getPageLike());

                    int delay = 150;//默认延迟加载关联测评和评论
                    //文章内容过长，则加长延迟时间
                    if (!TextUtils.isEmpty(articleEntity.getArticleContent())
                            && articleEntity.getArticleContent().length() > 300) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            delay = 300;
                        } else {
                            delay = 500;
                        }
                    }
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //有关联的测评，则刷新关联测评的视图
                            if (articleEntity.getTopicInfo() != null) {
                                //refreshRelativeExamView(articleEntity);
                                topicInfo = articleEntity.getTopicInfo();
                                //请求关联测评
                                getReferenceExam(topicInfo);
                            }

                            //文章评论模块开启则加载评论信息
                            if (articleEntity.isShowComment()) {
                                //加载评论
                                loadCommentData(articleId);
                            }
                        }
                    }, delay);

                } catch (Exception e) {
                    e.printStackTrace();
                    //视为找不到文章数据
                    xemptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag,ArticleDetailActivity.this);
    }


    /**
     * 请求关联测评
     * @param topicInfo
     */
    private void getReferenceExam(final TopicInfo topicInfo) {
        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().getChildDimension(childId, topicInfo.getTopicId(), topicInfo.getDimensionId(), new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //隐藏关联量表模块
                hideRelativeExamView();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    DimensionInfoEntity dimension = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoEntity.class);

                    if (dimension == null || TextUtils.isEmpty(dimension.getDimensionId())) {
                        throw new QSCustomException("量表数据为空");
                    }

                    ArticleDetailActivity.this.dimension = dimension;

                    //刷新关联评测视图
                    refreshRelativeExamView(dimension);

                } catch (QSCustomException e) {
                    onFailure(e);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag,ArticleDetailActivity.this);
    }


    /**
     * 点赞操作
     *
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
                    refreshLikeView((Boolean) dataMap.get("is_like"), Double.valueOf(dataMap.get("page_like").toString()).intValue());

                } catch (Exception e) {
                    e.printStackTrace();
                    //操作失败
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag,ArticleDetailActivity.this);
    }

    /**
     * 刷新点赞视图
     *
     * @param isLike
     * @param likeCount
     */
    private void refreshLikeView(boolean isLike, int likeCount) {
        if (isLike) {
            //点赞状态
            ivLike.setImageResource(R.drawable.like_do);

        } else {
            //未点赞状态
            ivLike.setImageResource(R.drawable.like_not);
        }

        //点赞数量
        if (likeCount > 0) {
            tvLikeCount.setVisibility(View.VISIBLE);
            tvLikeCount.setText(String.valueOf(likeCount));
        }else {
            tvLikeCount.setVisibility(View.GONE);
        }
    }

    /**
     * 收藏操作
     *
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
                    refreshFavoriteView((Boolean) dataMap.get("is_favorite"), ((Double) dataMap.get("page_favorite")).intValue());

                } catch (Exception e) {
                    e.printStackTrace();
                    //操作失败
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag,ArticleDetailActivity.this);
    }


    /**
     * 刷新收藏视图
     * @param isFavorite 是否收藏
     * @param favoriteCount 收藏数量
     */
    private void refreshFavoriteView(boolean isFavorite, int favoriteCount) {
        if (isFavorite) {
            //收藏状态
            ivFavorite.setImageResource(R.drawable.favorite_do);
        } else {
            //未收藏状态
            ivFavorite.setImageResource(R.drawable.favorite_not);
        }

        if (favoriteCount > 0) {
            tvFavoriteCount.setVisibility(View.VISIBLE);
            tvFavoriteCount.setText(String.valueOf(favoriteCount));
        }else {
            tvFavoriteCount.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新关联测评的视图
     *
     * @param dimension
     */
    private void refreshRelativeExamView(DimensionInfoEntity dimension) {
        //显示布局
        rlEvaluation.setVisibility(View.VISIBLE);
        //标题
        tvDimensionName.setText(dimension.getDimensionName());
        //使用人数
        if (dimension.getUseCount() > 0) {
            tvUsedCount.setText(getResources().getString(R.string.exam_dimension_use_count, dimension.getUseCount() +""));
        } else {
            tvUsedCount.setText(getResources().getString(R.string.exam_dimension_use_count, "0"));
            tvUsedCount.setVisibility(View.GONE);
        }

        //根据量表状态改变按钮文字
        //未开始
        if (dimension.getChildDimension() == null) {
            btnGotoEvaluation.setText("开始测评");
        } else if (dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
            btnGotoEvaluation.setText("继续测评");
        } else if (dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
            btnGotoEvaluation.setText("查看报告");
        }
    }

    /**
     * 隐藏关联测评的视图
     */
    private void hideRelativeExamView() {
        rlEvaluation.setVisibility(View.GONE);
    }

    /**
     * 刷新文章视图
     *
     * @param articleEntity
     */
    private void refreshArticleView(ArticleEntity articleEntity) {
        if (articleEntity == null) return;
        //标题
        tvArticleTitle.setText(articleEntity.getArticleTitle());

        //显示信息布局
        llInfo.setVisibility(View.VISIBLE);
        //作者（来源）
        if (!TextUtils.isEmpty(articleEntity.getSourceName())) {
            tvAuthor.setVisibility(View.VISIBLE);
            tvAuthor.setText(articleEntity.getSourceName());
        } else {
            tvAuthor.setVisibility(View.GONE);
        }
        //发布日期
        if (!TextUtils.isEmpty(articleEntity.getPublishDate())) {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(articleEntity.getPublishDate());
        } else {
            tvDate.setVisibility(View.GONE);
        }

        //阅读数
        if (articleEntity.getPageView() > 0) {
            llReadCount.setVisibility(View.VISIBLE);
            tvReadCount.setText(String.valueOf(articleEntity.getPageView()));
        } else {
            llReadCount.setVisibility(View.GONE);
        }

        //文章内容（富文本）
        //处理图片宽高
//        String tempContent = testArticleContent;
        String tempContent = articleEntity.getArticleContent();
        if (!TextUtils.isEmpty(tempContent)) {
            tempContent = tempContent.replaceAll("\t", "");
            tempContent = tempContent.replaceAll("\n", "");
            if (!TextUtils.isEmpty(tempContent)) {
                tempContent = getNewContent(tempContent, webArticleContent.getMeasuredWidth() - DensityUtil.dip2px(ArticleDetailActivity.this, 100));
//                tvArticleContent.setText(Html.fromHtml(tempContent, new URLImageParser(ArticleDetailActivity.this, tvArticleContent), null));
                webArticleContent.loadData(tempContent, "text/html; charset=UTF-8", "utf-8");
            } else {
                tvArticleContent.setVisibility(View.GONE);
            }
        } else {
            tvArticleContent.setVisibility(View.GONE);
        }

//        tempContent = HtmlUtil.getNewContentByHandleImage(tempContent);
//        webArticleContent.loadDataWithBaseURL(null, tempContent, "text/html","utf-8",null);
    }


    /**
     * 重新处理内容文本
     * @param htmlText html文本
     * @param maxWidth 控件宽度
     * @return 返回新文本
     */
    private String getNewContent(String htmlText, int maxWidth){

        Document doc= Jsoup.parse(htmlText);
        //图片处理
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            String widthStr = element.attr("width");
            int width = 0;
            try {
                if (!TextUtils.isEmpty(widthStr)) {
                    width = Integer.parseInt(widthStr);
                }
                if (width == 0) {
                    widthStr = element.attr("img_width");
                    if (!TextUtils.isEmpty(widthStr)) {
                        width = Integer.parseInt(widthStr);
                    }
                }
            } catch (Exception e) {
            }
            if (width == 0 || width > 400) {
                element.attr("width", "100%").attr("height", "auto");
            }
        }

        //段落处理
        Elements pElements = doc.getElementsByTag("p");
        for (Element element : pElements) {
            element.attr("style", "line-height:160%");
        }

//        Log.d("VACK", doc.toString());
        return doc.toString();
    }


    /**
     * 加载评论列表
     *
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
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CommentRootEntity commentRootEntity = InjectionWrapperUtil.injectMap(dataMap, CommentRootEntity.class);

                    totalCount = commentRootEntity.getTotal();
                    List<CommentEntity> dataList = commentRootEntity.getItems();
                    //无数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        //显示评论模块
                        showCommentBlock();
                        //刷新评论总数
                        refreshCommentTotalCountView(totalCount);
                        //暂无评论
                        settingNoCommentView();
                        return;
                    }

                    //添加数据
                    recyclerAdapter.addData(dataList);

                    //判断是否全部加载结束
                    if (recyclerItem.size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;

                    //显示评论模块
                    showCommentBlock();
                    //刷新评论总数
                    refreshCommentTotalCountView(totalCount);
                    //显示评论内容
                    settingHasCommentView();

                } catch (Exception e) {
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }

            }
        }, httpTag,ArticleDetailActivity.this);
    }

    /**
     * 刷新评论总数
     */
    private void refreshCommentTotalCountView(int totalCount) {
        //评论列表header中的评论总数
        tvCommentCount.setText(getResources().getString(R.string.comment_count, totalCount + ""));
        //编辑模块中的评论总数
        if (totalCount > 0) {
            tvCommentTotalCount.setVisibility(View.VISIBLE);
            tvCommentTotalCount.setText(String.valueOf(totalCount));
        } else {
            tvCommentTotalCount.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 隐藏评论模块
     */
    private void hideCommentBlock() {
        //评论内容模块
        llCommentContent.setVisibility(View.GONE);
        //编辑模块
        clCommentEdit.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示评论模块
     */
    private void showCommentBlock() {
        //评论内容模块
        llCommentContent.setVisibility(View.VISIBLE);
        //编辑模块
        clCommentEdit.setVisibility(View.VISIBLE);
    }

    /**
     * 有评论内容时的视图设置
     */
    private void settingHasCommentView() {
        //评论列表
        recycleView.setVisibility(View.VISIBLE);
        //无评论提示
        tvNoComment.setVisibility(View.GONE);
    }

    /**
     * 没有评论内容时的视图设置
     */
    private void settingNoCommentView() {
        //隐藏评论列表
        recycleView.setVisibility(View.GONE);
        //无评论提示
        tvNoComment.setVisibility(View.VISIBLE);
    }


    /**
     * 弹出评论对话框
     */
    private void popupCommentEditWindows() {
        View view = LayoutInflater.from(ArticleDetailActivity.this).inflate(R.layout.popup_window_comment_edit, null);
        final Dialog dialog = new Dialog(this, R.style.dialog_bottom_full);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
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
        btnSend.setEnabled(false);//初始设置为未激活状态
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //最后一次未发送的评论
                lastNotSendComment = etComment.getText().toString();
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
                //最后一次未发送的评论
                lastNotSendComment = etComment.getText().toString();
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

        //初始化最后一次未发送的评论
        if (!TextUtils.isEmpty(lastNotSendComment)) {
            etComment.setText(lastNotSendComment);
            etComment.setSelection(lastNotSendComment.length());
        }

//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface iDialog) {
//                btnClose.setOnClickListener(null);
//                btnSend.setOnClickListener(null);
//                BaseActivity.fixInputMethodManagerLeak(dialog.getContext());
//            }
//        });
    }

    /**
     * 提交评论
     *
     * @param articleId
     * @param content
     */
    private void doComment(String articleId, String content) {
        DataRequestService.getInstance().postDoComment(articleId, content, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
//                ToastUtil.showShort(getApplicationContext(), "评论失败");
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CommentEntity commentEntity = InjectionWrapperUtil.injectMap(dataMap, CommentEntity.class);

                    ToastUtil.showShort(getApplication(), "评论成功");
                    //清空最后一次未发送的评论
                    lastNotSendComment = "";
                    //刷新我的评论视图
                    refreshMineComment(commentEntity);
                    //滚动到我的评论
                    scrollToMineComment();

                } catch (Exception e) {
                    //操作失败
                    e.printStackTrace();
                    ToastUtil.showShort(getApplication(), "评论失败");
                }
            }
        }, httpTag,ArticleDetailActivity.this);
    }

    /**
     * 刷新我的评论视图
     * @param item
     */
    private void refreshMineComment(CommentEntity item) {
        //显示我的评论模块
        if (llCommentMine.getVisibility() == View.GONE) {
            llCommentMine.setVisibility(View.VISIBLE);
        }

        //添加我的评论
        View commentItemView = View.inflate(ArticleDetailActivity.this, R.layout.recycleritem_comment, null);

        //用户信息（发布评论者）
        if (item.getUserData() != null) {
            //头像
            String avatar = item.getUserData().getAvatar();
            ImageView imageView = commentItemView.findViewById(R.id.iv_profile);
            if (!TextUtils.isEmpty(avatar)) {
                GlideUrl glideUrl = new GlideUrl(avatar, new LazyHeaders.Builder()
                        .addHeader(Dictionary.PROFILE_HEADER_KEY, Dictionary.PROFILE_HEADER_VALUE)
                        .build());

                Glide.with(ArticleDetailActivity.this)
                        .load(glideUrl)
//                    .thumbnail(0.5f)
                        .apply(optionsCircle)
                        .into(imageView);
            } else {
                Glide.with(ArticleDetailActivity.this)
                        .load(R.drawable.ico_head)
//                    .thumbnail(0.5f)
                        .apply(optionsCircle)
                        .into(imageView);
            }

            //用户名
            String nickName = item.getUserData().getNickName();
            if (!TextUtils.isEmpty(nickName)) {
                TextView textView = commentItemView.findViewById(R.id.tv_username);
                textView.setText(nickName);
            } else {
                commentItemView.findViewById(R.id.tv_username).setVisibility(View.GONE);
            }
        }

        //发布时间
        if (!TextUtils.isEmpty(item.getCreateTime())) {
            TextView textView = commentItemView.findViewById(R.id.tv_send_date);
            textView.setText(item.getCreateTime());
        } else {
            commentItemView.findViewById(R.id.tv_send_date).setVisibility(View.GONE);
        }

        //发布内容
        if (!TextUtils.isEmpty(item.getCommentInfo())) {
            TextView textView = commentItemView.findViewById(R.id.tv_content);
            textView.setText(item.getCommentInfo());
        } else {
            commentItemView.findViewById(R.id.tv_content).setVisibility(View.GONE);
        }

        //分割线
        if (llCommentMineContent.getChildCount() > 0) {
            commentItemView.findViewById(R.id.tv_divider_line).setVisibility(View.VISIBLE);
        } else {
            commentItemView.findViewById(R.id.tv_divider_line).setVisibility(View.GONE);
        }
        //添加评论
        llCommentMineContent.addView(commentItemView, 0);

    }


    //测试的文章内容
//    String testArticleContent = "<div class=\"article\" id=\"article\">\t\t\t\t\n" +
//            "<p>　　原标题：外交部今天再谈中非“全家福”，说了一句意味深长的话</p>\n" +
////            "<div class=\"img_wrapper\"><img style=\"max-width: 500px;\" src=\"http://n.sinaimg.cn/translate/784/w500h284/20180821/OHhj-hhzsnea3625408.jpg\" alt=\"\"></div>\n" +
//            "<img style=\"max-width: 500px;\" src=\"http://n.sinaimg.cn/translate/784/w500h284/20180821/OHhj-hhzsnea3625408.jpg\" alt=\"\">" +
//            "<p>　　[环球网综合报道]2018年8月21日外交部发言人陆慷主持例行记者会，有记者问到，问：第一，台湾方面认为中国与萨尔瓦多建交与大陆“金元外交”有关。外交部发言人对此有何回应？第二，今年年初，王毅国务委员兼外长表示希望在中非合作论坛北京峰会期间拍一张中非“全家福”。这次斯威士兰是否将与会？</p>\n" +
////            "<p>　　陆慷回应，关于第一个问题，相信你已经关注到了今天上午王毅国务委员兼外长与卡斯塔内达外长共见记者的详细情况。按说对台湾岛内某些人的表态，应由中央政府主管部门作回应，不是外交部的职责。但就这一问题而言，我可以强调一下，中国和萨尔瓦多建立外交关系是政治决断，绝不是台湾方面某些人想象的所谓“交易筹码”。萨尔瓦多政府决定同中国建交，是出于对一个中国原则的认同，是一项政治决断，没有任何经济前提。中方高度赞赏萨方的立场。</p>\n" +
////            "<p>　　我也想提醒台湾方面某些人，世界各国同中华人民共和国建立和发展正常友好的国家关系是大势所趋、民心所向，希望岛内某些人能够看清世界大势，不要把任何问题都理解为钱的问题。</p>\n" +
//            "<p>　　至于你提到现在还有一个非洲国家仍然没有放弃同台湾的所谓“外交关系”，我们已经多次表明，中国政府有意愿在和平共处五项原则和一个中国原则基础上，同世界上所有国家发展友好合作关系。我们相信这样的关系符合彼此利益，希望有关国家能够认清世界大势。</p>\n" +
//            "\t\t\t\t\n" +
//            "\t\t\t\t<div class=\"wap_special\" data-sudaclick=\"content_relativetopics_p\">\n" +
//            "        <div class=\"tlt\">点击进入专题：</div>\n" +
//            "\t<a href=\"http://news.sina.cn/zt_d/zfhzlt\" target=\"_blank\">习近平将主持2018年中非合作论坛北京峰会</a></div>\n" +
//            "  \n" +
//            "\t\t\t\t\n" +
//            "\t\t\t\t\n" +
//            "<p class=\"show_author\">责任编辑：张建利 </p>\n" +
//            "\t\t\t\t\n" +
//            "\t\t\t\t\n" +
//            "\t\t\t</div>";


    @OnClick({R.id.btn_goto_evaluation, R.id.tv_comment_tip, R.id.iv_favorite, R.id.iv_like,
            R.id.fl_comment_total_count,R.id.iv_comment_edit_tip, R.id.iv_close })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //跳转到关联的测评（量表）
            case R.id.btn_goto_evaluation: {
                //未锁定
                if (dimension.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_NO) {
                    if (dimension != null && dimension.getChildDimension() != null
                            && dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
                        //查看报告
                        queryDimensionReport(dimension);

                    } else {
                        //进入量表详情页面
                        TopicInfoEntity topicInfoEntity = new TopicInfoEntity();
                        //话题ID
                        topicInfoEntity.setTopicId(topicInfo.getTopicId());
                        //测评ID
                        topicInfoEntity.setExamId(dimension.getExamId());
                        DimensionDetailActivity.startDimensionDetailActivity(
                                ArticleDetailActivity.this, dimension,
                                topicInfoEntity,
                                Dictionary.EXAM_STATUS_DOING,
                                Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);
                    }
                } else {
                    //已锁定
                    ToastUtil.showShort(getApplication(), "该测评被锁定");
                }

                break;
            }
            //编辑模块中的评论总数布局
            case R.id.fl_comment_total_count: {
                scrollToMineComment();
                break;
            }
            //触发评论对话框的提示文本
            case R.id.tv_comment_tip: {
                //弹出评论编辑弹窗
                popupCommentEditWindows();
                break;
            }
            //收藏
            case R.id.iv_favorite: {
                if (TextUtils.isEmpty(articleId)) {
                    return;
                }
                doFavorite(articleId);
                break;
            }
            //点赞
            case R.id.iv_like: {
                if (TextUtils.isEmpty(articleId)) {
                    return;
                }
                doLike(articleId);
                break;
            }
            //铅笔图标（测试）
//            case R.id.iv_comment_edit_tip : {
//                svMainBlock.scrollTo(0, 0);
//                break;
//            }
            //关闭按钮
            case R.id.iv_close: {
                finish();
                break;
            }
        }

    }


    /**
     * 滚动到我的评论
     */
    private void scrollToMineComment() {
        svMainBlock.post(new Runnable() {
            @Override
            public void run() {
                int temp = llCommentContent.getTop();
                svMainBlock.scrollTo(0,  temp);
            }
        });
    }


    /**
     * 请求量表报告
     * @param dimensionInfo 分量表对象，一定得带孩子量表对象
     */
    private void queryDimensionReport(DimensionInfoEntity dimensionInfo) {
        try {
            new DimensionReportDialog(ArticleDetailActivity.this, dimensionInfo, new DimensionReportDialog.OnOperationListener() {
                @Override
                public void onExit() {

                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplication(), e.getMessage());
        }
    }


}

