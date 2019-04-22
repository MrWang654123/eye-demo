package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.ChartItemDesc;
import com.cheersmind.cheersgenie.features.entity.RecyclerViewDivider;
import com.cheersmind.cheersgenie.features.entity.ReportRecommend;
import com.cheersmind.cheersgenie.features.entity.ReportRecommendItem;
import com.cheersmind.cheersgenie.features.entity.ReportRecommendRootEntity;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.ChartUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamReportRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.EvaluateDto;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ExamReportRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.ReportEvaluate;
import com.cheersmind.cheersgenie.features_v2.entity.ReportEvaluateItem;
import com.cheersmind.cheersgenie.features_v2.entity.ReportMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ReportRecommendActType;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubTitleEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfo;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

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
//    View footerReportView;
    //文章列表布局
    LinearLayout llArticle;

    Unbinder unbinder;

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

            switch (multiItem.getItemType()) {
                //报告子项
                case Dictionary.CHART_SUB_ITEM: {
                    //话题报告
                    if (Dictionary.REPORT_TYPE_TOPIC.equals(reportDto.getRelationType())) {
                        ReportSubItemEntity item = (ReportSubItemEntity) multiItem;
                        ExamReportDto dto = new ExamReportDto();
                        dto.setChildExamId(reportDto.getChildExamId());
                        dto.setCompareId(reportDto.getCompareId());
                        dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
                        dto.setRelationId(item.getItem_id());
                        //跳转到报告页面量表
                        ExamReportActivity.startExamReportActivity(getContext(), dto);

                    } else if (Dictionary.REPORT_TYPE_DIMENSION.equals(reportDto.getRelationType())) {//量表报告
                        //伸缩
                        ReportSubItemEntity item = (ReportSubItemEntity) multiItem;
                        item.setExpand(!item.isExpand());
                        int layoutPos = recyclerAdapter.getHeaderLayoutCount() + position;
                        recyclerAdapter.notifyItemChanged(layoutPos);
                    }

                    break;
                }
                //推荐内容子项
                case Dictionary.CHART_RECOMMEND_CONTENT_ITEM: {
                    ReportRecommendItem entity = (ReportRecommendItem) multiItem;
                    if ("audio".equals(entity.getElement_type())) {
                        System.out.println("点击音频");

                    } else if ("video".equals(entity.getElement_type())) {
                        ArticleDetailActivity.startArticleDetailActivity(getContext(), entity.getElement_id(), null, entity.getElement_name());

                    } else if ("article".equals(entity.getElement_type())) {
                        ArticleDetailActivity.startArticleDetailActivity(getContext(), entity.getElement_id(), null, entity.getElement_name());

                    } else if ("task".equals(entity.getElement_type())) {
                        System.out.println("点击任务");

                    } else if ("dimension".equals(entity.getElement_type())) {
                        if (!TextUtils.isEmpty(entity.getTopic_id()) && !TextUtils.isEmpty(entity.getElement_id())) {
                            TopicInfo topicInfo = new TopicInfo();
                            topicInfo.setTopicId(entity.getTopic_id());
                            topicInfo.setDimensionId(entity.getElement_id());
                            //继续答题
                            getReferenceExam(topicInfo);
                        } else {
                            if (!RepetitionClickUtil.isFastClickLong()) {
                                showToast(getString(R.string.operate_fail));
                            }
                        }
                    }
                    break;
                }
            }

        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity multiItem = recyclerAdapter.getData().get(position);

            switch (view.getId()) {
//                //子项伸缩
//                case R.id.iv_expand: {
//                    if (multiItem.getItemType() == Dictionary.CHART_SUB_ITEM) {
//                        ReportSubItemEntity item = (ReportSubItemEntity) multiItem;
//                        item.setExpand(!item.isExpand());
//                        int layoutPos = recyclerAdapter.getHeaderLayoutCount() + position;
//                        recyclerAdapter.notifyItemChanged(layoutPos);
//                    }
//                    break;
//                }
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

    //职业分类点击监听
    ExamReportRecyclerAdapter.OnActCategoryClickListener actCategoryClickListener = new ExamReportRecyclerAdapter.OnActCategoryClickListener() {
        @Override
        public void onClick(OccupationCategory category) {
//            SimpleOccupationActivity.startOccupationActivity(getContext(), category);
            OccupationActivity.startOccupationActivity(getContext(), category);
        }
    };

    //评价选项点击监听
    ExamReportRecyclerAdapter.OnEvaluateItemClickListener evaluateItemClickListener = new ExamReportRecyclerAdapter.OnEvaluateItemClickListener() {
        @Override
        public void onClick(BaseQuickAdapter adapter, int layoutPosition, ReportEvaluate evaluate, ReportEvaluateItem evaluateItem) {
            if (evaluate.isEvaluate()) {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), "已评价");
                }
            } else {
                EvaluateDto evaluateDto = new EvaluateDto();
                evaluateDto.setRefId(reportDto.getRelationId());//评价实体ID
                evaluateDto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());//孩子ID
                evaluateDto.setItemId(evaluateItem.getId());//评价项ID
                doPostReportEvaluate(evaluateDto, layoutPosition, evaluate, evaluateItem);
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
        //act职业分类点击监听
        recyclerAdapter.setActCategoryClickListener(actCategoryClickListener);
        //评价选项点击监听
        recyclerAdapter.setOnEvaluateItemClickListener(evaluateItemClickListener);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);

        //滑动监听
