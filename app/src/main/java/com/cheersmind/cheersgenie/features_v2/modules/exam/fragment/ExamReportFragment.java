package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.ChartItemDesc;
import com.cheersmind.cheersgenie.features.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.ChartUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamReportRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.ActType;
import com.cheersmind.cheersgenie.features_v2.entity.ExamMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ExamReportRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ReportRecommendActType;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubTitleEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.FactorResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 测评报告（话题、量表通用）
 */
public class ExamReportFragment extends LazyLoadFragment {

    //报告dto
    private ExamReportDto reportDto;

    //报告列表
    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //报告列表footer
    View footerReportView;
    //文章列表布局
    LinearLayout llArticle;

    Unbinder unbinder;

    //报告列表
    List<List<ReportItemEntity>> recyclerItem = new ArrayList<>();//每个报告可能不只一个图表
    //适配器
    private ExamReportRecyclerAdapter recyclerAdapter;

    //默认Glide处理参数
    private RequestOptions defaultOptions;

    /**
     * 初始化默认Glide处理参数
     */
    private void initRequestOptions(Context context) {
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCornersTransformation(DensityUtil.dip2px(context, 3), 0, RoundedCornersTransformation.CornerType.ALL));
        //默认Glide处理参数
        defaultOptions = new RequestOptions()
                .skipMemoryCache(false)//不忽略内存
                .placeholder(R.drawable.default_image_round_article_list)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存策略：缓存所有
                .transform(multi);
//                .centerCrop();
    }

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity multiItem = recyclerAdapter.getData().get(position);
            //当前是话题报告
            if (Dictionary.REPORT_TYPE_TOPIC.equals(reportDto.getRelationType())
                    && multiItem.getItemType() == Dictionary.CHART_SUB_ITEM) {
                ReportSubItemEntity item = (ReportSubItemEntity) multiItem;
                ExamReportDto dto = new ExamReportDto();
                dto.setChildExamId(reportDto.getChildExamId());
                dto.setCompareId(reportDto.getCompareId());
                dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
                dto.setRelationId(item.getItem_id());
                //跳转到报告页面量表
                ExamReportActivity.startExamReportActivity(getContext(), dto);
            }
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity multiItem = recyclerAdapter.getData().get(position);

            switch (view.getId()) {
                //子项伸缩
                case R.id.iv_expand: {
                    if (multiItem.getItemType() == Dictionary.CHART_SUB_ITEM) {
                        ReportSubItemEntity item = (ReportSubItemEntity) multiItem;
                        item.setExpand(!item.isExpand());
                        int layoutPos = recyclerAdapter.getHeaderLayoutCount() + position;
                        recyclerAdapter.notifyItemChanged(layoutPos);
                    }
                    break;
                }
                //图表说明伸缩
                case R.id.tv_ctrl_chart_desc: {
                    if (multiItem.getItemType() == Dictionary.CHART_DESC) {
                        ChartItemDesc item = (ChartItemDesc) multiItem;
                        item.setExpand(!item.isExpand());
                        int layoutPos = recyclerAdapter.getHeaderLayoutCount() + position;
                        recyclerAdapter.notifyItemChanged(layoutPos);
                    }
                    break;
                }
            }
        }
    };



    @Override
    protected int setContentView() {
        return R.layout.fragment_exam_report;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                reportDto = (ExamReportDto) bundle.getSerializable(DtoKey.EXAM_REPORT_DTO);
                if (reportDto == null) {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //空布局：无数据可重载
            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
            return;
        }

        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载报告
                loadReport(reportDto);
                //加载报告推荐文章
//                loadReportRecommendArticle(reportDto);
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //适配器
        recyclerAdapter = new ExamReportRecyclerAdapter(getContext(), null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recycleView.setAdapter(recyclerAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);

        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //报告列表footer（推荐文章）
        footerReportView = LayoutInflater.from(getContext()).inflate(R.layout.recycler_footer_article_recommend, null);
        llArticle = footerReportView.findViewById(R.id.ll_article);
        recyclerAdapter.addFooterView(footerReportView);
        footerReportView.setVisibility(View.GONE);//初始隐藏footer

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        //初始化默认Glide处理参数
        initRequestOptions(getContext());
    }

    @Override
    protected void lazyLoad() {
        //加载报告
        loadReport(reportDto);
        //加载报告推荐文章
//        loadReportRecommendArticle(reportDto);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        //取消当前对话框的所有通信
        BaseService.cancelTag(httpTag);
    }


    /**
     * 加载报告
     * @param dto 报告dto
     */
    private void loadReport(final ExamReportDto dto) {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        try {
            DataRequestService.getInstance().getReportV2New(dto,
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        //空布局：网络连接有误，或者请求失败
                        emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        //空布局：隐藏
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                        try {
                            //测试数据
//                            Map dataMap = JsonUtil.fromJson(testDataStr, Map.class);
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            ExamReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ExamReportRootEntity.class);

                            //无数据处理
                            if (data == null) {
                                //空布局：无数据
                                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                                return;
                            }

//                            //报告结果
//                            List<ReportResultEntity> reportResultEntities = data.getReportResults();
//                            //报告图表数据
//                            List<ReportItemEntity> reportItems = data.getChartDatas();

//                            //拼接因子结果文本
//                            formatFactorResultText(reportResultEntities);
//                            //把报告结果置于报告图表对象中
//                            reportItems = settingResultToReportItem(reportItems, reportResultEntities);
//                            //初始图表说明是否展开（没有评价则展开）
//                            initDescIsExpand(reportItems);
//                            //把比较名称置于报告图表对象中
//                            settingCompareName(reportItems, data);
                            //把reportItems分组，每个量表可能不只一个图表
//                            recyclerItem = groupReportItem(reportItems);
//                            //如果topic报告项没有报告结果（ReportResultEntity中的title），则提取所有量表项的结果作为结果
//                            fetchDimensionResultToTopicResult(recyclerItem);

                            //标记是否是话题
                            data.setTopic(Dictionary.REPORT_TYPE_TOPIC.equals(dto.getRelationType()));
                            //数据转换
                            List<MultiItemEntity> multiItemEntities = examReportRootToRecyclerMulti(data);
                            //标记是否是话题
                            recyclerAdapter.setTopic(Dictionary.REPORT_TYPE_TOPIC.equals(dto.getRelationType()));
                            //目前每次都是重置列表数据
                            recyclerAdapter.setNewData(multiItemEntities);

                        } catch (Exception e) {
                            e.printStackTrace();
                            //空布局：加载失败
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                        }
                    }
                }, httpTag, getActivity());

        } catch (Exception e) {
            e.printStackTrace();
            //空布局：网络连接有误，或者请求失败
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
        }
    }


    /**
     * 如果topic报告项没有报告结果（ReportResultEntity中的title），则提取所有量表项的结果作为结果
     * @param recyclerItem
     */
    private void fetchDimensionResultToTopicResult(List<List<ReportItemEntity>> recyclerItem) {
        if (ArrayListUtil.isNotEmpty(recyclerItem)) {
            //所有量表项的结果作为结果文本
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("【");

            for (List<ReportItemEntity> list : recyclerItem) {
                //非空
                if (ArrayListUtil.isNotEmpty(list)) {
                    //非topic项
                    if (!list.get(0).isTopic() && list.get(0).getReportResult() != null
                            && !TextUtils.isEmpty(list.get(0).getReportResult().getTitle())) {
                        stringBuilder.append(list.get(0).getReportResult().getTitle());
                        stringBuilder.append("、");
                    }
                }
            }

            if (stringBuilder.length() > 1) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
//                stringBuilder.substring(0,stringBuilder.length() - 2);
            }
            stringBuilder.append("】");

            //量表项的结果文本有数据的情况才继续
            if (stringBuilder.length() > 2) {
                for (List<ReportItemEntity> list : recyclerItem) {
                    //非空
                    if (ArrayListUtil.isNotEmpty(list)) {
                        //是topic项，且ReportResultEntity为空，则赋值
                        if (list.get(0).isTopic() && list.get(0).getReportResult() == null) {
                            ReportResultEntity reportResult = new ReportResultEntity();
                            reportResult.setTitle(stringBuilder.toString());
                            list.get(0).setReportResult(reportResult);
                        }
                    }
                }
            }

        }
    }

    /**
     * 初始图表说明是否展开（没有评价则展开）
     * @param reportItems
     */
    private void initDescIsExpand(List<ReportItemEntity> reportItems) {
        //非空
        if (ArrayListUtil.isNotEmpty(reportItems)) {
            for (ReportItemEntity reportItem : reportItems) {
                //评价
                if (reportItem.getReportResult() == null || TextUtils.isEmpty(reportItem.getReportResult().getContent())) {
                    reportItem.setExpandDesc(true);
                } else {
                    reportItem.setExpandDesc(false);
                }
            }
        }
    }


    /**
     * 拼接因子结果文本
     * @param reportResultEntities
     */
    private void formatFactorResultText(List<ReportResultEntity> reportResultEntities) {
        if (ArrayListUtil.isNotEmpty(reportResultEntities)) {
            for (ReportResultEntity reportResult : reportResultEntities) {
                //因子结果集合非空，且数量大于1
                if (ArrayListUtil.isNotEmpty(reportResult.getFactorResultList()) && reportResult.getFactorResultList().size() > 1) {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i=0; i<reportResult.getFactorResultList().size(); i++) {
                        FactorResultEntity factorResult = reportResult.getFactorResultList().get(i);

                        stringBuilder.append("<strong>");
                        stringBuilder.append((i+1));
                        stringBuilder.append("、");
                        stringBuilder.append("</strong>");

                        //标题非空
                        if (!TextUtils.isEmpty(factorResult.getTitle())) {
                            stringBuilder.append("<strong>");
                            //颜色非空
                            if (!TextUtils.isEmpty(factorResult.getColor())) {
                                stringBuilder.append("<font color='");
                                stringBuilder.append(factorResult.getColor());
                                stringBuilder.append("'>");
                            } else {
                                stringBuilder.append("<font>");
                            }
                            stringBuilder.append(factorResult.getTitle());
                            stringBuilder.append("</font>");
                            stringBuilder.append("</strong>");

                            //内容非空（结果详细）
                            if (!TextUtils.isEmpty(factorResult.getContent())) {
                                stringBuilder.append("，");
                            }
                        }

                        //内容非空（结果详细）
                        if (!TextUtils.isEmpty(factorResult.getContent())) {
                            stringBuilder.append(factorResult.getContent());
                        }

                        stringBuilder.append("<br/><br/>");
                    }

                    //最终拼接结果
                    reportResult.setFactorResultText(stringBuilder.toString());

                }
            }
        }
    }


    /**
     * List<List<ReportItemEntity>>转成用于适配recycler的分组数据模型List<MultiItemEntity>
     *
     * @param reportItemCollection List<List<ReportItemEntity>>
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> reportItemCollectionToRecyclerMulti(List<List<ReportItemEntity>> reportItemCollection) throws CloneNotSupportedException {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(reportItemCollection)) {
            resList = new ArrayList<>();
            //遍历话题列表
            for (List<ReportItemEntity> reportItemList : reportItemCollection) {
                if (ArrayListUtil.isEmpty(reportItemList)) {
                    break;
                }

                //header
                ReportItemEntity header = reportItemList.get(0);
                ReportItemEntity footer = (ReportItemEntity) reportItemList.get(0).clone();
                resList.add(header);

                //图表
                for (ReportItemEntity reportItem : reportItemList) {
                    //图表类型
                    int chartType = reportItem.getChartType();
                    //报告项转图表项
                    ChartItem chartItem = ChartUtil.reportItemToChartItem(getContext(), chartType, reportItem);
                    if (chartItem != null) {
                        //添加为header的子项
                        header.addSubItem(chartItem);
                    }
                }

                //footer
                footer.setItemType(Dictionary.CHART_FOOTER);
                resList.add(footer);
            }
        }

        return resList;
    }

    /**
     * ExamReportRootEntity转成用于适配recycler的分组数据模型List<MultiItemEntity>
     *
     * @param examReport ExamReportRootEntity
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> examReportRootToRecyclerMulti(ExamReportRootEntity examReport) throws CloneNotSupportedException {
        List<MultiItemEntity> resList = null;

        if (examReport != null) {
            resList = new ArrayList<>();

            //header
            resList.add(examReport.setItemType(Dictionary.CHART_HEADER));

            //图表（堆叠图为MBTI，其他为正常图表）
            if (examReport.getChart_type() == Dictionary.CHART_LEFT_RIGHT_RATIO) {
                List<ExamMbtiData> mbtiData = examReport.getMbtiData();
                if (ArrayListUtil.isNotEmpty(mbtiData)) {
                    //添加子标题
                    resList.add(new ReportSubTitleEntity("数据").setItemType(Dictionary.CHART_SUB_TITLE));
                    resList.add(new ReportMbtiData(mbtiData).setItemType(Dictionary.CHART_LEFT_RIGHT_RATIO));
                }

            } else {
                List<ReportItemEntity> chartDatas = examReport.getChartDatas();
                if (ArrayListUtil.isNotEmpty(chartDatas)) {
                    //添加子标题
                    resList.add(new ReportSubTitleEntity("数据").setItemType(Dictionary.CHART_SUB_TITLE));
                    //遍历图表
                    for (ReportItemEntity reportItem : chartDatas) {
                        //图表类型
                        int chartType = reportItem.getChartType();
                        //报告项转图表项
                        ChartItem chartItem = ChartUtil.reportItemToChartItem(getContext(), chartType, reportItem);
                        if (chartItem != null) {
                            //添加图表
                            resList.add(chartItem);
                            //添加图表说明
                            if (!TextUtils.isEmpty(reportItem.getChartDescription())) {
                                resList.add(new ChartItemDesc(reportItem.getChartDescription()).setItemType(Dictionary.CHART_DESC));
                            }
                        }
                    }
                }
            }

            //子项结果（详细测评）
            List<ReportSubItemEntity> subItems = examReport.getSubItems();
            if (ArrayListUtil.isNotEmpty(subItems)) {
                //添加子标题
                resList.add(new ReportSubTitleEntity("详细说明").setItemType(Dictionary.CHART_SUB_TITLE));
                //设置布局类型
                for (ReportSubItemEntity subItem : subItems) {
                    subItem.setItemType(Dictionary.CHART_SUB_ITEM);
                    resList.add(subItem);
                }
            }

            //推荐职业类型
            List<ActType> recommendActType = examReport.getRecommendActType();
            if (ArrayListUtil.isNotEmpty(recommendActType)) {
                //添加子标题
                resList.add(new ReportSubTitleEntity("推荐职业类型").setItemType(Dictionary.CHART_SUB_TITLE));
                resList.add(new ReportRecommendActType(recommendActType).setItemType(Dictionary.CHART_RECOMMEND_ACT_TYPE));
            }

        }

        return resList;
    }


    /**
     * 加载报告推荐内容
     * @param dto 报告dto
     */
    private void loadReportRecommendArticle(ExamReportDto dto) {
        //通信等待提示
//        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getReportRecommendContent(dto,
            new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    //空布局：网络连接有误，或者请求失败
//                        emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                    footerReportView.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(Object obj) {
                    //空布局：隐藏
//                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    try {
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);

                        int totalCount = articleRootEntity.getTotal();
                        List<SimpleArticleEntity> dataList = articleRootEntity.getItems();

                        //空数据处理
                        if (ArrayListUtil.isEmpty(dataList)) {
//                                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                            return;
                        }

                        //清空已存在的推荐文章
                        int childCount = llArticle.getChildCount();
                        if (childCount > 0) {
                            llArticle.removeAllViews();
                        }

                        footerReportView.setVisibility(View.VISIBLE);
                        //添加推荐文章
                        for (final SimpleArticleEntity article : dataList) {
                            View commentItemView = View.inflate(getContext(), R.layout.recycleritem_report_recommed_article, null);
                            llArticle.addView(commentItemView);

                            ImageView ivMain = commentItemView.findViewById(R.id.iv_main);//主图
                            //非空
                            String url = article.getArticleImg();
                            if (!TextUtils.isEmpty(url)) {
                                if (!url.equals(ivMain.getTag(R.id.iv_main))) {
                                    Glide.with(ExamReportFragment.this)
                                            .load(url)
                                            .apply(defaultOptions)
                                            .into(ivMain);
                                    ivMain.setTag(R.id.iv_main, url);
                                }
                            } else {
                                Glide.with(ExamReportFragment.this)
                                        .load(R.drawable.default_image_round_article_list)
                                        .apply(defaultOptions)
                                        .into(ivMain);
                                ivMain.setTag(R.id.iv_main, url);
                            }

                            ImageView ivPlay = commentItemView.findViewById(R.id.iv_play);//播放键
                            if (article.getContentType() == Dictionary.ARTICLE_TYPE_VIDEO) {
                                ivPlay.setVisibility(View.VISIBLE);
                            } else {
                                ivPlay.setVisibility(View.GONE);
                            }

                            TextView tvArticleTitle = commentItemView.findViewById(R.id.tv_article_title);//标题
                            tvArticleTitle.setText(article.getArticleTitle());

                            //收藏状态初始化
                            ImageView ivFavorite = commentItemView.findViewById(R.id.iv_favorite);
                            if (article.isFavorite()) {
                                //收藏状态
                                ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_do));
                            } else {
                                //未收藏状态
                                ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_not));
                            }

                            //阅读数量
                            View rlRead = commentItemView.findViewById(R.id.rl_read);
                            if (article.getPageView() > 0) {
                                rlRead.setVisibility(View.VISIBLE);
                                TextView tvReadCount = commentItemView.findViewById(R.id.tv_read_count);
                                tvReadCount.setText(String.valueOf(article.getPageView()));

                            } else {
                                rlRead.setVisibility(View.GONE);
                            }

                            //项目点击（查看文章详情）
                            commentItemView.setOnClickListener(new OnMultiClickListener() {
                                @Override
                                public void onMultiClick(View view) {
                                    String articleId = article.getId();
                                    //非空
                                    if (!TextUtils.isEmpty(articleId)) {
                                        //跳转到文章详情页面
                                        String ivMainUrl = article.getArticleImg();
                                        String articleTitle = article.getArticleTitle();

                                        ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);
                                    } else {
                                        if (getActivity() != null) {
                                            ToastUtil.showShort(getActivity().getApplication(), "暂无详情");
                                        }
                                    }
                                }
                            });

                            //收藏点击监听
                            ivFavorite.setOnClickListener(new OnMultiClickListener() {
                                @Override
                                public void onMultiClick(View view) {
                                    String articleId = article.getId();
                                    //非空
                                    if (!TextUtils.isEmpty(articleId)) {
                                        doFavorite(articleId, (ImageView) view);
                                    } else {
                                        if (getActivity() != null) {
                                            ToastUtil.showShort(getActivity().getApplication(), getResources().getString(R.string.operate_fail));
                                        }
                                    }
                                }
                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        //空布局：加载失败
//                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                        footerReportView.setVisibility(View.GONE);
                    }
                }
            }, httpTag, getActivity());
    }


    /**
     * 把比较名称置于报告图表对象中
     * @param reportItems
     * @param data
     */
    private void settingCompareName(List<ReportItemEntity> reportItems, ReportRootEntity data) {
        if (reportItems == null || data == null) {
            return;
        }

        for (ReportItemEntity reportItem : reportItems) {
            reportItem.setCompareName(data.getCompareName());
        }
    }

    /**
     * 把reportItems分组，每个报告可能不只一个图表
     * （topic项在前，dimension项在后）
     * @param reportItems
     * @return
     */
    private List<List<ReportItemEntity>> groupReportItem(List<ReportItemEntity> reportItems) {
        List<List<ReportItemEntity>> topicReportList = new ArrayList<>();
        List<List<ReportItemEntity>> dimensionReportList = new ArrayList<>();

        for(ReportItemEntity reportItem : reportItems){
            //话题
            if(reportItem.getTopic()){
                //话题报告
                addDimensionReport(topicReportList, reportItem);
            }else{
                //量表报告
                addDimensionReport(dimensionReportList, reportItem);
            }
        }

        //话题报告在前
        topicReportList.addAll(dimensionReportList);

        return topicReportList;
    }

    /**
     * 添加报告到报告集合中
     * @param reportList 报告集合
     * @param reportItem 报告对象
     */
    private void addDimensionReport(List<List<ReportItemEntity>> reportList, ReportItemEntity reportItem){
        //是否已存在
        boolean exist = false;

        for(int i=0;i<reportList.size();i++){
            List<ReportItemEntity> list = reportList.get(i);
            if(list!=null && list.size()>0){
                if(list.get(0).getChartItemId().equals(reportItem.getChartItemId())){
                    list.add(reportItem);
                    //标记已存在
                    exist = true;
                    break;
                }
            }
        }

        //未存在，则新建
        if (!exist) {
            List<ReportItemEntity> newList = new ArrayList<>();
            newList.add(reportItem);
            reportList.add(newList);
        }
    }

