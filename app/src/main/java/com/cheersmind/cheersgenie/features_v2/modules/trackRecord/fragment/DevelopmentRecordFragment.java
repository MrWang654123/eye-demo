package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.DevelopmentRecordRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecord;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecordRecycler;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecordRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfo;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 发展档案
 */
public class DevelopmentRecordFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器的数据列表
    DevelopmentRecordRecyclerAdapter recyclerAdapter;

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity item = ((DevelopmentRecordRecyclerAdapter) adapter).getData().get(position);
            //子项
            if (item.getItemType() == DevelopmentRecordRecyclerAdapter.LAYOUT_TYPE_COMMON_ITEM
                    || item.getItemType() == DevelopmentRecordRecyclerAdapter.LAYOUT_TYPE_COMMON_ITEM) {
                DevelopmentRecordItem entity = (DevelopmentRecordItem) item;
                //孩子测评ID、话题量表ID不为空
                if (entity.isFinish()
                        && !TextUtils.isEmpty(entity.getChild_exam_id())
                        && !TextUtils.isEmpty(entity.getTopic_dimension_id())) {
                    ExamReportDto dto = new ExamReportDto();
                    dto.setChildExamId(entity.getChild_exam_id());//孩子测评ID
                    dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                    dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);//量表报告类型
                    dto.setRelationId(entity.getTopic_dimension_id());//话题量表ID
                    //查看报告
                    ExamReportActivity.startExamReportActivity(getContext(), dto);

                } else {
                    TopicInfo topicInfo = new TopicInfo();
                    topicInfo.setTopicId(entity.getTopic_id());
                    topicInfo.setDimensionId(entity.getDimension_id());

                    //完成
                    if (entity.isFinish()) {
                        //查看报告
                        getReferenceExam(topicInfo, POST_OPT_TYPE_REPORT);

                    } else {
                        //跳转量表详情
                        getReferenceExam(topicInfo, POST_OPT_TYPE_GO_ON);
                    }
                }

            } else if (item.getItemType() == DevelopmentRecordRecyclerAdapter.LAYOUT_TYPE_SUMMARY) {//header项
                DevelopmentRecord entity = (DevelopmentRecord) item;
                //布局位置
                int layoutPosition = position + recyclerAdapter.getHeaderLayoutCount();
                if (entity.isExpanded()) {
                    recyclerAdapter.collapse(layoutPosition);
                } else {
                    recyclerAdapter.expand(layoutPosition);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_track_record_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new DevelopmentRecordRecyclerAdapter(getContext(),null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //添加一个空HeaderView，用于显示顶部分割线
        recyclerAdapter.addHeaderView(new View(getContext()));
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //添加自定义分割线
        if (getContext() != null) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider_f5f5f5_15dp));
            recycleView.addItemDecoration(divider);
        }

        //滑动监听
//        try {
//            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_track_record));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载数据
                loadData();
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void lazyLoad() {
        //加载数据
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    /**
     * 停止Fling的消息
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopFlingNotice(StopFlingEvent event) {
        if (recycleView != null) {
            recycleView.stopScroll();
        }

    }

    /**
     * 问题提交成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionSubmitNotice(QuestionSubmitSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            DimensionInfoEntity dimension = event.getDimension();
            onQuestionSubmit(dimension);
        }

    }

    /**
     * 问题提交成功的通知事件的处理
     * @param dimension 量表对象
     */
    protected void onQuestionSubmit(DimensionInfoEntity dimension) {
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        //通信等待提示
//        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        try {
            String childId = UCManager.getInstance().getDefaultChild().getChildId();
            DataRequestService.getInstance().getDevelopmentRecord(childId, new BaseService.ServiceCallback() {
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
                        DevelopmentRecordRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, DevelopmentRecordRootEntity.class);

                        List<DevelopmentRecord> dataList = rootEntity.getItems();

                        //空数据处理
                        if (ArrayListUtil.isEmpty(dataList)) {
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                            return;
                        }

                        List<MultiItemEntity> multiItemEntities = developmentRecordToMultiItem(dataList);

                        recyclerAdapter.setNewData(multiItemEntities);
                        //初始展开
//                        recyclerAdapter.expandAll();

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
     * DevelopmentRecord转成List<MultiItemEntity>
     * @param dataList DevelopmentRecord
     * @return List<MultiItemEntity>
     */
    private List<MultiItemEntity> developmentRecordToMultiItem(List<DevelopmentRecord> dataList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(dataList)) {
            resList = new ArrayList<>();
            for (DevelopmentRecord record : dataList) {
                DevelopmentRecord header = new DevelopmentRecord();
                header.setCapability_name(record.getCapability_name());
                header.setFinish_radio(record.getFinish_radio());
                header.setItemType(DevelopmentRecordRecyclerAdapter.LAYOUT_TYPE_SUMMARY);

//                List<DevelopmentRecordItem> items = record.getItems();
//                if (ArrayListUtil.isNotEmpty(items)) {
//                    for (int i=0; i<items.size(); i++) {
//                        DevelopmentRecordItem item = items.get(i);
//                        item.setIndex(i);//索引位置
//                        item.setItemType(DevelopmentRecordRecyclerAdapter.LAYOUT_TYPE_COMMON_ITEM);//布局类型
//                        header.addSubItem(item);
//                    }
//                }

                List<DevelopmentRecordItem> items = record.getItems();
                if (ArrayListUtil.isNotEmpty(items)) {
                    for (int i=0; i<items.size(); i++) {
                        DevelopmentRecordItem item = items.get(i);
                        item.setIndex(i);//索引位置
                    }

                    resList.add(header);
                    resList.add(new DevelopmentRecordRecycler(items).setItemType(DevelopmentRecordRecyclerAdapter.LAYOUT_TYPE_H_RECYCLER));
                }
            }
        }

        return resList;
    }

    //后续操作类型：查看报告
    private final static int POST_OPT_TYPE_REPORT = 1;
    //后续操作类型：继续作答
    private final static int POST_OPT_TYPE_GO_ON = 2;

    /**
     * 请求测评
     * @param topicInfo 简单话题
     * @param postOptType 后续的操作类型
     */
    private void getReferenceExam(final TopicInfo topicInfo, final int postOptType) {
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
                        throw new QSCustomException("量表数据为空");
                    }

                    //查看报告
                    if (postOptType == POST_OPT_TYPE_REPORT) {
                        ExamReportDto dto = new ExamReportDto();
                        dto.setChildExamId(dimension.getChildDimension().getChildExamId());//孩子测评ID
                        dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                        dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);//量表报告类型
                        dto.setRelationId(dimension.getTopicDimensionId());//话题量表ID
                        dto.setDimensionId(dimension.getDimensionId());//量表ID（目前用于报告推荐内容）

                        ExamReportActivity.startExamReportActivity(getContext(), dto);

                    } else if (postOptType == POST_OPT_TYPE_GO_ON) {//继续作答
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
                                Dictionary.FROM_ACTIVITY_TO_TRACK_RECORD);
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

}

