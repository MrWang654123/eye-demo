package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ChildDto;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features_v2.adapter.ObserveMajorRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.chart.formatter.IntAxisFormatter;
import com.cheersmind.cheersgenie.features_v2.dto.CourseGroupDto;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroup;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroupRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajorRootEntity;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorEvent;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.RecommendMajorActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 专业观察表
 */
public class ObserveMajorFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评id
    private String childExamId;
    private boolean isCompleteSelect;//是否完成了最终选科

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    @BindView(R.id.iv_add)
    ImageView ivAdd;

    //总体就业率
    @BindView(R.id.ll_bar_chart)
    LinearLayout ll_bar_chart;
    @BindView(R.id.barChart)
    BarChart barChart;
    private IntAxisFormatter formatter = new IntAxisFormatter();

    private Typeface mTfLight;
    //轴宽度
    float axisLineWidth = 1.7f;
    //x轴颜色
    String xAxisColor = "#289bd3";
    //y轴颜色
    String yAxisColor = "#3ca5d8";
    //轴文本颜色
    String axisTextColor = "#333333";
    //数据集合1的颜色
    protected String dataSetColor_1 = "#2ec8c9";

    //适配器
    ObserveMajorRecyclerAdapter recyclerAdapter;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            RecommendMajor item = recyclerAdapter.getItem(position);