//    private List<List<ReportItemEntity>> groupReportItem(List<ReportItemEntity> reportItems) {
//        ArrayMap<String, List<ReportItemEntity>> reportArrayMap = new ArrayMap<>();
//
//        //目前暂时不区分是不是topic报告
//        for (ReportItemEntity reportItem : reportItems) {
//            //是否已经存在
//            if (reportArrayMap.containsKey(reportItem.getChartItemId())) {
//                reportArrayMap.get(reportItem.getChartItemId()).add(reportItem);
//
//            } else {
//                //不存在则新建
//                List<ReportItemEntity> newReport = new ArrayList<>();
//                newReport.add(reportItem);
//                reportArrayMap.put(reportItem.getChartItemId(), newReport);
//            }
//        }
//
//        //转成ArrayList
//        List<List<ReportItemEntity>> reportList = new ArrayList<>();
//        Iterator<Map.Entry<String, List<ReportItemEntity>>> iterator = reportArrayMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, List<ReportItemEntity>> next = iterator.next();
//            reportList.add(next.getValue());
//        }
//
//        return reportList;
//    }


    /**
     * 把ReportResult设置到对应的ReportItem中
     *
     * @param reportItemEntities   图表项集合
     * @param reportResultEntities 结果项集合
     * @return
     */
    private List<ReportItemEntity> settingResultToReportItem(List<ReportItemEntity> reportItemEntities, List<ReportResultEntity> reportResultEntities) {
        //图表项
        for (int i = 0; i < reportItemEntities.size(); i++) {
            ReportItemEntity reportItemEntity = reportItemEntities.get(i);
            //结果项
            for (int j = 0; j < reportResultEntities.size(); j++) {
                ReportResultEntity reportResultEntity = reportResultEntities.get(j);
                try {
                    //判等：chart_item_id和relationId
                    if (reportItemEntity.getChartItemId().equals(reportResultEntity.getRelationId())) {
                        //图表项中的reportResult为空才赋值
                        if (reportItemEntity.getReportResult() == null) {
                            reportItemEntity.setReportResult(reportResultEntity);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return reportItemEntities;
    }

    /**
     * 是否存在话题报告
     * @param reportItems
     * @return
     */
    private boolean hasTopicReport(List<ReportItemEntity> reportItems) {
        boolean res = false;

        if (reportItems != null) {
            for (ReportItemEntity reportItem : reportItems) {
                if (reportItem.getTopic()) {
                    res = true;
                    break;
                }
            }
        }

        return res;
    }


    /**
     * 收藏操作
     *
     * @param articleId
     */
    private void doFavorite(String articleId, final ImageView ivFavorite) {
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
                    Boolean favorite = (Boolean) dataMap.get("is_favorite");
                    Double pageFavorite = (Double) dataMap.get("page_favorite");
                    int count = 0;
                    if (pageFavorite != null) {
                        count = pageFavorite.intValue();
                    }

                    if (favorite) {
                        //收藏状态
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_do));
                    } else {
                        //未收藏状态
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_not));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //操作失败
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag, getActivity());
    }


    String testDataStr = "{\n" +
            "\t\"compare_name\": \"全国\",\n" +
            "\t\"topic_name\": \"我的认知过程\",\n" +
            "\t\"chart_datas\": [{\n" +
            "\t\t\"is_topic\": true,\n" +
            "\t\t\"chart_type\": 2,\n" +
            "\t\t\"chart_description\": \"原始分曲线图\",\n" +
            "\t\t\"score_type\": 1,\n" +
            "\t\t\"min_score\": 0,\n" +
            "\t\t\"max_score\": 100,\n" +
            "\t\t\"chart_item_id\": \"084eb136-34db-11e8-bc53-00163e000f06\",\n" +
            "\t\t\"chart_item_name\": \"我的认知过程\",\n" +
            "\t\t\"chart_show_item_name\": false,\n" +
            "\t\t\"report_result\": null,\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"compare_id\": \"0\",\n" +
            "\t\t\t\"compare_name\": \"全国\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"1953c6a3-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\t\t\"item_name\": \"正念注意觉知\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"1953cbd0-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\t\t\"item_name\": \"执行功能\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}]\n" +
            "\t\t}, {\n" +
            "\t\t\t\"compare_id\": \"1\",\n" +
            "\t\t\t\"compare_name\": \"年级\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"1953c6a3-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\t\t\"item_name\": \"正念注意觉知\",\n" +
            "\t\t\t\t\"score\": 44.35\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"1953cbd0-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\t\t\"item_name\": \"执行功能\",\n" +
            "\t\t\t\t\"score\": 36.92\n" +
            "\t\t\t}]\n" +
            "\t\t}, {\n" +
            "\t\t\t\"compare_id\": \"2\",\n" +
            "\t\t\t\"compare_name\": \"我\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"1953c6a3-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\t\t\"item_name\": \"正念注意觉知\",\n" +
            "\t\t\t\t\"score\": 44.35\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"1953cbd0-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\t\t\"item_name\": \"执行功能\",\n" +
            "\t\t\t\t\"score\": 36.92\n" +
            "\t\t\t}]\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"is_topic\": false,\n" +
            "\t\t\"chart_type\": 1,\n" +
            "\t\t\"chart_description\": \"原始分曲线图\",\n" +
            "\t\t\"score_type\": 1,\n" +
            "\t\t\"min_score\": 0,\n" +
            "\t\t\"max_score\": 100,\n" +
            "\t\t\"chart_item_id\": \"1953cbd0-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\"chart_item_name\": \"执行功能\",\n" +
            "\t\t\"chart_show_item_name\": false,\n" +
            "\t\t\"report_result\": null,\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"compare_id\": \"0\",\n" +
            "\t\t\t\"compare_name\": \"全国\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"55b95f9f-4c03-4fbd-c575-ee152452baa0\",\n" +
            "\t\t\t\t\"item_name\": \"抑制\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"49b128fc-27f0-c155-ad07-45f83632141f\",\n" +
            "\t\t\t\t\"item_name\": \"转换\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"0fc66061-142b-739e-3b16-61e230f9e19e\",\n" +
            "\t\t\t\t\"item_name\": \"情绪控制\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"67e4373e-d2c1-f699-d7c5-dbb9e66bc520\",\n" +
            "\t\t\t\t\"item_name\": \"任务完成\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"c88e8e0a-fe99-d128-99cb-40e1d0c80a7f\",\n" +
            "\t\t\t\t\"item_name\": \"工作记忆\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"4ea7451f-524e-658f-ec44-044a3301cdac\",\n" +
            "\t\t\t\t\"item_name\": \"计划\\/组织\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"cad6dd76-8f0b-9263-6532-c5e034701aec\",\n" +
            "\t\t\t\t\"item_name\": \"材料组织\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"1d4d7fd7-0d2b-0c1d-e106-ebb0cb44f805\",\n" +
            "\t\t\t\t\"item_name\": \"监控\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}]\n" +
            "\t\t}, {\n" +
            "\t\t\t\"compare_id\": \"1\",\n" +
            "\t\t\t\"compare_name\": \"年级\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"55b95f9f-4c03-4fbd-c575-ee152452baa0\",\n" +
            "\t\t\t\t\"item_name\": \"抑制\",\n" +
            "\t\t\t\t\"score\": 35.54\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"49b128fc-27f0-c155-ad07-45f83632141f\",\n" +
            "\t\t\t\t\"item_name\": \"转换\",\n" +
            "\t\t\t\t\"score\": 34.28\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"0fc66061-142b-739e-3b16-61e230f9e19e\",\n" +
            "\t\t\t\t\"item_name\": \"情绪控制\",\n" +
            "\t\t\t\t\"score\": 42.91\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"67e4373e-d2c1-f699-d7c5-dbb9e66bc520\",\n" +
            "\t\t\t\t\"item_name\": \"任务完成\",\n" +
            "\t\t\t\t\"score\": 54.6\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"c88e8e0a-fe99-d128-99cb-40e1d0c80a7f\",\n" +
            "\t\t\t\t\"item_name\": \"工作记忆\",\n" +
            "\t\t\t\t\"score\": 33.6\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"4ea7451f-524e-658f-ec44-044a3301cdac\",\n" +
            "\t\t\t\t\"item_name\": \"计划\\/组织\",\n" +
            "\t\t\t\t\"score\": 15.42\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"cad6dd76-8f0b-9263-6532-c5e034701aec\",\n" +
            "\t\t\t\t\"item_name\": \"材料组织\",\n" +
            "\t\t\t\t\"score\": 26.93\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"1d4d7fd7-0d2b-0c1d-e106-ebb0cb44f805\",\n" +
            "\t\t\t\t\"item_name\": \"监控\",\n" +
            "\t\t\t\t\"score\": 46.25\n" +
            "\t\t\t}]\n" +
            "\t\t}, {\n" +
            "\t\t\t\"compare_id\": \"2\",\n" +
            "\t\t\t\"compare_name\": \"我\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"55b95f9f-4c03-4fbd-c575-ee152452baa0\",\n" +
            "\t\t\t\t\"item_name\": \"抑制\",\n" +
            "\t\t\t\t\"score\": 30.54\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"49b128fc-27f0-c155-ad07-45f83632141f\",\n" +
            "\t\t\t\t\"item_name\": \"转换\",\n" +
            "\t\t\t\t\"score\": 41.28\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"0fc66061-142b-739e-3b16-61e230f9e19e\",\n" +
            "\t\t\t\t\"item_name\": \"情绪控制\",\n" +
            "\t\t\t\t\"score\": 48.91\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"67e4373e-d2c1-f699-d7c5-dbb9e66bc520\",\n" +
            "\t\t\t\t\"item_name\": \"任务完成\",\n" +
            "\t\t\t\t\"score\": 34.6\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"c88e8e0a-fe99-d128-99cb-40e1d0c80a7f\",\n" +
            "\t\t\t\t\"item_name\": \"工作记忆\",\n" +
            "\t\t\t\t\"score\": 37.6\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"4ea7451f-524e-658f-ec44-044a3301cdac\",\n" +
            "\t\t\t\t\"item_name\": \"计划\\/组织\",\n" +
            "\t\t\t\t\"score\": 45.42\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"cad6dd76-8f0b-9263-6532-c5e034701aec\",\n" +
            "\t\t\t\t\"item_name\": \"材料组织\",\n" +
            "\t\t\t\t\"score\": 36.93\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"item_id\": \"1d4d7fd7-0d2b-0c1d-e106-ebb0cb44f805\",\n" +
            "\t\t\t\t\"item_name\": \"监控\",\n" +
            "\t\t\t\t\"score\": 36.25\n" +
            "\t\t\t}]\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"is_topic\": false,\n" +
            "\t\t\"chart_type\": 2,\n" +
            "\t\t\"chart_description\": \"原始分曲线图\",\n" +
            "\t\t\"score_type\": 1,\n" +
            "\t\t\"min_score\": 0,\n" +
            "\t\t\"max_score\": 100,\n" +
            "\t\t\"chart_item_id\": \"1953c6a3-34e4-11e8-bc53-00163e000f06\",\n" +
            "\t\t\"chart_item_name\": \"正念注意觉知\",\n" +
            "\t\t\"chart_show_item_name\": false,\n" +
            "\t\t\"report_result\": null,\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"compare_id\": \"0\",\n" +
            "\t\t\t\"compare_name\": \"全国\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"a2b2eaab-56dc-57d3-9fea-727949424d76\",\n" +
            "\t\t\t\t\"item_name\": \"正念注意觉知\",\n" +
            "\t\t\t\t\"score\": 50\n" +
            "\t\t\t}]\n" +
            "\t\t}, {\n" +
            "\t\t\t\"compare_id\": \"1\",\n" +
            "\t\t\t\"compare_name\": \"年级\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"a2b2eaab-56dc-57d3-9fea-727949424d76\",\n" +
            "\t\t\t\t\"item_name\": \"正念注意觉知\",\n" +
            "\t\t\t\t\"score\": 44.43\n" +
            "\t\t\t}]\n" +
            "\t\t}, {\n" +
            "\t\t\t\"compare_id\": \"2\",\n" +
            "\t\t\t\"compare_name\": \"我\",\n" +
            "\t\t\t\"chart_datas\": [{\n" +
            "\t\t\t\t\"item_id\": \"a2b2eaab-56dc-57d3-9fea-727949424d76\",\n" +
            "\t\t\t\t\"item_name\": \"正念注意觉知\",\n" +
            "\t\t\t\t\"score\": 44.43\n" +
            "\t\t\t}]\n" +
            "\t\t}]\n" +
            "\t}],\n" +
            "\t\"report_results\": [{\n" +
            "\t\t\"relation_id\": null,\n" +
            "\t\t\"header\": null,\n" +
            "\t\t\"color\": null,\n" +
            "\t\t\"title\": null,\n" +
            "\t\t\"content\": null,\n" +
            "\t\t\"description\": \"原始分曲线图\",\n" +
            "\t\t\"result\": null,\n" +
            "\t\t\"score\": 36.92,\n" +
            "\t\t\"factor_result\": []\n" +
            "\t}, {\n" +
            "\t\t\"relation_id\": null,\n" +
            "\t\t\"header\": null,\n" +
            "\t\t\"color\": null,\n" +
            "\t\t\"title\": null,\n" +
            "\t\t\"content\": null,\n" +
            "\t\t\"description\": \"原始分曲线图\",\n" +
            "\t\t\"result\": null,\n" +
            "\t\t\"score\": 44.35,\n" +
            "\t\t\"factor_result\": []\n" +
            "\t}]\n" +
            "}";

}
