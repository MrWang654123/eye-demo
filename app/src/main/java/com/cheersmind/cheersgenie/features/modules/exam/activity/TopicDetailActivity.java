package com.cheersmind.cheersgenie.features.modules.exam.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.TopicDetail;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 场景（话题）详情页面
 */
public class TopicDetailActivity extends BaseActivity {

    private static final String TOPIC_ID = "topic_id";
    //话题ID
    private String topicId;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.tv_topic_name)
    TextView tvTopicName;
    @BindView(R.id.tv_suitable_user)
    TextView tvSuitableUser;
    @BindView(R.id.tv_used_count)
    TextView tvUsedCount;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_desc)
    ImageView ivDesc;
    @BindView(R.id.tv_content_bottom)
    TextView tvContentBottom;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;


    /**
     * 启动话题详情页面
     *
     * @param context
     * @param topicId 话题ID
     */
    public static void startTopicDetailActivity(Context context, String topicId) {
        Intent intent = new Intent(context, TopicDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putString(TOPIC_ID, topicId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_topic_detail;
    }

    @Override
    protected String settingTitle() {
        return "场景详情";
    }

    @Override
    protected void onInitView() {
        //初始隐藏scrollView
        scrollView.setVisibility(View.GONE);
    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }

        topicId = getIntent().getExtras().getString(TOPIC_ID);
        if (TextUtils.isEmpty(topicId)) {
            ToastUtil.showShort(getApplicationContext(), "话题ID不能为空");
            return;
        }

        //加载话题详情
        loadTopicDetail(topicId);
    }

    /**
     * 获取话题详情
     * @param topicId
     */
    private void loadTopicDetail(String topicId) {
        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
        DataRequestService.getInstance().getTopicDetail(topicId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map<String, Object> dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TopicDetail topicDetail = InjectionWrapperUtil.injectMap(dataMap, TopicDetail.class);

                    //刷新话题详细视图
                    refreshTopicDetailView(topicDetail);

                } catch (Exception e) {
                    e.printStackTrace();
                    emptyLayout.setErrorType(XEmptyLayout.NODATA);
                }
            }
        });
    }

    /**
     * 刷新话题详细视图
     * @param topicDetail
     */
    private void refreshTopicDetailView(TopicDetail topicDetail) {
        //显示scrollView
        scrollView.setVisibility(View.VISIBLE);
        tvTopicName.setText(topicDetail.getTopicName());
//        tvContent.setText(Dictionary.Text_Indent + topicDetail.getDescription());
        tvContentBottom.setText(Dictionary.Text_Indent + topicDetail.getDescription());
        tvSuitableUser.setText("#适合对象：高中生");
        tvUsedCount.setText(getResources().getString(R.string.exam_dimension_use_count, topicDetail.getUseCount()+""));

//        Glide.with(TopicDetailActivity.this)
//                .load(topicDetail.getIcon())
//                .thumbnail(0.5f)
//                .apply(QSApplication.getDefaultOptions())
//                .into(ivDesc);
        ivDesc.setImageResource(R.drawable.default_image_round);
    }


    //测试网络图片
    String testImageUrl = "https://goss.veer.com/creative/vcg/veer/800water/veer-122247916.jpg";

    //测试文章
    String testArticle = "<div class=\"article\" id=\"article\">\n" +
            "\t\t\t\t<p>　　原标题：<a href=\"http://news.sina.com.cn/c/2018-08-20/doc-ihhxaafy9187605.shtml\" target=\"_blank\">帕劳</a>拒绝与台湾断交：我们不会选择“一个中国”政策</p>\n" +
            "<p>　　据路透社报道，空旷的酒店客房，闲置的旅游船和关闭的旅行社揭示了太平洋小国帕劳旅游业的现状，而造成这样的原因是因为中国大陆与台湾之间的外交拉锯战不断升级。帕劳作为目前世界上与台湾“建交”的18个国家之一，拒绝承认“一个中国”这导致帕劳的旅游业受损严重。</p>\n" +
            "<div class=\"img_wrapper\"><img src=\"http://n.sinaimg.cn/news/transform/55/w550h305/20180820/rXOA-hhxaafy8344195.jpg\" alt=\"▲帕劳酒店外用于接待中国旅游团的大巴车如今空空荡荡\" data-mcesrc=\"http://n.sinaimg.cn/translate/79/w1080h599/20180820/Yry0-hhxaafy8321219.jpg\" data-mcestyle=\"max-width: 500px;\" data-mceselected=\"1\" data-link=\"\"><span class=\"img_descr\">▲帕劳酒店外用于接待中国旅游团的大巴车如今空空荡荡</span></div>\n" +
            "<p>　　据路透社透露从去年年底开始，中国旅游团已经很少再到这个田园诗般的热带群岛。根据帕劳旅游局的官方数据显示，在2017年的122，000名访客中，约有55，000名来自中国大陆，而大约有9，000名来自台湾。而此前来自中国大陆投资者也曾建立酒店，开业和保护大片优质沿海房地产，不过目前大部分项目已经暂停。</p>\n" +
            "<div class=\"img_wrapper\"><img src=\"http://n.sinaimg.cn/news/transform/110/w550h360/20180820/S3dq-hhxaafy8344486.jpg\" alt=\"▲帕劳此前曾是中国旅游热门目的地之一\" data-mcesrc=\"http://n.sinaimg.cn/translate/574/w830h544/20180820/8dXs-hhxaafy8321262.jpg\" data-mcestyle=\"max-width: 500px;\" data-mceselected=\"1\" data-link=\"\"><span class=\"img_descr\">▲帕劳此前曾是中国旅游热门目的地之一</span></div>\n" +
            "<p>　　由于中国大陆游客下降幅度非常大，帕劳太平洋航空公司作为主要的旅游包机业务提供商在7月底宣布已经终止飞往中国大陆的航班。</p>\n" +
            "<div class=\"img_wrapper\"><img src=\"http://n.sinaimg.cn/news/transform/115/w550h365/20180820/t4lw-hhxaafy8345052.jpg\" alt=\"▲由台湾负责建设的帕劳新总统府与美国白宫十分接近\" data-mcesrc=\"http://n.sinaimg.cn/translate/363/w699h464/20180820/X59G-hhxaafy8321279.jpg\" data-mcestyle=\"max-width: 500px;\" data-mceselected=\"1\" data-link=\"\"><span class=\"img_descr\">▲由台湾负责建设的帕劳新总统府与美国白宫十分接近</span></div>\n" +
            "<p>　　帕劳总统汤米·雷门格绍在接受记者采访时表示：“中国希望我们切断和台湾的外交关系朋友，这并不是秘密，但对帕劳而言，我们不会选择支持一个中国的政策。”帕劳正在通过吸引于高消费游客而不是大规模旅游来适应游客下降的情况，理由是大量的游客对帕劳环境造成了影响。不过这项政策显然并不是那么奏效，因为目前帕劳每年从台湾获得1000万美元，以及教育和医疗奖学金。而面对曾经繁荣的度假村和暂停的中国大陆建设项目，帕劳前驻台“大使”和帕劳游客管理局前主席杰罗姆·亨利表示：“帕劳希望成为台湾和中国的朋友。”</p>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t  \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "<p class=\"show_author\">责任编辑：余鹏飞 </p>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t</div>";


}