//            if (item != null) {
//                MajorItem majorItem = new MajorItem();
//                majorItem.setMajor_name(item.getMajor_name());
//                majorItem.setMajor_code(item.getMajor_code());
//                //跳转到详情
//                MajorDetailActivity.startMajorDetailActivity(getContext(), majorItem);
//
//            } else {
//                if (getActivity() != null) {
//                    ToastUtil.showShort(getActivity().getApplication(), R.string.operate_fail);
//                }
//            }
        }
    };

    //recycler子项的孩子视图点击监听
    protected BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            RecommendMajor entity = recyclerAdapter.getItem(position);
            if (entity != null) {
                switch (view.getId()) {
                    //删除
                    case R.id.iv_del: {
                        //删除数据
                        RecommendMajor remove = recyclerAdapter.getData().remove(position);
                        //请求删除
                        doSaveObserveMajor(recyclerAdapter.getData(), position, remove);
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


    //页长度
    private static final int PAGE_SIZE = 100;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ChildDto dto;

    CourseGroupDto courseGroupDto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_observe_major;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
            isCompleteSelect = bundle.getBoolean(DtoKey.IS_COMPLETE_SELECT_COURSE,false);
        }

        //适配器
        recyclerAdapter = new ObserveMajorRecyclerAdapter(getContext(), R.layout.recycleritem_observe_major, null,isCompleteSelect);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_observe_major));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //如果已经选科直接加载观察专业表；如果标志为false可能是真实的没选，也可能是上一个页面延迟造成的
                if (isCompleteSelect) {
                    loadMoreData();
                } else {
                    loadUserSelectCourseGroup();
                }
            }
        });
        //无数据可点击
        emptyLayout.setClickEnableForNoData(true);
        emptyLayout.setOnGotoRelationBtnText("添加");
        //跳转相关监听
        emptyLayout.setOnGotoRelationListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                RecommendMajorActivity.startRmdMajorActivity(getContext(), childExamId);
            }
        });

        dto = new ChildDto(pageNum, PAGE_SIZE);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());

        //设置图表无数据提示
        barChart.setNoDataText("暂无数据");

        courseNameMap.put("0000", "无限制");
        courseNameMap.put("1001", "语文");
        courseNameMap.put("1002", "数学");
        courseNameMap.put("1003", "英语");
        courseNameMap.put("1004", "物理");
        courseNameMap.put("1005", "化学");
        courseNameMap.put("1006", "生物");
        courseNameMap.put("1007", "历史");
        courseNameMap.put("1008", "地理");
        courseNameMap.put("1009", "政治");
        courseNameMap.put("1010", "信息");

        courseGroupDto = new CourseGroupDto(1, 50);
        courseGroupDto.setChild_id(UCManager.getInstance().getDefaultChild().getChildId());
        courseGroupDto.setChild_exam_id(childExamId);

        if (isCompleteSelect) {
            ivAdd.setVisibility(View.GONE);
        }
    }

    //课程编码-累计值
    private HashMap<String, Integer> courseMap = new HashMap<>();
    //课程编码-名称
    private HashMap<String, String> courseNameMap = new HashMap<>();

    /**
     * 初始化图表
     */
    private void initBarChart(List<RecommendMajor> all, boolean clear) {

        if (clear) {
            courseMap.clear();
        }

//        List<RecommendMajor> all = DataSupport.findAll(RecommendMajor.class);
        if (ArrayListUtil.isNotEmpty(all)) {
            for (RecommendMajor major : all) {
                String require_subjects = major.getRequire_subjects();
                //非空
                if (!TextUtils.isEmpty(require_subjects)) {
                    String[] group = require_subjects.split("\\|\\|");
                    if (group.length > 0) {
                        for (String groupItem : group) {
                            String[] items = groupItem.split(",");
                            for (String item : items) {
                                String[] codes = item.split("\\|");
                                for (String code : codes) {
                                    //累加
                                    if (courseMap.containsKey(code)) {
                                        courseMap.put(code, courseMap.get(code) + 1);

                                    } else {
                                        courseMap.put(code, 1);
                                    }
                                }
                            }
                        }
                    }
                } else {//空表示无限制
                    //累加
                    if (courseMap.containsKey("0000")) {
                        courseMap.put("0000", courseMap.get("0000") + 1);

                    } else {
                        courseMap.put("0000", 1);
                    }
                }
            }
        }

        if (courseMap.size() > 0) {
            refreshBarChart(courseMap);

        } else {
            barChart.setData(null);
            barChart.invalidate();
        }
    }

    @Override
    protected void lazyLoad() {
        //如果已经选科直接加载观察专业表；如果标志为false可能是真实的没选，也可能是上一个页面延迟造成的
        if (isCompleteSelect) {
            loadMoreData();
        } else {
            loadUserSelectCourseGroup();
        }
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

    @OnClick({R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //添加观察专业
            case R.id.iv_add: {
                RecommendMajorActivity.startRmdMajorActivity(getContext(), childExamId);
                break;
            }
        }
    }

    /**
     * 添加观察专业成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddObserveMajorSuccessNotice(AddObserveMajorSuccessEvent event) {
        //成功添加的数量大于0
        if (event.getCount() > 0) {
            //刷新数据
            refreshData();
        }
    }

    /**
     * 将要保存观察专业的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddObserveMajorNotice(AddObserveMajorEvent event) {
        //非空
        if (ArrayListUtil.isNotEmpty(event.getSelectMajor())) {

            List<RecommendMajor> saveList = new ArrayList<>(recyclerAdapter.getData());

            for (RecommendMajor major : event.getSelectMajor()) {
                if (!saveList.contains(major)) {
                    saveList.add(major);
                }
            }

            //请求保存观察专业
            doSaveObserveMajor(saveList);
        }
    }


    /**
     * 保存观察专业
     * @param selectMajor 观察专业
     */
    private void doSaveObserveMajor(final List<RecommendMajor> selectMajor) {
        //显示通信等待
//        LoadingView.getInstance().show(getContext(), httpTag);

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        } else {
            //确保显示了刷新动画
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().postSaveObserveMajor(childId, selectMajor, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启上拉加载功能
                recyclerAdapter.setEnableLoadMore(true);
                //结束下拉刷新动画
                swipeRefreshLayout.setRefreshing(false);
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                //清空列表数据
//                recyclerAdapter.setNewData(null);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信等待
//                    LoadingView.getInstance().dismiss();

                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);

//                    if (getActivity() != null) {
//                        getActivity().finish();
//                        ToastUtil.showShort(getActivity().getApplication(), "添加成功");
//                        //发送添加观察专业的事件
//                        EventBus.getDefault().post(new AddObserveMajorSuccessEvent(selectMajor.size()));
//                    }

                    //刷新列表
                    refreshData();
                    //发送添加观察专业成功的通知事件
                    EventBus.getDefault().post(new AddObserveMajorSuccessEvent(selectMajor.size()));

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
//                    recyclerAdapter.setNewData(null);
                }
            }
        }, httpTag, getActivity());
    }


