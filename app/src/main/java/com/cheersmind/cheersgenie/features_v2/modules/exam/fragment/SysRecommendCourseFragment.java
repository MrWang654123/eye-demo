package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.SysRmdCourseRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourse;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourseItem;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.CourseRelateMajorActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.RecommendMajorActivity;
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
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 系统推荐选科
 */
public class SysRecommendCourseFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评id
    private String childExamId;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器
    SysRmdCourseRecyclerAdapter recyclerAdapter;

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity item = ((SysRmdCourseRecyclerAdapter) adapter).getItem(position);
            //简单量表
            if (item != null && item.getItemType() == SysRmdCourseRecyclerAdapter.LAYOUT_SIMPLE_DIMENSION) {
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
            MultiItemEntity item = ((SysRmdCourseRecyclerAdapter) adapter).getItem(position);
            if (item != null) {
                switch (view.getId()) {
                    //查看报告
                    case R.id.btn_report: {
                        TopicInfo topicInfo = new TopicInfo();
                        SysRmdCourseItem entity = (SysRmdCourseItem) item;
                        topicInfo.setTopicId(entity.getTopic_id());
                        topicInfo.setDimensionId(entity.getDimension_id());

                        getReferenceExam(topicInfo, POST_OPT_TYPE_REPORT);
                        break;
                    }
                    //继续答题
                    case R.id.btn_to_exam: {
                        TopicInfo topicInfo = new TopicInfo();
                        SysRmdCourseItem entity = (SysRmdCourseItem) item;
                        topicInfo.setTopicId(entity.getTopic_id());
                        topicInfo.setDimensionId(entity.getDimension_id());

                        getReferenceExam(topicInfo, POST_OPT_TYPE_GO_ON);
                        break;
                    }
                    //查看可报考专业
                    case R.id.btn_can_select_major: {
                        SysRmdCourse entity = (SysRmdCourse) item;
                        List<String> courseCodes = entity.getRecommend_subject_codes();
                        List<String> courseNames = entity.getRecommend_subjects();

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
                        SysRmdCourse entity = (SysRmdCourse) item;
                        List<String> courseCodes = entity.getRecommend_subject_codes();
                        List<String> courseNames = entity.getRecommend_subjects();

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_sys_rmd_course;
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
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_sys_rmd_course));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadData(childExamId);
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //适配器
        recyclerAdapter = new SysRmdCourseRecyclerAdapter(getContext(),  null);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recycleView.setAdapter(recyclerAdapter);
//        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        //网格布局管理器
        final int count = getResources().getInteger(R.integer.career_plan_record_simple_dimension_count);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), count);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return recyclerAdapter.getItemViewType(position) == SysRmdCourseRecyclerAdapter.LAYOUT_SIMPLE_DIMENSION ? 1 : count;
            }
        });
        //必须在setAdapter之后
        recycleView.setLayoutManager(gridLayoutManager);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

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

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void lazyLoad() {
        loadData(childExamId);
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
        loadData(childExamId);
    }

    @OnClick({R.id.btn_add_major})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //添加观察专业
            case R.id.btn_add_major: {
                RecommendMajorActivity.startRmdMajorActivity(getContext(), childExamId);
                break;
            }
        }
    }

    /**
     * 加载数据
     */
    private void loadData(String childExamId) {

        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getSysRmdCourse(childExamId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    SysRmdCourse entity = InjectionWrapperUtil.injectMap(dataMap, SysRmdCourse.class);

                    if (entity == null || ArrayListUtil.isEmpty(entity.getItems())) {
                        //空布局：无数据
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    List<MultiItemEntity> multiItemEntities = sysRmdCourseToRecyclerMulti(entity);
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
    }


    /**
     * SysRmdCourse转成用于适配recycler的分组数据模型List<MultiItemEntity>
     *
     * @param record SysRmdCourse
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> sysRmdCourseToRecyclerMulti(SysRmdCourse record) {
        List<MultiItemEntity> resList = null;

        if (record != null && ArrayListUtil.isNotEmpty(record.getItems())) {
            resList = new ArrayList<>();

            //系统推荐选科Header
            SysRmdCourse sysRmdCourseHeader = new SysRmdCourse();
            sysRmdCourseHeader.setRecommend_subjects(record.getRecommend_subjects());
            sysRmdCourseHeader.setRecommend_subject_codes(record.getRecommend_subject_codes());
            sysRmdCourseHeader.setRecommend_major_per(record.getRecommend_major_per());
            sysRmdCourseHeader.setRecommend_high_major_per(record.getRecommend_high_major_per());
            sysRmdCourseHeader.setItemType(SysRmdCourseRecyclerAdapter.LAYOUT_SYS_RECOMMEND_HEADER);
            //标记是否完成
            if (ArrayListUtil.isNotEmpty(sysRmdCourseHeader.getRecommend_subjects())) {
                sysRmdCourseHeader.setFinish(true);
            } else {
                sysRmdCourseHeader.setFinish(false);
            }

            if (sysRmdCourseHeader.isFinish()) {
                resList.add(sysRmdCourseHeader);
            }

            //子项
            if (ArrayListUtil.isNotEmpty(record.getItems())) {
                List<SysRmdCourseItem> items = record.getItems();
                for (SysRmdCourseItem item : items) {
                    item.setItemType(SysRmdCourseRecyclerAdapter.LAYOUT_ITEM_COMMON);
                    resList.add(item);

                    //有简单量表
                    if (ArrayListUtil.isNotEmpty(item.getDimensions())) {
                        List<SimpleDimensionResult> dimensions = item.getDimensions();

                        for (SimpleDimensionResult dimensionResult : dimensions) {
                            dimensionResult.setItemType(SysRmdCourseRecyclerAdapter.LAYOUT_SIMPLE_DIMENSION);
                            item.addSubItem(dimensionResult);
                        }
                    }
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
     * @param postOptType 后续的操作乐星
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
                        dto.setChildExamId(childExamId);//孩子测评ID
                        dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                        dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);//量表报告类型
                        dto.setRelationId(dimension.getTopicDimensionId());//话题量表ID

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

}