//        try {
//            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //报告列表footer（推荐文章）
//        footerReportView = LayoutInflater.from(getContext()).inflate(R.layout.recycler_footer_article_recommend, null);
//        llArticle = footerReportView.findViewById(R.id.ll_article);
//        recyclerAdapter.addFooterView(footerReportView);
//        footerReportView.setVisibility(View.GONE);//初始隐藏footer

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        //初始化默认Glide处理参数
        initRequestOptions(getContext());
    }

    @Override
    protected void lazyLoad() {
        //加载报告
        loadReport(reportDto);
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
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            ExamReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ExamReportRootEntity.class);

                            //无数据处理
                            if (data == null || TextUtils.isEmpty(data.getTitle())) {
                                //空布局：无数据
                                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                                return;
                            }

                            //数据转换
                            List<MultiItemEntity> multiItemEntities = examReportRootToRecyclerMulti(data);
                            //标记是否是话题
                            recyclerAdapter.setTopic(Dictionary.REPORT_TYPE_TOPIC.equals(dto.getRelationType()));
                            //目前每次都是重置列表数据
                            recyclerAdapter.setNewData(multiItemEntities);

                            //目前量表报告才有用户评价
                            if (Dictionary.REPORT_TYPE_DIMENSION.equals(dto.getRelationType())) {
                                //获取报告的评价选项
                                doGetReportEvaluate();
                            }

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
     * ExamReportRootEntity转成用于适配recycler的分组数据模型List<MultiItemEntity>
     *
     * @param examReport ExamReportRootEntity
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> examReportRootToRecyclerMulti(ExamReportRootEntity examReport) {
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
                    //默认展开
                    subItem.setExpand(true);
                    subItem.setItemType(Dictionary.CHART_SUB_ITEM);
                    resList.add(subItem);
                }
            }

            //推荐职业类型
            List<OccupationCategory> categories = examReport.getCategories();
            if (ArrayListUtil.isNotEmpty(categories)) {
                //添加子标题
                resList.add(new ReportSubTitleEntity("推荐职业类型").setItemType(Dictionary.CHART_SUB_TITLE));
                resList.add(new ReportRecommendActType(categories).setItemType(Dictionary.CHART_RECOMMEND_ACT_TYPE));
            }

        }

        return resList;
    }


    /**
     * 加载报告推荐内容
     */
    private void loadReportRecommendContent(ExamReportDto reportDto) {
        if (TextUtils.isEmpty(reportDto.getDimensionId())) {
            return;
        }

        DataRequestService.getInstance().getReportRecommendContent(reportDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ReportRecommendRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ReportRecommendRootEntity.class);

                    List<ReportRecommend> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        return;
                    }

                    //数据转换
                    List<MultiItemEntity> multiItemEntities = reportRecommendToRecyclerMulti(dataList);
                    if (ArrayListUtil.isNotEmpty(multiItemEntities)) {
                        recyclerAdapter.addData(multiItemEntities);
                    }

                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }, httpTag, getActivity());
    }


    /**
     * List<ReportRecommend>转成用于适配recycler的分组数据模型List<MultiItemEntity>
     *
     * @param dataList List<ReportRecommend>
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> reportRecommendToRecyclerMulti(List<ReportRecommend> dataList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(dataList)) {
            resList = new ArrayList<>();
            for (ReportRecommend reportRecommend : dataList) {
                List<ReportRecommendItem> items = reportRecommend.getItems();
                if (ArrayListUtil.isNotEmpty(items)) {
                    //子项非空则添加
                    reportRecommend.setItemType(Dictionary.CHART_RECOMMEND_CONTENT_TITLE);
                    resList.add(reportRecommend);

                    for (ReportRecommendItem item : items) {
                        item.setItemType(Dictionary.CHART_RECOMMEND_CONTENT_ITEM);
                        resList.add(item);
                    }

                    //分割线
                    RecyclerViewDivider divider = new RecyclerViewDivider(Dictionary.CHART_RECOMMEND_CONTENT_DIVIDER);
                    resList.add(divider);
                }
            }
        }

        if (ArrayListUtil.isNotEmpty(resList)) {
            resList.add(0, new ReportSubTitleEntity("推荐学习").setItemType(Dictionary.CHART_SUB_TITLE));
        }

        return resList;
    }


    /**
     * 请求测评
     * @param topicInfo 简单话题
     */
    private void getReferenceExam(final TopicInfo topicInfo) {
        //通信等待加载提示
        LoadingView.getInstance().show(getContext(), httpTag);

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().getChildDimension(childId, topicInfo.getTopicId(), topicInfo.getDimensionId(), new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //关闭通信等待加载提示
                LoadingView.getInstance().dismiss();

                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), R.string.operate_fail);
                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信等待加载提示
                    LoadingView.getInstance().dismiss();

                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    DimensionInfoEntity dimension = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoEntity.class);

                    if (dimension == null || TextUtils.isEmpty(dimension.getDimensionId())) {
                        throw new QSCustomException(getString(R.string.operate_fail));
                    }

                    //已完成，查看报告
                    if (dimension.getChildDimension() != null
                            && dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
                        ExamReportDto dto = new ExamReportDto();
                        dto.setChildExamId(dimension.getChildDimension().getChildExamId());//孩子测评ID
                        dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                        dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);//量表报告类型
                        dto.setRelationId(dimension.getTopicDimensionId());//话题量表ID
                        dto.setDimensionId(dimension.getDimensionId());//量表ID（目前用于报告推荐内容）

                        ExamReportActivity.startExamReportActivity(getContext(), dto);

                    } else {//未完成，继续答题
                        //进入量表详情页面
                        TopicInfoEntity topicInfoEntity = new TopicInfoEntity();
                        //话题ID
                        topicInfoEntity.setTopicId(topicInfo.getTopicId());
                        //测评ID
                        topicInfoEntity.setExamId(dimension.getExamId());
                        DimensionDetailActivity.startDimensionDetailActivity(
                                getContext(), dimension,
                                topicInfoEntity,
                                Dictionary.EXAM_STATUS_DOING,
                                Dictionary.FROM_ACTIVITY_TO_SYS_RMD_COURSE);
                    }

                } catch (QSCustomException e) {
                    onFailure(e);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getContext());
    }


    /**
     * 获取报告的评价选项
     */
    private void doGetReportEvaluate() {

        EvaluateDto evaluateDto = new EvaluateDto();
        evaluateDto.setRefId(reportDto.getRelationId());
        evaluateDto.setType(Dictionary.REPORT_EVALUATE_TYPE_DIMENSION);

        DataRequestService.getInstance().getReportEvaluate(evaluateDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                //加载报告推荐内容
                loadReportRecommendContent(reportDto);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ReportEvaluate reportEvaluate = InjectionWrapperUtil.injectMap(dataMap, ReportEvaluate.class);

                    if (reportEvaluate == null || ArrayListUtil.isEmpty(reportEvaluate.getItems())) {
                        throw new QSCustomException(getString(R.string.operate_fail));
                    }

                    //评价过了就不显示
                    if (!reportEvaluate.isEvaluate()) {
                        reportEvaluate.setItemType(Dictionary.CHART_REPORT_EVALUATE);
                        recyclerAdapter.addData(reportEvaluate);
                    }

                    //加载报告推荐内容
                    loadReportRecommendContent(reportDto);

                } catch (QSCustomException e) {
                    onFailure(e);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getContext());
    }


    /**
     * 提交报告评价
     */
    private void doPostReportEvaluate(EvaluateDto evaluateDto, final int positionLayout,
                                      final ReportEvaluate evaluate, final ReportEvaluateItem evaluateItem) {

        DataRequestService.getInstance().postReportEvaluate(evaluateDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //设置选中标记
                    evaluate.setEvaluate(true);
                    evaluateItem.setEvaluate(true);
                    //刷新视图
                    recyclerAdapter.notifyItemChanged(positionLayout);

                } catch (Exception e) {
                    onFailure(new QSCustomException(getString(R.string.operate_fail)));
                }
            }
        }, httpTag, getContext());
    }

}
