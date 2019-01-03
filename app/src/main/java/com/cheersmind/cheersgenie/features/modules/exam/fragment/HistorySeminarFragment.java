package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HistorySeminarRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.event.ExamCompleteEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineExamDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.SeminarEntity;
import com.cheersmind.cheersgenie.main.entity.SeminarRootEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 历史专题页面
 */
public class HistorySeminarFragment extends LazyLoadFragment {

    protected Unbinder unbinder;

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycleView)
    protected RecyclerView recycleView;

    //历史专题适配
    HistorySeminarRecyclerAdapter recyclerAdapter;
    //专题集合
    List<SeminarEntity> seminarList;

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
            //刷新历史专题列表
            refreshHistorySeminarList();
        }
    };
    //上拉加载更多的监听
    protected BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多历史专题列表
            loadMoreHistorySeminarList();
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
        return R.layout.fragment_history_seminar;
    }

    @Override
    protected void lazyLoad() {
        //加载更多历史专题列表
        loadMoreHistorySeminarList();
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
        recyclerAdapter = new HistorySeminarRecyclerAdapter(null);
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
        //去除默认的动画效果
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
//        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_f5f5f5_15dp));
//        recycleView.addItemDecoration(divider);
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
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_history_seminar));
        //空布局重载点击监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多历史专题列表
                loadMoreHistorySeminarList();
            }
        });
        //初始显示正在加载中
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

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
     * 测评完成的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExamCompleteNotice(ExamCompleteEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            String examId = event.getExamId();
            List<MultiItemEntity> data = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(data) && !TextUtils.isEmpty(examId)) {
                for (int i=0; i<data.size(); i++) {
                    MultiItemEntity multiItem = data.get(i);
                    if (multiItem instanceof ExamEntity) {
                        ExamEntity exam = (ExamEntity) multiItem;
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
                            break;
                        }
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
        //清空集合
        if (ArrayListUtil.isNotEmpty(seminarList)) {
            seminarList.clear();
        }
    }


    /**
     * 刷新历史专题列表
     */
    protected void refreshHistorySeminarList() {
        //重置为第一页
        resetPageInfo();
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildHistorySeminarList(defaultChildId,
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
                    SeminarRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, SeminarRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<SeminarEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //转成列表的数据项
                    List recyclerItem = topicInfoEntityToRecyclerMulti(dataList);
                    //重置列表适配器的数据
                    recyclerAdapter.setNewData(recyclerItem);
                    //初始展开
                    recyclerAdapter.expandAll();
                    seminarList = dataList;

                    //判断是否全部加载结束
                    if (seminarList.size() >= totalCount) {
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
     * 加载更多历史专题列表
     */
    protected void loadMoreHistorySeminarList() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (ArrayListUtil.isEmpty(seminarList)) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildHistorySeminarList(defaultChildId,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                if (ArrayListUtil.isEmpty(seminarList)) {
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

//                    Map dataMap = JsonUtil.fromJson(testSeminarListStr, Map.class);
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    SeminarRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, SeminarRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<SeminarEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //转成列表的数据项
                    List recyclerItem = topicInfoEntityToRecyclerMulti(dataList);

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(recyclerItem);

                    } else {
                        recyclerAdapter.addData(recyclerItem);
                    }

                    //初始展开
                    int totalItem = getTotalItem(dataList);
                    recyclerAdapter.expandAll(totalItem);

                    if (seminarList == null) {
                        seminarList = new ArrayList<>();
                    }
                    seminarList.addAll(dataList);

                    //判断是否全部加载结束
                    if (seminarList.size() >= totalCount) {
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
                    if (ArrayListUtil.isEmpty(seminarList)) {
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
     * 专题集合转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
     *
     * @param seminarList 专题集合
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> topicInfoEntityToRecyclerMulti(List<SeminarEntity> seminarList) {
        List<MultiItemEntity> resList = null;


        if (ArrayListUtil.isNotEmpty(seminarList)) {
            boolean isFirstInExam = true;
            resList = new ArrayList<>();
            //专题
            for (SeminarEntity seminar : seminarList) {
//                        if (isFirstInExam) {
//                            topicInfo.setFirstInExam(true);
//                            isFirstInExam = false;
//                        }
                //已经添加过了就清空
                if (seminar.hasSubItem()) {
                    seminar.getSubItems().clear();
                }
                //默认设置为不展开
                seminar.setExpanded(false);

                List<ExamEntity> exams = seminar.getExams();
                if (ArrayListUtil.isNotEmpty(exams)) {
                    //添加适配器的1级模型
                    resList.add(seminar);
                    //遍历维度列表（量表）
                    for (int i=0; i<exams.size(); i++) {
                        ExamEntity exam = exams.get(i);
                        //设置布局type
                        exam.setItemType(HistorySeminarRecyclerAdapter.LAYOUT_TYPE_ITEM);
                        //标记第一个量表
                        if (i == 0) {
                            exam.setFirstInSeminar(true);
                        }
                        //标记是最后一个量表
                        if (i == exams.size() - 1) {
                            exam.setLastInSeminar(true);
                        }
                        //添加适配器的2级模型
                        seminar.addSubItem(exam);
                    }
                    //模拟footer
//                            DimensionInfoEntity footerDimensionInfo = new DimensionInfoEntity();
//                            footerDimensionInfo.setItemType(ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_SIMULATE_FOOTER);
//                            topicInfo.addSubItem(footerDimensionInfo);
                }
            }
        }

        return resList;
    }


    /**
     * 获取共总有多少项（专题、测评）
     *
     * @param seminarList 专题集合
     * @return 适配recycler的分组数据模型
     */
    protected int getTotalItem(List<SeminarEntity> seminarList) {
        int total = 0;

        if (ArrayListUtil.isNotEmpty(seminarList)) {
            //专题数量
            total += seminarList.size();
            for (SeminarEntity seminar : seminarList) {
                List<ExamEntity> examList = seminar.getExams();
                if (ArrayListUtil.isNotEmpty(examList)) {
                    //测评数量
                    total += examList.size();
                }
            }
        }

        return total;
    }


    //测试数据：专题列表
    String testSeminarListStr = "{\n" +
            "\t\"total\": 1,\n" +
            "\t\"items\": [{\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"description\": \"专题描述专题描述\",\n" +
            "\t\t\"start_time\": \"2018-09-25T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2019-06-02T11:24:07.000+0800\",\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中1\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 0,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中2\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 1,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中3\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 2,\n" +
            "\t\t\t\"child_exam_status\": 1\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题222\",\n" +
            "\t\t\"description\": \"专题描述专题描述\",\n" +
            "\t\t\"start_time\": \"2018-09-25T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2019-06-02T11:24:07.000+0800\",\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中2\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 1,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中3\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 2,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题3333\",\n" +
            "\t\t\"description\": \"专题描述专题描述\",\n" +
            "\t\t\"start_time\": \"2018-09-25T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2019-06-02T11:24:07.000+0800\",\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中2\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 1,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中3\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 2,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";

}

