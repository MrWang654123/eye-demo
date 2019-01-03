package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HistoryExamRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.event.ExamCompleteEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.RefreshTaskListEvent;
import com.cheersmind.cheersgenie.features.event.TopicInExamCompleteEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineExamDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.ExamRootEntity;
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

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 历史测评页面
 */
public class HistoryExamFragment extends LazyLoadFragment {

    protected Unbinder unbinder;

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycleView)
    protected RecyclerView recycleView;

    HistoryExamRecyclerAdapter recyclerAdapter;

    //空布局
    @BindView(R.id.emptyLayout)
    protected XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    protected FloatingActionButton fabGotoTop;

    //recycler子项的点击监听
    protected BaseQuickAdapter.OnItemClickListener recyclerItemClickListener =  new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            try {
                ExamEntity item = (ExamEntity) adapter.getItem(position);
                if (item != null) {
                    //状态
                    int status = item.getStatus();
                    if (status == Dictionary.EXAM_STATUS_OVER) {
                        //跳转历史测评明细页面
                        MineExamDetailActivity.startMineExamDetailActivity(getActivity(), item.getExamId(), status, item.getExamName());

                    } else if (status == Dictionary.EXAM_STATUS_DOING) {
                        //跳转历史测评明细页面
                        MineExamDetailActivity.startMineExamDetailActivity(getActivity(), item.getExamId(), status, item.getExamName());

                    } else if (status == Dictionary.EXAM_STATUS_INACTIVE) {
                        if (getActivity() != null) {
                            ToastUtil.showShort(getActivity().getApplication(), getResources().getString(R.string.exam_inactive_tip));
                        }
                    } else {
                        throw new QSCustomException("未知的测评状态");
                    }
                } else {
                    throw new QSCustomException("列表项数据为空");
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), R.string.operate_fail);
                }
            }
        }
    };


    //下拉刷新的监听
    protected SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //刷新历史测评列表
            refreshHistoryExamList();
        }
    };
    //上拉加载更多的监听
    protected BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多历史测评列表
            loadMoreHistoryExamList();
        }
    };

    //页长度
    protected static final int PAGE_SIZE = 10;
    //页码
    protected int pageNum = 1;
    //后台总记录数
    protected int totalCount = 0;

    //滚动监听
    RecyclerViewScrollListener scrollListener;


    @Override
    protected int setContentView() {
        return R.layout.fragment_history_exam;
    }

    @Override
    protected void lazyLoad() {
        //加载更多历史测评列表
        loadMoreHistoryExamList();
    }

    /**
     * 初始化视图
     */
    @Override
    public void onInitView(View contentView) {
        //绑定ButterKnife
        unbinder = ButterKnife.bind(this, contentView);
        //注册事件
        EventBus.getDefault().register(this);

        //适配器
        recyclerAdapter = new HistoryExamRecyclerAdapter(getContext(), R.layout.recycleritem_history_exam, null);
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
        //去除默认的动画效果
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_f5f5f5_15dp));
        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //滑动监听
        try {
            scrollListener = new RecyclerViewScrollListener(getContext(), fabGotoTop);
            recycleView.addOnScrollListener(scrollListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //限制最大滑动速度
//        int maxFlingVelocity = recycleView.getMaxFlingVelocity();
//        maxFlingVelocity = getResources().getInteger(R.integer.recycler_view_max_velocity);
//        RecyclerViewUtil.setMaxFlingVelocity(recycleView, DensityUtil.dip2px(getContext(), maxFlingVelocity));


        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_history_exam));
        //空布局重载点击监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多历史测评列表
                loadMoreHistoryExamList();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解绑ButterKnife
        unbinder.unbind();

        //注销事件
        EventBus.getDefault().unregister(this);
    }


    /**
     * 测评下某个话题完成的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopicInExamCompleteNotice(TopicInExamCompleteEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            String examId = event.getExamId();
            List<ExamEntity> examList = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(examList) && !TextUtils.isEmpty(examId)) {
                for (int i=0; i<examList.size(); i++) {
                    ExamEntity exam = examList.get(i);
                    if (examId.equals(exam.getExamId())) {
                        //设置完成话题数
                        exam.setCompleteTopics(exam.getCompleteTopics() + 1);
                        try {
                            int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                            recyclerAdapter.notifyItemChanged(tempPosition);//局部刷新列表项，把header计算在内
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 测评完成的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExamCompleteNotice(ExamCompleteEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            String examId = event.getExamId();
            List<ExamEntity> examList = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(examList) && !TextUtils.isEmpty(examId)) {
                for (int i=0; i<examList.size(); i++) {
                    ExamEntity exam = examList.get(i);
                    if (examId.equals(exam.getExamId())) {
                        //设置完成量表数
                        exam.setCompleteDimensions(exam.getTotalDimensions());
                        //设置完成话题数
                        exam.setCompleteTopics(exam.getTotalTopics());
                        //设置为完成状态
                        exam.setChildExamStatus(Dictionary.CHILD_EXAM_STATUS_COMPLETE);
                        try {
                            int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                            recyclerAdapter.notifyItemChanged(tempPosition);//局部刷新列表项，把header计算在内
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //发送刷新任务列表的通知事件
                        EventBus.getDefault().post(new RefreshTaskListEvent());
                        break;
                    }
                }
            }
        }

    }

    /**
     * 重置为第一页
     */
    protected void resetPageInfo() {
        //页码
        pageNum = 1;
        //总数量
        totalCount = 0;
    }


    /**
     * 刷新历史测评列表
     */
    protected void refreshHistoryExamList() {
        //重置为第一页
        resetPageInfo();
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildHistoryExamList(defaultChildId,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
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
                    ExamRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<ExamEntity> dataList = articleRootEntity.getItems();

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
        },httpTag, getActivity());
    }


    /**
     * 加载更多历史测评列表
     */
    protected void loadMoreHistoryExamList() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildHistoryExamList(defaultChildId,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
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

//                    Map dataMap = JsonUtil.fromJson(testExamListStr, Map.class);
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ExamRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ExamRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<ExamEntity> dataList = articleRootEntity.getItems();

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


    //测试数据：测评列表
    String testExamListStr = "{\n" +
            "\t\"total\": 1,\n" +
            "\t\"items\": [{\n" +
            "\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\"status\": 0\n" +
            "\t},{\n" +
            "\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\"status\": 0\n" +
            "\t},{\n" +
            "\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\"status\": 1\n" +
            "\t},{\n" +
            "\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\"status\": 1\n" +
            "\t},{\n" +
            "\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\"status\": 2\n" +
            "\t}]\n" +
            "}";

}

