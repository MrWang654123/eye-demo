package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.CareerPlanRecordRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.dto.ModuleDto;
import com.cheersmind.cheersgenie.features_v2.entity.CareerPlanRecordRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamModuleEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamModuleRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRecord;
import com.cheersmind.cheersgenie.features_v2.entity.RecordItemHeader;
import com.cheersmind.cheersgenie.features_v2.entity.SelectCourseRecord;
import com.cheersmind.cheersgenie.features_v2.entity.SelectCourseRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.features_v2.event.SelectCourseSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.CourseRelateMajorActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.SelectCourseAssistantActivity;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationActivity;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.activity.TrackRecordActivity;
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
            MultiItemEntity item = ((CareerPlanRecordRecyclerAdapter) adapter).getItem(position);
            //简单量表
            if (item != null && item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_SIMPLE_DIMENSION) {
                SimpleDimensionResult entity = (SimpleDimensionResult) item;

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
        }
    };

    //recycler子项的孩子视图点击监听
    protected BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity item = ((CareerPlanRecordRecyclerAdapter) adapter).getItem(position);
            if (item != null) {
                switch (view.getId()) {
                    //查看报告
                    case R.id.btn_report: {
                        TopicInfo topicInfo = new TopicInfo();
                        SelectCourseRecordItem entity = (SelectCourseRecordItem) item;
                        topicInfo.setTopicId(entity.getTopic_id());
                        topicInfo.setDimensionId(entity.getDimension_id());

                        getReferenceExam(topicInfo, POST_OPT_TYPE_REPORT);
                        break;
                    }
                    //继续答题
                    case R.id.btn_to_exam: {
                        TopicInfo topicInfo = new TopicInfo();
                        SelectCourseRecordItem entity = (SelectCourseRecordItem) item;
                        topicInfo.setTopicId(entity.getTopic_id());
                        topicInfo.setDimensionId(entity.getDimension_id());

                        getReferenceExam(topicInfo, POST_OPT_TYPE_GO_ON);
                        break;
                    }
                    //选科助手
                    case R.id.btn_select_course: {
                        SelectCourseAssistantActivity.startSelectCourseAssistantActivity(getContext(), childExamId);
                        break;
                    }
                    //查看可报考专业
                    case R.id.btn_can_select_major: {
                        List<String> courseCodes = null;
                        List<String> courseNames = null;
                        //系统推荐
                        if (item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_SYS_RECOMMEND_ITEM) {
                            SelectCourseRecord entity = (SelectCourseRecord) item;
                            courseCodes = entity.getRecommend_subject_codes();
                            courseNames = entity.getRecommend_subjects();

                        } else if (item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_SELECT_COURSE_HEADER) {//手选
                            SelectCourseRecord entity = (SelectCourseRecord) item;
                            courseCodes = entity.getCustom_subject_codes();
                            courseNames = entity.getCustom_subjects();
                        }

                        StringBuilder courseGroup = new StringBuilder();
                        if (ArrayListUtil.isNotEmpty(courseCodes)) {
                            for (String code : courseCodes) {
                                courseGroup.append(code);
                            }
                        }

                        StringBuilder courseName = new StringBuilder();
                        if (ArrayListUtil.isNotEmpty(courseNames)) {
                            for (String name : courseNames) {
                                courseName.append(name).append("、");
                            }
                        }

                        String nameStr = courseName.length() > 0 ? courseName.substring(0, courseName.length() - 1) : courseName.toString();
                        CourseRelateMajorActivity.startCourseRelateMajorActivity(getContext(), 1, courseGroup.toString(), nameStr);

                        break;
                    }
                    //查看高要求专业
                    case R.id.btn_high_require_major: {
                        List<String> courseCodes = null;
                        List<String> courseNames = null;
                        //系统推荐
                        if (item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_SYS_RECOMMEND_ITEM) {
                            SelectCourseRecord entity = (SelectCourseRecord) item;
                            courseCodes = entity.getRecommend_subject_codes();
                            courseNames = entity.getRecommend_subjects();

                        } else if (item.getItemType() == CareerPlanRecordRecyclerAdapter.LAYOUT_SELECT_COURSE_HEADER) {//手选
                            SelectCourseRecord entity = (SelectCourseRecord) item;
                            courseCodes = entity.getCustom_subject_codes();
                            courseNames = entity.getCustom_subjects();
                        }

                        StringBuilder courseGroup = new StringBuilder();
                        if (ArrayListUtil.isNotEmpty(courseCodes)) {
                            for (String code : courseCodes) {
                                courseGroup.append(code);
                            }
                        }

                        StringBuilder courseName = new StringBuilder();
                        if (ArrayListUtil.isNotEmpty(courseNames)) {
                            for (String name : courseNames) {
                                courseName.append(name).append("、");
                            }
                        }

                        String nameStr = courseName.length() > 0 ? courseName.substring(0, courseName.length() - 1) : courseName.toString();
                        CourseRelateMajorActivity.startCourseRelateMajorActivity(getContext(), 2, courseGroup.toString(), nameStr);

                        break;
                    }
                }

            } else {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), R.string.operate_fail);
                }
            }
        }
    };

    //职业分类点击监听
    CareerPlanRecordRecyclerAdapter.OnActCategoryClickListener actCategoryClickListener = new CareerPlanRecordRecyclerAdapter.OnActCategoryClickListener() {
        @Override
        public void onClick(OccupationCategory category) {
//            SimpleOccupationActivity.startOccupationActivity(getContext(), category);
            OccupationActivity.startOccupationActivity(getContext(), category);
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
                loadData();
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //适配器
        recyclerAdapter = new CareerPlanRecordRecyclerAdapter(getContext(), null);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recycleView.setAdapter(recyclerAdapter);
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
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //act职业分类点击监听
        recyclerAdapter.setActCategoryClickListener(actCategoryClickListener);

        //添加自定义分割线
        recyclerAdapter.addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.recycler_divider_f5f5f5_10dp, (ViewGroup) null));

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
        loadData();
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
     * 加载数据
     */
    private void loadData() {
        if (!TextUtils.isEmpty(childExamId)) {
            //加载报告
            loadReport(childExamId);
        } else {
            //加载生涯规划模块
            loadCareerPlanModule();
        }
    }

    /**
     * 加载生涯规划模块
     */
    private void loadCareerPlanModule() {
        //显示通信等待提示
//        LoadingView.getInstance().show(getContext(), httpTag);

        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        ModuleDto dto = new ModuleDto(1, 1);
        dto.setType(2);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());
        DataRequestService.getInstance().getModules(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
//                onFailureDefault(e);
                //空布局：网络连接有误，或者请求失败
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信等待
//                    LoadingView.getInstance().dismiss();

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamModuleRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamModuleRootEntity.class);

                    List<ExamModuleEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
