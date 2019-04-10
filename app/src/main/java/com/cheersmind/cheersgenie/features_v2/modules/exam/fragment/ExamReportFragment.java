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
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.ChartUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamReportRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ExamReportRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.ReportMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ReportRecommendActType;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubTitleEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

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

    //职业分类点击监听
    ExamReportRecyclerAdapter.OnActCategoryClickListener actCategoryClickListener = new ExamReportRecyclerAdapter.OnActCategoryClickListener() {
        @Override
        public void onClick(OccupationCategory category) {
//            SimpleOccupationActivity.startOccupationActivity(getContext(), category);
            OccupationActivity.startOccupationActivity(getContext(), category);
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
        //act职业分类点击监听
        recyclerAdapter.setActCategoryClickListener(actCategoryClickListener);
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
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            ExamReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ExamReportRootEntity.class);

                            //无数据处理
                            if (data == null) {
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

}
