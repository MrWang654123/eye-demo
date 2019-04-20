package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamTaskRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ExamTaskDto;
import com.cheersmind.cheersgenie.features_v2.dto.ModuleDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamModuleEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamModuleRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskStatus;
import com.cheersmind.cheersgenie.features_v2.event.AddExamTaskSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.event.TaskStatusEvent;
import com.cheersmind.cheersgenie.features_v2.modules.college.activity.CollegeRankActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamTaskAddActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamTaskDetailActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.SelectCourseAssistantActivity;
import com.cheersmind.cheersgenie.features_v2.modules.major.activity.MajorActivity;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 生涯规划
 */
public class CareerPlanFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;
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
    //添加自定义任务按钮
    @BindView(R.id.fabAddTaskItem)
    FloatingActionButton fabAddTaskItem;

    //适配器的数据列表
    ExamTaskRecyclerAdapter recyclerAdapter;

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

            ExamTaskEntity entity = recyclerAdapter.getData().get(position);
            //判断任务项是否被锁，友好提示
            if (entity.getIs_lock() == Dictionary.IS_LOCKED_YSE) {
                lockedTaskTip(entity);

            } else {
                ExamTaskDetailActivity.startExamTaskDetailActivity(getContext(), entity);
            }
        }
    };


    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ExamTaskDto dto;

    //孩子测评ID：用于生涯档案
    String childExamId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_career_plan;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new ExamTaskRecyclerAdapter(getContext(), R.layout.recycleritem_exam_task, null);
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
        if (getContext() != null) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider_custom));
            recycleView.addItemDecoration(divider);
        }
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //滑动监听
//        try {
//            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                    //停止Fling
                    recycleView.stopScroll();
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_career_plan));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadData();
            }
        });
        //空布局背景色
        if (getContext() != null) {
            emptyLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
        //初始隐藏添加任务按钮
        fabAddTaskItem.setVisibility(View.INVISIBLE);

        dto = new ExamTaskDto(pageNum, PAGE_SIZE);
        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        dto.setChildId(childId);
    }

    @Override
    protected void lazyLoad() {
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

    @OnClick({R.id.fabAddTaskItem, R.id.cl_college,
            R.id.cl_major, R.id.cl_occupation, R.id.cl_report_tip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //添加任务
            case R.id.fabAddTaskItem: {
//                ExamTaskAddActivity.startExamTaskAddActivity(getContext(), "123123123123123");
                Intent intent = new Intent(getContext(), ExamTaskAddActivity.class);
                Bundle extras = new Bundle();
                extras.putString(DtoKey.CHILD_MODULE_ID, dto.getChildModuleId());
                intent.putExtras(extras);
                startActivityForResult(intent, REQUEST_CODE_ADD_TASK, extras);
                break;
            }
            //学校
            case R.id.cl_college: {
                CollegeRankActivity.startCollegeActivity(getContext());
                break;
            }
            //专业
            case R.id.cl_major: {
                MajorActivity.startMajorActivity(getContext());
                break;
            }
            //职业
            case R.id.cl_occupation: {
                OccupationActivity.startOccupationActivity(getContext(), null);
                break;
            }
            //选科助手
            case R.id.cl_report_tip: {
                SelectCourseAssistantActivity.startSelectCourseAssistantActivity(getContext(), childExamId);
                break;
            }
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {
        //如果孩子模块ID为空，则先获取模块，否则直接获取任务列表
        if (TextUtils.isEmpty(dto.getChildModuleId())) {
            loadCareerPlanModule();
        } else {
            loadMoreData();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        //下拉刷新
        pageNum = 1;
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        //确保显示下拉刷新
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        dto.setPage(pageNum);
        DataRequestService.getInstance().getExamTasks(dto, new BaseService.ServiceCallback() {
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
                    ExamTaskRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamTaskRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<ExamTaskEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //下拉刷新
                    recyclerAdapter.setNewData(dataList);
                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
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
        DataRequestService.getInstance().getExamTasks(dto, new BaseService.ServiceCallback() {
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
                    ExamTaskRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamTaskRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<ExamTaskEntity> dataList = rootEntity.getItems();
//                    MessageEntity messageEntity = dataList.get(0);
//                    for (int i=0; i<20; i++) {
//                        dataList.add(messageEntity);
//                    }

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(dataList);

                    } else {
                        recyclerAdapter.addData(dataList);
                    }

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
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


    public static final int REQUEST_CODE_ADD_TASK = 80;
    public static final int RESULT_CODE_ADD_TASK = 81;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_CODE_ADD_TASK) {
            ExamTaskEntity selectItem = (ExamTaskEntity) data.getSerializableExtra(DtoKey.ADD_EXAM_TASK);
            recyclerAdapter.addData(selectItem);
            totalCount++;
            recycleView.scrollToPosition(recyclerAdapter.getHeaderLayoutCount() + recyclerAdapter.getData().size());
        }
    }


    /**
     * 加载生涯规划模块
     */
    private void loadCareerPlanModule() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突
        //显示通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        ModuleDto dto = new ModuleDto(1, 1);
        dto.setType(2);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());
        DataRequestService.getInstance().getModules(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamModuleRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamModuleRootEntity.class);

                    List<ExamModuleEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    ExamModuleEntity examModule = dataList.get(0);
                    if (examModule.getChildModule() == null || TextUtils.isEmpty(examModule.getChildModule().getChild_module_id())) {
                        throw new QSCustomException("暂无数据");
                    }

                    //保存孩子测评ID
                    if (examModule.getChildModule() != null) {
                        childExamId = examModule.getChildModule().getChild_exam_id();
                    }

                    CareerPlanFragment.this.dto.setChildModuleId(examModule.getChildModule().getChild_module_id());
                    //加载可添加任务
                    loadAddTaskList();
                    //加载模块下的任务列表
                    loadMoreData();

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 任务被锁定的提示
     */
    protected void lockedTaskTip(ExamTaskEntity task) {

        if(!TextUtils.isEmpty(task.getPre_tasks())){
            String [] taskIds = task.getPre_tasks().split(",");
            List<ExamTaskEntity> tasks = recyclerAdapter.getData();
            if(taskIds.length > 0 && tasks.size() > 0){
                StringBuilder stringBuffer = new StringBuilder("");

                for(ExamTaskEntity item : tasks) {
                    for (String taskId : taskIds) {
                        if (taskId.equals(item.getTask_id())) {
                            stringBuffer.append(item.getTask_name());
                            stringBuffer.append("、");
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
     * 任务状态的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskStatusNotice(TaskStatusEvent event) {
        ExamTaskStatus taskStatus = event.getTaskStatus();
        if (taskStatus != null) {
            int status = taskStatus.getStatus();
            //如果是完成状态，则刷新对应任务视图
            if (status == Dictionary.TASK_STATUS_COMPLETED) {
                String taskId = taskStatus.getTask_id();
                String childTaskId = taskStatus.getChild_task_id();
                List<ExamTaskEntity> data = recyclerAdapter.getData();

                if (ArrayListUtil.isNotEmpty(data)) {
                    for (int i = 0; i < data.size(); i++) {
                        ExamTaskEntity item = data.get(i);
                        try {
                            if (item.getTask_id().equals(taskId)
                                    && item.getChildTask().getChild_task_id().equals(childTaskId)) {
                                //设置完成状态
                                item.getChildTask().setStatus(Dictionary.TASK_STATUS_COMPLETED);
                                //刷新视图
                                int position = recyclerAdapter.getHeaderLayoutCount() + i;
                                recyclerAdapter.notifyItemChanged(position);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //处理任务解锁
                    handlerUnlockTask();
                }
            }
        }
    }


    /**
     * 处理任务解锁
     */
    protected void handlerUnlockTask() {
        List<ExamTaskEntity> data = recyclerAdapter.getData();

        if (ArrayListUtil.isNotEmpty(data)) {

            for (int i=0; i<data.size(); i++) {
                ExamTaskEntity item = data.get(i);
                //被锁状态
                if (item.getIs_lock() == Dictionary.IS_LOCKED_YSE) {

                    //解锁必须先做完的任务项id集合
                    String[] taskItemIds = item.getPre_tasks().split(",");
                    Set<String> taskIdSet = new HashSet<>(Arrays.asList(taskItemIds));

                    //是否满足解锁条件
                    boolean isMeetUnlockCondition = true;
                    //存在一个未做完的前置任务项，则视为不满足解锁条件
                    for (ExamTaskEntity item2 : data) {

                        if (taskIdSet.contains(item2.getTask_id())) {

                            if (item2.getChildTask() == null
                                    || item2.getChildTask().getStatus() != Dictionary.TASK_STATUS_COMPLETED) {
                                //不满足解锁条件
                                isMeetUnlockCondition = false;
                                break;
                            }
                        }
                    }

                    //满足条件直接本地刷新（是否需要重新请求列表？）
                    if (isMeetUnlockCondition) {
                        item.setIs_lock(Dictionary.IS_LOCKED_NO);
                        int realLayoutPosition = i + recyclerAdapter.getHeaderLayoutCount();
                        recyclerAdapter.notifyItemChanged(realLayoutPosition);
                    }
                }
            }

        }

    }


    /**
     * 获取可添加任务列表
     */
    private void loadAddTaskList() {
        DataRequestService.getInstance().getExamCanAddTasks(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamTaskRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamTaskRootEntity.class);

                    List<ExamTaskEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isNotEmpty(dataList)) {
                        //存在自定义任务则显示添加自定义任务按钮
                        fabAddTaskItem.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, httpTag, getActivity());
    }


    /**
     * 添加测评任务成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddExamTaskSuccessNotice(AddExamTaskSuccessEvent event) {
        refreshData();
    }

}