//    /**
//     * 刷新数据
//     */
//    private void freshData() {
//
//        beginPos = 0;
//        dto.setBeginPos(beginPos);
//
//        totalCount = DataSupport.count(RecommendMajor.class);
//
//        List<RecommendMajor> dataList = DataSupport.limit(dto.getSize())
//                .offset(dto.getBeginPos())
//                .find(RecommendMajor.class);
//
//        recyclerAdapter.setNewData(dataList);
//
//        //判断是否全部加载结束
//        if (recyclerAdapter.getData().size() >= totalCount) {
//            //全部加载结束
//            recyclerAdapter.loadMoreEnd(true);
//        } else {
//            //本次加载完成
//            recyclerAdapter.loadMoreComplete();
//        }
//
//        //移动索引
//        beginPos += dto.getSize();
//    }

//    /**
//     * 加载更多数据
//     */
//    private void loadMoreData() {
//
//        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
//        if (recyclerAdapter.getData().size() == 0) {
//            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
//        }
//
//        dto.setBeginPos(beginPos);
//
//        totalCount = DataSupport.count(RecommendMajor.class);
//
//        List<RecommendMajor> dataList = DataSupport.limit(dto.getSize())
//                .offset(dto.getBeginPos())
//                .find(RecommendMajor.class);
//
//        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
//
//        //当前列表无数据
//        if (recyclerAdapter.getData().size() == 0) {
//            recyclerAdapter.setNewData(dataList);
//
//        } else {
//            recyclerAdapter.addData(dataList);
//        }
//
//        //判断是否全部加载结束
//        if (recyclerAdapter.getData().size() >= totalCount) {
//            //全部加载结束
//            recyclerAdapter.loadMoreEnd(true);
//        } else {
//            //本次加载完成
//            recyclerAdapter.loadMoreComplete();
//        }
//
//        //移动索引
//        beginPos += dto.getSize();
//    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        //下拉刷新
        pageNum = 1;
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        dto.setPage(pageNum);
        DataRequestService.getInstance().getSaveObserveMajor(dto, new BaseService.ServiceCallback() {
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
                    RecommendMajorRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, RecommendMajorRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<RecommendMajor> dataList = rootEntity.getItems();

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

                    //重新初始化图表
                    initBarChart(dataList, true);

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
     * 加载数据
     */
    private void loadMoreData() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        dto.setPage(pageNum);
        DataRequestService.getInstance().getSaveObserveMajor(dto, new BaseService.ServiceCallback() {
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
                    RecommendMajorRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, RecommendMajorRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<RecommendMajor> dataList = rootEntity.getItems();

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

                    //重新初始化图表
                    initBarChart(dataList, false);

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
     * 加载用户选科组合
     */
    private void loadUserSelectCourseGroup() {

        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getUserSelectCourseGroup(courseGroupDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启下拉刷新功能
                    swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CourseGroupRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CourseGroupRootEntity.class);
                    List<CourseGroup> lastSelectCourses = rootEntity.getItems();
                    //判断是否已经选科
                    isCompleteSelect = !ArrayListUtil.isEmpty(lastSelectCourses);
                    recyclerAdapter.setCompleteSelect(isCompleteSelect);
                    //加载专业观察表
                    loadMoreData();

                } catch (Exception e) {
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 保存观察专业
     * @param selectMajor 观察专业
     */
    private void doSaveObserveMajor(final List<RecommendMajor> selectMajor, final int delPosition, final RecommendMajor delItem) {
        //显示通信等待
        LoadingView.getInstance().show(getContext(), httpTag);

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().postSaveObserveMajor(childId, selectMajor, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                //还原被删除项
                recyclerAdapter.getData().add(delPosition, delItem);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信等待
                    LoadingView.getInstance().dismiss();
                    //删除视图
                    recyclerAdapter.notifyItemRemoved(delPosition);
                    //刷新图表
                    initBarChart(recyclerAdapter.getData(),true);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getString(R.string.operate_fail)));
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 刷新统计图表
     *
     * @param map 数据集合
     */
    private void refreshBarChart(HashMap<String, Integer> map) {
        barChart.setBackgroundColor(Color.parseColor("#ffffff"));

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        //x轴文本颜色
        xAxis.setTextColor(Color.parseColor(axisTextColor));
        //x轴颜色
        xAxis.setAxisLineColor(Color.parseColor(xAxisColor));
        //x轴宽度
        xAxis.setAxisLineWidth(axisLineWidth);
        xAxis.setDrawGridLines(false);
        //X轴文本旋转角度
//        xAxis.setLabelRotationAngle(-20);
        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
        //右侧还有部分图表未展示出来，此时还需要对X轴进行相应的设置
//        xAxis.setAxisMinimum(0f);
        //放到setData之后
//        xAxis.setAxisMaximum(xValues.size());
//        //将X轴的值显示在中央
//        xAxis.setCenterAxisLabels(true);

        //X轴文本
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
//        xAxis.setValueFormatter(xAxisFormatter);

//        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        //y轴颜色
        leftAxis.setTextColor(Color.parseColor(axisTextColor));
        //y轴颜色
        leftAxis.setAxisLineColor(Color.parseColor(yAxisColor));
        //y轴宽度
        leftAxis.setAxisLineWidth(axisLineWidth);
        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        //最小值
        leftAxis.setAxisMinimum(0);
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //水平偏移
        leftAxis.setXOffset(10);
        //格式化
        leftAxis.setValueFormatter(formatter);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setTextColor(Color.parseColor("#f7f7f7"));
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setAxisLineColor(Color.parseColor("#f7f7f7"));
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
//        l.setYOffset(10f);
        l.setXEntrySpace(4f);
        l.setEnabled(false);

//        setData();

        //X轴文本
        List<String> xLabels = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            xLabels.add(courseNameMap.get(entry.getKey()));
        }
        IndexAxisValueFormatter iAxisValueFormatter = new IndexAxisValueFormatter(xLabels);
        //X轴文本格式化
        barChart.getXAxis().setValueFormatter(iAxisValueFormatter);

        //选中的标记
//        MyMarkerView mv = (MyMarkerView) barChart.getMarker();
//        if (mv == null) {
//            MyMarkerView marker = new MyMarkerView(getContext(), R.layout.custom_marker_view);
//            barChart.setMarker(marker);
//            mv = marker;
//        }
////        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
//        mv.setChartView(barChart); // For bounds control
//        mv.setxLabels(xLabels);
////        mChart.setMarker(mv); // Set the marker to the chart

        //添加数据
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        //比较项
        if (map.size() > 0) {
            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
            int index = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                yValues.add(new BarEntry(index, entry.getValue()));
                index++;
            }

            BarDataSet set1;
            set1 = new BarDataSet(yValues, "统计数据");
            set1.setDrawIcons(false);
            set1.setColor(Color.parseColor(dataSetColor_1));

            dataSets.add(set1);
        }

        ChartData<?> mChartData = new BarData(dataSets);

        mChartData.setValueTextSize(10f);
        mChartData.setValueTypeface(mTfLight);
        ((BarData) mChartData).setBarWidth(0.5f);
        //内容文本格式
        mChartData.setValueFormatter(formatter);


//        xAxis.setAxisMaximum(xLabels.size());

        leftAxis.setAxisMinimum(0);
        int max = calculateSuitableMax(map);
        leftAxis.setAxisMaximum(max);

        barChart.setData((BarData) mChartData);
//        mChart.invalidate();
        barChart.animateY(500);
    }

    /**
     * 获取最大值
     * @param map 科目map
     * @return 最大值
     */
    private int getMaxCountForCourse(HashMap<String, Integer> map) {
        int max = 0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }

        return max;
    }

    /**
     * 计算出适当的最大值
     * @return 最大值
     */
    private int calculateSuitableMax(HashMap<String, Integer> map) {
        int num = getMaxCountForCourse(map);
        int max = num;

        //求出位数
        int count = 0;
        while (true) {
            //如果input不为0，则计数器+1，并且将整数去掉1位数
            if (num != 0) {
                count++;
                num = num / 10;
            } else {//如果计算到input为0，则退出循环
                break;
            }
        }

        int total = 1;
        for (int i=0; i<count; i++) {
            total *= 10;
        }

        if (max < total/2) {
            max = total/2;
        } else {
            max = total;
        }

        return max;
    }

}
