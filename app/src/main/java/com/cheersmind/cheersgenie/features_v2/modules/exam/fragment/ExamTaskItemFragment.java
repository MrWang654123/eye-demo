package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionBaseRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.entity.ExamEntity;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamTaskItemRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.TaskItemDto;
import com.cheersmind.cheersgenie.features_v2.dto.TopicDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemRootEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
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
 * 测评任务内容
 */
public class ExamTaskItemFragment extends LazyLoadFragment {

    //孩子任务ID
    private String childTaskId;
    //任务状态
    private int taskStatus;
    //任务描述
    private String taskDesc;

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
//            if (position == 0) {
//                //确认选课页面
//                ChooseCourseActivity.startChooseCourseActivity(getContext(), "1111");
//
//            } else {
//
//            }

            ExamTaskItemEntity entity = (ExamTaskItemEntity) recyclerAdapter.getData().get(position);
            //测评
            if (entity.getItem_type() == 1) {
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

            } else {
                //跳转到具体类型的详情页
                String articleId = entity.getItem_id();
                String ivMainUrl = "";
                String articleTitle = entity.getItem_name();
                ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);
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
            taskDesc = bundle.getString(DtoKey.TASK_DESC);
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
     * 加载更多数据
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

                    for (DimensionInfoEntity dimension : dataList) {
                        dimension.setItemType(ExamTaskItemEntity.TYPE_DIMENSION);
                        entity.addSubItem(dimension);
                    }

                    recyclerAdapter.expand(position);

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

}

