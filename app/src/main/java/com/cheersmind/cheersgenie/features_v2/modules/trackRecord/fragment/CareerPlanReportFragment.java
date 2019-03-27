package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionBaseRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.TopicDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.CareerPlanRecordRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.CareerPlanRecordRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRecord;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.RecordItemHeader;
import com.cheersmind.cheersgenie.features_v2.entity.SelectCourseRecord;
import com.cheersmind.cheersgenie.features_v2.entity.SelectCourseRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamTaskDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

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
 * 生涯规划报告
 */
public class CareerPlanReportFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评ID
    private String childExamId;

    //报告列表
    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器
    private CareerPlanRecordRecyclerAdapter recyclerAdapter;

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

//            ExamTaskEntity entity = recyclerAdapter.getData().get(position);
//            //判断任务项是否被锁，友好提示
//            if (entity.getIs_lock() == Dictionary.IS_LOCKED_YSE) {
//                lockedTaskTip(entity);
//
//            } else {
//                ExamTaskDetailActivity.startExamTaskDetailActivity(getContext(), entity);
//            }
        }
    };

    //recycler子项的孩子视图点击监听
    protected BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity item = ((CareerPlanRecordRecyclerAdapter) adapter).getItem(position);
            if (item != null) {
                //查看报告
                if (view.getId() == R.id.btn_report) {

//                    ExamReportDto dto = new ExamReportDto();
//                    dto.setChildExamId(childExamId);//孩子测评ID
//                    dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
//                    dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);//量表报告类型
//
//                    //高考选科子项
//                    if (item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_SELECT_COURSE_ITEM) {
//                        SelectCourseRecordItem entity = (SelectCourseRecordItem) item;
//                        dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
//
//                    } else if (item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_OCCUPATION_EXPLORE_ITEM) {//职业探索子项
//                        OccupationRecordItem entity = (OccupationRecordItem) item;
//                    }
//
//                    ExamReportActivity.startExamReportActivity(getContext(), dto);
                }
            } else {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), R.string.operate_fail);
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
        return R.layout.fragment_career_plan_report;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_career_plan_record));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载报告
                loadReport(childExamId);
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //适配器
        recyclerAdapter = new CareerPlanRecordRecyclerAdapter(getContext(), null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
//        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        //网格布局管理器
        final int count = getResources().getInteger(R.integer.career_plan_record_simple_dimension_count);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), count);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return recyclerAdapter.getItemViewType(position) == CareerPlanRecordRecyclerAdapter.LAYOUT_SIMPLE_DIMENSION ? 1 : count;
            }
        });
        //必须在setAdapter之后
        recycleView.setLayoutManager(gridLayoutManager);

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

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void lazyLoad() {
        //加载报告
        loadReport(childExamId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        //取消当前对话框的所有通信
        BaseService.cancelTag(httpTag);
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
//    @Subscribe
    public void onStopFlingNotice(StopFlingEvent event) {
        if (recycleView != null) {
            recycleView.stopScroll();
        }
    }

    /**
     * 加载报告
     * @param childExamId 孩子测评ID
     */
    private void loadReport(String childExamId) {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        try {
            DataRequestService.getInstance().getCareerPlanRecord(childExamId, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    //空布局：网络连接有误，或者请求失败
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(Object obj) {

                    try {
                        //空布局：隐藏
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                        //测试数据
//                        Map dataMap = JsonUtil.fromJson(testDataStr, Map.class);
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        CareerPlanRecordRootEntity data = InjectionWrapperUtil.injectMap(dataMap, CareerPlanRecordRootEntity.class);

                        if (data == null || (data.getSubject_archive() == null && data.getOccupation_archive() == null)) {
                            //空布局：无数据
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                            return;
                        }

                        List<MultiItemEntity> multiItemEntities = careerPlanRecordToRecyclerMulti(data);
                        //目前每次都是重置列表数据
                        recyclerAdapter.setNewData(multiItemEntities);
                        //初始展开
                        recyclerAdapter.expandAll();

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
     * CareerPlanRecordRootEntity转成用于适配recycler的分组数据模型List<MultiItemEntity>
     *
     * @param record CareerPlanRecordRootEntity
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> careerPlanRecordToRecyclerMulti(CareerPlanRecordRootEntity record) throws CloneNotSupportedException {
        List<MultiItemEntity> resList = null;

        if (record != null && record.getSubject_archive() != null && record.getOccupation_archive() != null) {
            resList = new ArrayList<>();

            //高考选科档案
            SelectCourseRecord subject_archive = record.getSubject_archive();
            //系统推荐选科header
            SelectCourseRecord sysRmdHeader = new SelectCourseRecord();
            sysRmdHeader.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_SYS_RECOMMEND_HEADER);
            sysRmdHeader.setRecommend_subjects(subject_archive.getRecommend_subjects());
            sysRmdHeader.setRecommend_subject_codes(subject_archive.getRecommend_subject_codes());
            //标记是否完成
            if (ArrayListUtil.isNotEmpty(sysRmdHeader.getRecommend_subjects())) {
                sysRmdHeader.setFinish(true);
            } else {
                sysRmdHeader.setFinish(false);
            }

            //有系统推荐才添加
            if (sysRmdHeader.isFinish()) {

                //系统推荐选科Item
                SelectCourseRecord sysRmdItem = new SelectCourseRecord();
                sysRmdItem.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_SYS_RECOMMEND_ITEM);
                sysRmdItem.setRecommend_major_per(subject_archive.getRecommend_major_per());
                sysRmdItem.setRecommend_high_major_per(subject_archive.getRecommend_high_major_per());
                //添加到系统推荐选科header中
                sysRmdHeader.addSubItem(sysRmdItem);

                //高考选科header
                SelectCourseRecord selectCourseRecord = new SelectCourseRecord();
                selectCourseRecord.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_SELECT_COURSE_HEADER);
                selectCourseRecord.setCustom_subjects(subject_archive.getCustom_subjects());
                selectCourseRecord.setCustom_subject_codes(subject_archive.getCustom_subject_codes());
                selectCourseRecord.setCustom_major_per(subject_archive.getCustom_major_per());
                selectCourseRecord.setCustom_subject_codes(subject_archive.getCustom_subject_codes());
                resList.add(selectCourseRecord);

                resList.add(sysRmdHeader);
            }

            //子项header
            RecordItemHeader itemHeader = new RecordItemHeader("测评详细说明:");
            itemHeader.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_ITEM_HEADER);
            //添加到系统推荐选科header中
            if (sysRmdHeader.isFinish()) {
                sysRmdHeader.addSubItem(itemHeader);

            } else {//否则直接添加
                resList.add(itemHeader);
            }

            //高考选科子项
            List<SelectCourseRecordItem> items = subject_archive.getItems();
            for (SelectCourseRecordItem item : items) {
                item.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_SELECT_COURSE_ITEM);
                //添加到系统推荐选科header中
                if (sysRmdHeader.isFinish()) {
                    sysRmdHeader.addSubItem(item);

                } else {//否则直接添加
                    resList.add(item);
                }

                //有简单量表
                if (ArrayListUtil.isNotEmpty(item.getDimensions())) {
                    List<SimpleDimensionResult> dimensions = item.getDimensions();

                    for (SimpleDimensionResult dimensionResult : dimensions) {
                        dimensionResult.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_SIMPLE_DIMENSION);
                        //设置列表层级
                        if (sysRmdHeader.isFinish()) {
                            item.setLevel(1);
                        } else {
                            item.setLevel(0);
                        }

                        item.addSubItem(dimensionResult);
                    }
                }
            }

            //职业探索档案
            OccupationRecord occupation_archive = record.getOccupation_archive();

            //职业探索header
            OccupationRecord occupationHeader = new OccupationRecord();
            occupationHeader.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_OCCUPATION_EXPLORE_HEADER);
            occupationHeader.setCareer_areas(occupation_archive.getCareer_areas());
            //标记是否完成
            if (ArrayListUtil.isNotEmpty(occupation_archive.getCareer_areas())) {
                occupationHeader.setFinish(true);
            } else {
                occupationHeader.setFinish(false);
            }
            resList.add(occupationHeader);

            //子项header
            RecordItemHeader itemHeader1 = new RecordItemHeader("职业探索测试:").setCanExpand(true);
            itemHeader1.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_ITEM_HEADER);
            resList.add(itemHeader1);

            //职业探索子项
            List<OccupationRecordItem> items1 = occupation_archive.getItems();
            for (OccupationRecordItem item : items1) {
                item.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_OCCUPATION_EXPLORE_ITEM);
                //添加到子项Header中
                itemHeader1.addSubItem(item);
            }
        }

        return resList;
    }

}