//                        throw new QSCustomException(getString(R.string.operate_fail));
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    ExamModuleEntity examModule = dataList.get(0);
                    if (examModule.getChildModule() == null || TextUtils.isEmpty(examModule.getChildModule().getChild_module_id())) {
//                        throw new QSCustomException("暂无数据");
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //保存孩子测评ID
                    if (examModule.getChildModule() != null) {
                        childExamId = examModule.getChildModule().getChild_exam_id();
                        //跳转到成长档案
//                        TrackRecordActivity.startTrackRecordActivity(getContext(), childExamId);
                        //加载报告
                        loadReport(childExamId);

                    } else {
                        throw new QSCustomException(getString(R.string.operate_fail));
                    }

                } catch (QSCustomException qse) {
                    onFailure(qse);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getString(R.string.operate_fail)));
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 加载报告
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
    protected List<MultiItemEntity> careerPlanRecordToRecyclerMulti(CareerPlanRecordRootEntity record) {
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
                sysRmdItem.setRecommend_subjects(subject_archive.getRecommend_subjects());
                sysRmdItem.setRecommend_subject_codes(subject_archive.getRecommend_subject_codes());
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
                selectCourseRecord.setCustom_high_major_per(subject_archive.getCustom_high_major_per());
                //标记是否完成
                if (ArrayListUtil.isNotEmpty(subject_archive.getCustom_subjects())) {
                    selectCourseRecord.setFinish(true);
                } else {
                    selectCourseRecord.setFinish(false);
                }
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
            for (int i=0; i<items.size(); i++) {
                SelectCourseRecordItem item = items.get(i);
                item.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_SELECT_COURSE_ITEM);
                item.setIndex(i);
                //添加到系统推荐选科header中
                if (sysRmdHeader.isFinish()) {
                    sysRmdHeader.addSubItem(item);

                } else {//否则直接添加
                    resList.add(item);
                }

                //标记兄弟中的最后一个
                if (i == items.size() - 1) {
                    item.setLastInBrother(true);
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
            occupationHeader.setAct_areas(occupation_archive.getAct_areas());
            //标记是否完成
            if (ArrayListUtil.isNotEmpty(occupation_archive.getAct_areas())) {
                occupationHeader.setFinish(true);
            } else {
                occupationHeader.setFinish(false);
            }
            resList.add(occupationHeader);

            //子项header
            RecordItemHeader itemHeader1 = new RecordItemHeader("职业探索测试:").setCanExpand(false);
            itemHeader1.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_ITEM_HEADER);
            resList.add(itemHeader1);

            //职业探索子项
            List<SelectCourseRecordItem> items1 = occupation_archive.getItems();
            for (int i=0; i<items1.size(); i++) {
                SelectCourseRecordItem item = items1.get(i);
                item.setItemType(CareerPlanRecordRecyclerAdapter.LAYOUT_OCCUPATION_EXPLORE_ITEM);
                item.setIndex(i);
                //添加到子项Header中
                itemHeader1.addSubItem(item);

                //标记兄弟中的最后一个
                if (i == items.size() - 1) {
                    item.setLastInBrother(true);
                }
            }
        }

        return resList;
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


    /**
     * 选科成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectCourseSuccessNotice(SelectCourseSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            loadData();
        }
    }

}

