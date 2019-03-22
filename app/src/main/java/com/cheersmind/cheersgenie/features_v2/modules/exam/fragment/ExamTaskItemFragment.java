package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.DimensionOpenSuccessEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamTaskItemRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.dto.TaskItemDto;
import com.cheersmind.cheersgenie.features_v2.dto.TopicDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskStatus;
import com.cheersmind.cheersgenie.features_v2.entity.TaskDetailItemChildEntity;
import com.cheersmind.cheersgenie.features_v2.event.ActionCompleteEvent;
import com.cheersmind.cheersgenie.features_v2.event.TaskStatusEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 测评任务项
 */
public class ExamTaskItemFragment extends LazyLoadFragment {

    //孩子任务ID
    private String childTaskId;
    //任务状态
    private int taskStatus;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    Unbinder unbinder;

    //适配器的数据列表
    ExamTaskItemRecyclerAdapter recyclerAdapter;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //加载数据
            refreshData();
        }
    };

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多数据
            loadMoreData();
        }
    };

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            //判断任务项是否被锁，已经友好提示
            MultiItemEntity multiItem = recyclerAdapter.getData().get(position);
            if (multiItem instanceof ExamTaskItemEntity) {
                ExamTaskItemEntity taskItem = (ExamTaskItemEntity) multiItem;
                if (taskItem.getIs_lock() == Dictionary.IS_LOCKED_YSE) {
                    lockedTaskItemTip(taskItem);
                    return;
                }
            }

            int itemType = recyclerAdapter.getData().get(position).getItemType();
            switch (itemType) {
                //话题
                case Dictionary.TASK_ITEM_TYPE_TOPIC_COMMON:
                case Dictionary.TASK_ITEM_TYPE_TOPIC_363:
                case Dictionary.TASK_ITEM_TYPE_TOPIC_373:
                case Dictionary.TASK_ITEM_TYPE_TOPIC_321_42: {
                    ExamTaskItemEntity entity = (ExamTaskItemEntity) recyclerAdapter.getData().get(position);
                    //是否加载了量表
                    if (ArrayListUtil.isNotEmpty(entity.getSubItems())) {
                        if (entity.isExpanded()) {
                            adapter.collapse(position);
                        } else {
                            adapter.expand(position);
                        }

                    } else {
                        //加载量表
                        if (entity.getChildItem() != null) {
                            loadDimensions(entity.getItem_id(),
                                    entity.getChildItem().getChild_exam_id(),
                                    UCManager.getInstance().getDefaultChild().getChildId(),
                                    entity,
                                    position);
                        } else {
                            if (getActivity() != null) {
                                ToastUtil.showShort(getActivity().getApplication(), "获取测评失败，请稍后再试");
                            }
                        }
                    }

                    break;
                }
                //量表
                case Dictionary.TASK_ITEM_TYPE_DIMENSION: {
                    DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity)recyclerAdapter.getData().get(position);
                    int parentPosition = adapter.getParentPosition(dimensionInfoEntity);
                    ExamTaskItemEntity entity = (ExamTaskItemEntity) ((ExamTaskItemRecyclerAdapter) adapter).getItem(parentPosition);
                    if (entity != null) {
                        TaskDetailItemChildEntity childTaskItem = entity.getChildItem();
                        //构建模拟的话题对象
                        TopicInfoEntity topicInfoEntity = new TopicInfoEntity();
                        topicInfoEntity.setTopicId(entity.getItem_id());//话题ID
                        topicInfoEntity.setExamId(dimensionInfoEntity.getExamId());//测评ID
                        TopicInfoChildEntity childTopic = new TopicInfoChildEntity();//孩子话题对象
                        childTopic.setChildTopicId("1");//设置一个模拟的孩子话题ID
                        childTopic.setChildExamId(entity.getChildItem().getChild_exam_id());//孩子测评ID
                        topicInfoEntity.setChildTopic(childTopic);

                        //如果任务项已完成则查看话题报告
//                        if (childTaskItem != null && childTaskItem.getStatus() == Dictionary.TASK_STATUS_COMPLETED) {
//
//                            ExamReportDto dto = new ExamReportDto();
//                            dto.setChildExamId(dimensionInfoEntity.getChild_exam_id());//孩子测评ID
//                            dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
//
//                            //只有一个量表则查看量表报告
//                            if (ArrayListUtil.isNotEmpty(entity.getSubItems())
//                                    && entity.getSubItems().size() == 1) {
//                                dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
//                                dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
//
//                            } else {
//                                dto.setRelationId(dimensionInfoEntity.getTopicId());
//                                dto.setRelationType(Dictionary.REPORT_TYPE_TOPIC);
//                            }
//
//                            ExamReportActivity.startExamReportActivity(getContext(), dto);
//
//                        } else {
//                            //设置量表集合到话题对象中
//                            topicInfoEntity.setDimensions(entity.getSubItems());
//                            //点击量表项的操作
//                            operateClickDimension(dimensionInfoEntity, topicInfoEntity);
//                        }

                        //设置量表集合到话题对象中
                        topicInfoEntity.setDimensions(entity.getSubItems());
                        //点击量表项的操作
                        operateClickDimension(dimensionInfoEntity, topicInfoEntity);
                    }

                    break;
                }
                //文章
                case Dictionary.TASK_ITEM_TYPE_ARTICLE:
                case Dictionary.TASK_ITEM_TYPE_VIDEO:
                case Dictionary.TASK_ITEM_TYPE_AUDIO:
                case Dictionary.TASK_ITEM_TYPE_PRACTICE:
                case Dictionary.TASK_ITEM_TYPE_CHOOSE_COURSE: {
                    //跳转到具体类型的详情页
                    ExamTaskItemEntity entity = (ExamTaskItemEntity) recyclerAdapter.getData().get(position);
                    String articleId = entity.getItem_id();
                    String ivMainUrl = "";
                    String articleTitle = entity.getItem_name();
                    String childTaskItemId = entity.getChildItem().getChild_item_id();
                    int taskItemType = entity.getItem_type();
                    ArticleDetailActivity.startArticleDetailActivityByTask(
                            getContext(), articleId, ivMainUrl, articleTitle, childTaskItemId, taskItemType);

                    break;
                }
            }
        }
    };


    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    TaskItemDto dto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_exam_task_item;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new ExamTaskItemRecyclerAdapter(null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        //添加一个空HeaderView，用于显示顶部分割线
//        recyclerAdapter.addHeaderView(new View(getContext()));
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_exam_task_item));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        //获取数据
        Bundle bundle = getArguments();
        if(bundle!=null) {
            childTaskId = bundle.getString(DtoKey.CHILD_TASK_ID);
            taskStatus = bundle.getInt(DtoKey.TASK_STATUS);
        }

        //设置任务状态
        recyclerAdapter.setTaskStatus(taskStatus);

        dto = new TaskItemDto(pageNum, PAGE_SIZE);
        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        dto.setChildId(childId);
        dto.setChildTaskId(childTaskId);

        long userId = UCManager.getInstance().getUserId();
        System.out.println(userId);
    }

    @Override
    protected void lazyLoad() {
        loadMoreData();
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
     * 刷新数据
     */
    private void refreshData() {
        //下拉刷新
        pageNum = 1;
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        dto.setPage(pageNum);
        DataRequestService.getInstance().getExamTaskDetailItems(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启上拉加载功能
                recyclerAdapter.setEnableLoadMore(true);
                //结束下拉刷新动画
                swipeRefreshLayout.setRefreshing(false);
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                //清空列表数据
                recyclerAdapter.setNewData(null);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamTaskItemRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamTaskItemRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<ExamTaskItemEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //转成列表的数据项
                    List recyclerItem = toMultiItemEntity(dataList);

                    //下拉刷新
                    recyclerAdapter.setNewData(recyclerItem);
                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd(true);
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                }
            }
        }, httpTag, getActivity());
    }


    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        dto.setPage(pageNum);
        DataRequestService.getInstance().getExamTaskDetailItems(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                if (recyclerAdapter.getData().size() == 0) {
                    //设置空布局：网络错误
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                } else {
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启下拉刷新功能
                    swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamTaskItemRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamTaskItemRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<ExamTaskItemEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //转成列表的数据项
                    List recyclerItem = toMultiItemEntity(dataList);

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(recyclerItem);

                    } else {
                        recyclerAdapter.addData(dataList);
                    }

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd(true);
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    if (recyclerAdapter.getData().size() == 0) {
                        //设置空布局：没有数据，可重载
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    } else {
                        //加载失败处理
                        recyclerAdapter.loadMoreFail();
                    }
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 任务子项集合转成用于适配recycler的分组数据模型
     *
     * @param taskItemList 任务子项集合
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> toMultiItemEntity(List<ExamTaskItemEntity> taskItemList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(taskItemList)) {
            resList = new ArrayList<>();
            for (ExamTaskItemEntity taskItem : taskItemList) {
                //添加适配器的测评模型
                resList.add(taskItem);
            }
        }

        return resList;
    }

    /**
     * 加载话题下的量表
     */
    private void loadDimensions(String topicId, String childExamId, String childId, final ExamTaskItemEntity entity, final int position) {
        //开启通信等待提示
        LoadingView.getInstance().show(getContext(), httpTag);

        TopicDto topicDto = new TopicDto();
        topicDto.setTopicId(topicId);
        topicDto.setChildExamId(childExamId);
        topicDto.setChildId(childId);

        DataRequestService.getInstance().getDimensionsInTopic(topicDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    DimensionRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, DimensionRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<DimensionInfoEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        throw new QSCustomException("该测评无数据");
                    }

                    //如果已经存在子集合则清空
                    if (ArrayListUtil.isNotEmpty(entity.getSubItems())) {
                        entity.getSubItems().clear();
                    }

                    for (int i=0; i<dataList.size(); i++) {
                        DimensionInfoEntity dimension = dataList.get(i);
                        //标记第一个
                        if (i == 0) {
                            dimension.setFirstInTopic(true);
                        }
                        //标记最后一个
                        if (i == dataList.size() - 1) {
                            dimension.setLastInTopic(true);
                        }
                        dimension.setItemType(Dictionary.TASK_ITEM_TYPE_DIMENSION);
                        entity.addSubItem(dimension);
                    }

                    //实际刷新的布局索引得加载header的数量
                    int realLayoutPosition = position + recyclerAdapter.getHeaderLayoutCount();
                    recyclerAdapter.expand(realLayoutPosition);

                } catch (QSCustomException e) {
                    onFailure(e);
                } catch (Exception e) {
                    onFailure(new QSCustomException("获取测评失败，请稍后再试"));
                }

            }
        }, httpTag, getActivity());
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
     * 操作点击量表
     * @param dimensionInfoEntity 量表
     */
    protected void operateClickDimension(final DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        if( dimensionInfoEntity == null ){
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), "打开测评失败，请稍后再试");
            }
            return;
        }

        //被锁定
        if(dimensionInfoEntity.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE){
            //锁定提示
            lockedDimensionTip(dimensionInfoEntity, topicInfoEntity);

        } else {
            //孩子量表对象
            DimensionInfoChildEntity entity = dimensionInfoEntity.getChildDimension();
            //从未开启过的状态
            if (entity == null) {
                //跳转到量表详细页面，传递量表对象和话题对象
                DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                        dimensionInfoEntity, topicInfoEntity,
                        Dictionary.EXAM_STATUS_DOING,
                        Dictionary.FROM_ACTIVITY_TO_TASK_DETAIL);

            } else {//已经开启过的状态
                //未完成状态
                if(entity.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE){
                    //跳转到量表详细页面，传递量表对象和话题对象
                    DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                            dimensionInfoEntity, topicInfoEntity,
                            Dictionary.EXAM_STATUS_DOING,
                            Dictionary.FROM_ACTIVITY_TO_TASK_DETAIL);

                } else {
                    //已完成状态，显示报告
//                    showDimensionReport(dimensionInfoEntity);

                    ExamReportDto dto = new ExamReportDto();
                    dto.setChildExamId(dimensionInfoEntity.getChild_exam_id());//孩子测评ID
                    dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                    dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
                    dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);

                    ExamReportActivity.startExamReportActivity(getContext(), dto);
                }
            }
        }

    }


    /**
     * 量表被锁定的提示
     */
    protected void lockedDimensionTip(DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {

        if(!TextUtils.isEmpty(dimensionInfoEntity.getPreDimensions())){
            String [] dimensionIds = dimensionInfoEntity.getPreDimensions().split(",");
            List<DimensionInfoEntity> dimensions = topicInfoEntity.getDimensions();
            if(dimensionIds.length>0 && dimensions.size()>0){
                StringBuilder stringBuffer = new StringBuilder("");

                for(DimensionInfoEntity dimension : dimensions){
                    for (String dimensionId : dimensionIds) {
                        if (dimensionId.equals(dimension.getDimensionId())) {
                            stringBuffer.append(dimension.getDimensionName());
                            stringBuffer.append("、");
                        }
                    }
                }

                if(stringBuffer.length() > 0 && getActivity() != null){
                    String str = getActivity().getResources().getString(R.string.lock_tip, stringBuffer.substring(0, stringBuffer.length() - 1));
                    ToastUtil.showLong(getActivity().getApplication(), str);
                }
            }
        }
    }


    /**
     * 显示量表报告
     */
    protected void showDimensionReport(DimensionInfoEntity dimensionInfo) {
//        ToastUtil.showShort(getContext(), "查看该量表报告");
        try {
            new DimensionReportDialog(getContext(), dimensionInfo, null).show();
        } catch (Exception e) {
            e.printStackTrace();
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), e.getMessage());
            }
        }
    }


    /**
     * 量表开启成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDimensionOpenSuccessNotice(DimensionOpenSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            //量表
            DimensionInfoEntity dimension = event.getDimension();
            if (dimension != null && dimension.getChildDimension() != null) {
                //更新量表的孩子量表对象
                List<MultiItemEntity> data = recyclerAdapter.getData();
                if (ArrayListUtil.isNotEmpty(data)) {
                    for (MultiItemEntity item : data) {
                        if (item instanceof DimensionInfoEntity) {
                            DimensionInfoEntity t = (DimensionInfoEntity) item;
                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
                            if (t.getTopicId().equals(dimension.getTopicId())
                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
                                //刷新对应量表的列表项
                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                                break;
                            }
                        }
                    }
                }
            }
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
        //孩子量表对象
        DimensionInfoChildEntity childDimension = dimension.getChildDimension();

        //孩子量表不为空
        if (childDimension != null) {
            //局部刷新量表对应的视图项
            List<MultiItemEntity> data = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(data)) {
                //header模型位置
                int headerPosition = 0;
                //量表位置
                int dimensionPosition = 0;
                ExamTaskItemEntity taskItem = null;
                DimensionInfoEntity t = null;

                for (int i = 0; i < data.size(); i++) {
                    MultiItemEntity item = data.get(i);

                    if (item instanceof ExamTaskItemEntity) {
                        int item_type = ((ExamTaskItemEntity) item).getItem_type();
                        //话题类型
                        if (item_type == Dictionary.TASK_ITEM_TYPE_TOPIC_COMMON
                                || item_type == Dictionary.TASK_ITEM_TYPE_TOPIC_363
                                || item_type == Dictionary.TASK_ITEM_TYPE_TOPIC_373
                                || item_type == Dictionary.TASK_ITEM_TYPE_TOPIC_321_42) {
                            //标记话题对应任务项
                            taskItem = (ExamTaskItemEntity) item;
                            //标记header模型位置
                            headerPosition = i;
                        }

                    } else if (item instanceof DimensionInfoEntity) {
                        t = (DimensionInfoEntity) item;
                        dimensionPosition = i;
                    }

                    //t不为空
                    if (t != null ) {
                        //找出同一个量表，设置孩子量表，然后局部刷新列表项
                        if (t.getTopicId().equals(dimension.getTopicId())
                                && t.getDimensionId().equals(dimension.getDimensionId())) {
                            //刷新对应量表的列表项
                            t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                            int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                            recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内

                            //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
                            if (isMeetUnlockDimensionCondition(taskItem)) {
                                //刷新话题下的量表列表
                                loadDimensions(taskItem.getItem_id(),
                                        taskItem.getChildItem().getChild_exam_id(),
                                        UCManager.getInstance().getDefaultChild().getChildId(),
                                        taskItem,
                                        dimensionPosition);

                            } else {
                                //判断话题是否完成
                                if (isTopicComplete(taskItem)) {
                                    //设置话题为完成状态
                                    taskItem.getChildItem().setStatus(Dictionary.TASK_STATUS_COMPLETED);
                                    //刷新header模型对应的列表项
                                    refreshHeader(headerPosition);

                                    //处理任务项解锁
                                    handlerUnlockTaskItem();

                                    //处理任务是否完成
                                    if (taskItem.getChildItem() != null) {
                                        //获取任务状态
                                        doGetTaskStatus(UCManager.getInstance().getDefaultChild().getChildId(), childTaskId);
                                    }
                                }
                            }

                            break;
                        }

                    }
                }
            }

        }

    }


    /**
     * 是否满足量表解锁条件
     * @param taskItem 任务项：话题
     * @return true 满足
     */
    protected boolean isMeetUnlockDimensionCondition(ExamTaskItemEntity taskItem) {
        //是否满足解锁条件
        boolean isMeetUnlockCondition = false;

        if (taskItem != null) {
            List<DimensionInfoEntity> dimensions = taskItem.getSubItems();
            if (ArrayListUtil.isNotEmpty(dimensions)) {
                //是否有被锁的量表
                boolean hasLockedDimension = false;
                //被锁的量表
                DimensionInfoEntity lockedDimension = null;

                for (DimensionInfoEntity dimension : dimensions) {
                    //被锁状态
                    if (dimension.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                        lockedDimension = dimension;
                        hasLockedDimension = true;
                        break;
                    }
                }

                //存在被锁的量表则判断是否满足解锁条件
                if (hasLockedDimension) {

                    //解锁必须先做完的量表id集合
                    String [] dimensionIds = lockedDimension.getPreDimensions().split(",");
                    Set<String> dimensionSet = new HashSet<>(Arrays.asList(dimensionIds));

                    isMeetUnlockCondition = true;
                    //存在一个未做完的量表，则视为不满足解锁条件
                    for (DimensionInfoEntity dimension : dimensions) {
                        if (dimensionSet.contains(dimension.getDimensionId())) {
                            if (dimension.getChildDimension() == null
                                    || dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                                isMeetUnlockCondition = false;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return isMeetUnlockCondition;
    }


    /**
     * 判断话题（场景）是否完成
     * @param taskItem 任务项：话题
     */
    protected boolean isTopicComplete(ExamTaskItemEntity taskItem) {
        boolean isComplete = false;

        if (taskItem != null) {
            List<DimensionInfoEntity> dimensions = taskItem.getSubItems();
            //量表非空
            if (ArrayListUtil.isNotEmpty(dimensions)) {
                //话题是否完成
                boolean isTopicComplete = true;
                for (DimensionInfoEntity tempDimension : dimensions) {
                    //孩子量表对象为空或者状态为0，视为未完成
                    if (tempDimension.getChildDimension() == null
                            || tempDimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                        isTopicComplete = false;
                        break;
                    }
                }

                //话题（场景）完成，则刷新header模型对应的列表项
                if (isTopicComplete) {
                    //孩子话题非空
                    if (taskItem.getChildItem() != null) {
//                        //设置话题为完成状态
//                        taskItem.getChildItem().setStatus(Dictionary.TASK_STATUS_COMPLETED);
                        isComplete = true;
                    }
                }
            }
        }

        return isComplete;
    }


    /**
     * 刷新header模型对应的列表项
     * @param headerPosition 列表索引
     */
    protected void refreshHeader(int headerPosition) {
        int tempHeaderPosition = headerPosition + recyclerAdapter.getHeaderLayoutCount();
        recyclerAdapter.notifyItemChanged(tempHeaderPosition);//局部刷新列表项，把header计算在内
    }


    /**
     * 处理任务项解锁
     */
    protected void handlerUnlockTaskItem() {
        List<MultiItemEntity> data = recyclerAdapter.getData();

        if (ArrayListUtil.isNotEmpty(data)) {

            for (int i=0; i<data.size(); i++) {
                MultiItemEntity multiItem = data.get(i);
                //任务项
                if (multiItem instanceof ExamTaskItemEntity) {
                    ExamTaskItemEntity taskItem = (ExamTaskItemEntity) multiItem;
                    //被锁状态
                    if (taskItem.getIs_lock() == Dictionary.IS_LOCKED_YSE) {

                        //解锁必须先做完的任务项id集合
                        String[] taskItemIds = taskItem.getPre_items().split(",");
                        Set<String> taskItemSet = new HashSet<>(Arrays.asList(taskItemIds));

                        //是否满足解锁条件
                        boolean isMeetUnlockCondition = true;
                        //存在一个未做完的前置任务项，则视为不满足解锁条件
                        for (MultiItemEntity item2 : data) {

                            if (item2 instanceof ExamTaskItemEntity
                                    && taskItemSet.contains(((ExamTaskItemEntity) item2).getItem_id())) {

                                if (((ExamTaskItemEntity) item2).getChildItem() == null
                                        || ((ExamTaskItemEntity) item2).getChildItem().getStatus() != Dictionary.TASK_STATUS_COMPLETED) {
                                    //不满足解锁条件
                                    isMeetUnlockCondition = false;
                                    break;
                                }
                            }
                        }

                        //满足条件直接本地刷新（是否需要重新请求列表？）
                        if (isMeetUnlockCondition) {
                            taskItem.setIs_lock(Dictionary.IS_LOCKED_NO);
                            int realLayoutPosition = i + recyclerAdapter.getHeaderLayoutCount();
                            recyclerAdapter.notifyItemChanged(realLayoutPosition);
                        }
                    }
                }
            }

        }

    }


    /**
     * 任务项被锁定的提示
     */
    protected void lockedTaskItemTip(ExamTaskItemEntity taskItem) {

        if(!TextUtils.isEmpty(taskItem.getPre_items())){
            String [] taskItemIds = taskItem.getPre_items().split(",");
            List<MultiItemEntity> multiItems = recyclerAdapter.getData();
            if(taskItemIds.length > 0 && multiItems.size() > 0){
                StringBuilder stringBuffer = new StringBuilder("");

                for(MultiItemEntity multiItem : multiItems){
                    if (multiItem instanceof ExamTaskItemEntity) {
                        for (String taskItemId : taskItemIds) {
                            if (taskItemId.equals(((ExamTaskItemEntity) multiItem).getItem_id())) {
                                stringBuffer.append(((ExamTaskItemEntity) multiItem).getItem_name());
                                stringBuffer.append("、");
                            }
                        }
                    }
                }

                if(stringBuffer.length() > 0 && getActivity() != null) {
                    String str = getActivity().getResources().getString(R.string.task_item_lock_tip, stringBuffer.substring(0, stringBuffer.length() - 1));
                    ToastUtil.showLong(getActivity().getApplication(), str);
                }
            }
        } else {
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), "未找到解锁条件，请稍后再试");
            }
        }
    }

    /**
     * 获取任务状态
     * @param childId 孩子ID
     * @param childTaskId 孩子任务ID
     */
    private void doGetTaskStatus(String childId, String childTaskId) {
        DataRequestService.getInstance().getTaskStatus(childId, childTaskId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamTaskStatus taskStatus = InjectionWrapperUtil.injectMap(dataMap, ExamTaskStatus.class);
                    //任务完成则发送事件
                    if (taskStatus.getStatus() == Dictionary.TASK_STATUS_COMPLETED) {
                        //发送任务状态事件
                        EventBus.getDefault().post(new TaskStatusEvent(taskStatus));
                    }

                } catch (Exception e) {
                    onFailure(new QSCustomException("获取任务状态失败，请稍后再试"));
                }

            }
        }, httpTag, getActivity());
    }


    /**
     * 动作完成的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionCompleteNotice(ActionCompleteEvent event) {
        //刷新对应任务项的视图
        if (hasLoaded && event != null && !TextUtils.isEmpty(event.getItemId())) {
            List<MultiItemEntity> data = recyclerAdapter.getData();

            if (ArrayListUtil.isNotEmpty(data)) {

                for (int i = 0; i < data.size(); i++) {
                    MultiItemEntity item = data.get(i);

                    if (item instanceof ExamTaskItemEntity) {
                        ExamTaskItemEntity taskItem = (ExamTaskItemEntity) item;

                        if (event.getItemId().equals(taskItem.getItem_id())) {
                            //设置任务项为完成状态
                            TaskDetailItemChildEntity childItem = taskItem.getChildItem();
                            if (childItem != null) {
                                childItem.setStatus(Dictionary.TASK_STATUS_COMPLETED);
                            }

                            //刷新视图
                            int position = i + recyclerAdapter.getHeaderLayoutCount();
                            recyclerAdapter.notifyItemChanged(position);

                            //处理任务是否完成
                            if (taskItem.getChildItem() != null) {
                                //获取任务状态
                                doGetTaskStatus(UCManager.getInstance().getDefaultChild().getChildId(), childTaskId);
                            }
                        }
                    }
                }
            }
        }
    }

}

