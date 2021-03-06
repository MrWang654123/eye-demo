package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamTaskRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.AddExamTaskDto;
import com.cheersmind.cheersgenie.features_v2.dto.ExamTaskDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskRootEntity;
import com.cheersmind.cheersgenie.features_v2.event.AddExamTaskSuccessEvent;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 测评任务添加
 */
public class ExamTaskAddFragment extends LazyLoadFragment {

    //孩子模块ID
    private String childModuleId;

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
//            addExamTaskDto.getItems().add(entity);
//            doConfirm();

            //设置选中标志
            entity.setSelected(!entity.isSelected());
            //刷新视图
            ImageView ivSelect = view.findViewById(R.id.iv_select);
            if (entity.isSelected()) {
                ivSelect.setImageResource(R.drawable.check_box_outline);
                //处理选中集合
                if (!addExamTaskDto.getItems().containsKey(entity.getTask_id())) {
                    addExamTaskDto.getItems().put(entity.getTask_id(), entity);
                }
            } else {
                ivSelect.setImageResource(R.drawable.check_box_outline_bl);
                //处理选中集合
                if (addExamTaskDto.getItems().containsKey(entity.getTask_id())) {
                    addExamTaskDto.getItems().remove(entity.getTask_id());
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

    //是否做全选操作：true做全选；false做全不选
    private boolean isDoSelectAll = true;

    ExamTaskDto dto;
    AddExamTaskDto addExamTaskDto;


    @Override
    protected int setContentView() {
        return R.layout.fragment_exam_task_add;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new ExamTaskRecyclerAdapter(getContext(), R.layout.recycleritem_exam_task, null, true);
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
        recyclerAdapter.addHeaderView(new View(getContext()));
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
        recycleView.addItemDecoration(divider);
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
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_add_task));
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
            childModuleId = bundle.getString(DtoKey.CHILD_MODULE_ID);
        }

        dto = new ExamTaskDto(pageNum, PAGE_SIZE);
        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        dto.setChildId(childId);
        dto.setChildModuleId(childModuleId);

        addExamTaskDto = new AddExamTaskDto();
        addExamTaskDto.setChildId(childId);
        addExamTaskDto.setChildModuleId(childModuleId);
        addExamTaskDto.setItems(new ArrayMap<String, ExamTaskEntity>());
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


    /**
     * 刷新数据
     */
    private void refreshData() {
        //下拉刷新
        pageNum = 1;
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        dto.setPage(pageNum);
        DataRequestService.getInstance().getExamCanAddTasks(dto, new BaseService.ServiceCallback() {
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
        DataRequestService.getInstance().getExamCanAddTasks(dto, new BaseService.ServiceCallback() {
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


    @OnClick({R.id.btn_confirm})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //确定
            case R.id.btn_confirm: {
                //非空
                if (addExamTaskDto.getItems().size() > 0) {
                    doConfirm();
                } else {
                    if (getActivity() != null) {
                        ToastUtil.showShort(getActivity().getApplication(), "请选择任务");
                    }
                }
                break;
            }
        }
    }

    public static final int RESULT_CODE_ADD_TASK = 81;

//    /**
//     * 确定操作
//     */
//    private void doConfirm() {
//        List<ExamTaskEntity> selectList = new ArrayList<>();
//        List<ExamTaskEntity> data = recyclerAdapter.getData();
//        for (ExamTaskEntity taskEntity : data) {
//            if (taskEntity.isSelected()) {
//                selectList.add(taskEntity);
//            }
//        }
//
//        if (ArrayListUtil.isEmpty(selectList)) {
//            if (getActivity() != null) {
//                ToastUtil.showShort(getActivity().getApplication(), "全选择任务");
//            }
//        } else {
//            if (getActivity() != null) {
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(DtoKey.ADD_EXAM_TASK, selectList);
//                intent.putExtras(bundle);
//                getActivity().setResult(RESULT_CODE_ADD_TASK);
//            }
//        }
//
//    }

    /**
     * 确定操作
     */
    private void doConfirm(ExamTaskEntity selectExamTask) {
        if (getActivity() != null) {
            Intent intent = new Intent();
            intent.putExtra(DtoKey.ADD_EXAM_TASK, selectExamTask);
            getActivity().setResult(RESULT_CODE_ADD_TASK, intent);
            getActivity().finish();
        }
    }

    /**
     * 确定操作
     */
    private void doConfirm() {
        LoadingView.getInstance().show(getContext(), httpTag);

        DataRequestService.getInstance().postAddExamTasks(addExamTaskDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    LoadingView.getInstance().dismiss();

                    showToast("添加成功");

                    //发送测评任务添加成功的通知事件
                    EventBus.getDefault().post(new AddExamTaskSuccessEvent(addExamTaskDto.getItems()));

                    //跳转到主页面
                    Intent intent = new Intent(getContext(), MasterTabActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);//FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 全选操作
     * @param isDoSelect true：全选；false全不选
     */
    private void doSelectAll(boolean isDoSelect) {
        List<ExamTaskEntity> data = recyclerAdapter.getData();
        for (ExamTaskEntity taskEntity : data) {
            if (isDoSelect) {
                taskEntity.setSelected(true);
            } else {
                taskEntity.setSelected(false);
            }
        }

        recyclerAdapter.notifyDataSetChanged();
    }

}

