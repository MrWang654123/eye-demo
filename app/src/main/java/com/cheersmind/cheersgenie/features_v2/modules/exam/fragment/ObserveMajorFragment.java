package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.BasePositionDto;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ObserveMajorRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.chart.formatter.IntAxisFormatter;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.RecommendMajorActivity;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
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
import org.litepal.crud.DataSupport;

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

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

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
                        //删除视图
                        recyclerAdapter.remove(position);
                        //删除数据库
                        int count = DataSupport.deleteAll(RecommendMajor.class, "major_code=?", entity.getMajor_code());
                        //刷新图表
                        initBarChart();
                        //索引后退
                        beginPos -= count;
                        if (beginPos < 0) {
                            beginPos = 0;
                        }
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
    private static final int PAGE_SIZE = 20;
    //起始位置
    private int beginPos = 0;
    //后台总记录数
    private int totalCount = 0;

    BasePositionDto dto;


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
        }

        //适配器
        recyclerAdapter = new ObserveMajorRecyclerAdapter(getContext(), R.layout.recycleritem_observe_major, null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
//        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
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
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_observe_major));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        dto = new BasePositionDto(beginPos, PAGE_SIZE);

        //设置图表无数据提示
        barChart.setNoDataText("暂无数据");
    }

    private HashMap<String, Integer> courseMap = new HashMap<>();
    private HashMap<String, String> courseNameMap = new HashMap<>();

    /**
     * 初始化图表
     */
    private void initBarChart() {

        if (courseNameMap.size() == 0) {
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
        }

        courseMap.clear();

        List<RecommendMajor> all = DataSupport.findAll(RecommendMajor.class);
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
        loadMoreData();
        initBarChart();
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

    @OnClick({R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //添加观察专业
            case R.id.tv_add: {
                RecommendMajorActivity.startRmdMajorActivity(getContext(), childExamId);
                break;
            }
        }
    }

    /**
     * 添加观察专业的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddObserveMajorNotice(AddObserveMajorEvent event) {
        //成功添加的数量大于0
        if (event.getCount() > 0) {
            //刷新数据
            freshData();
            //刷新图表
            initBarChart();
        }
    }

    /**
     * 刷新数据
     */
    private void freshData() {

        beginPos = 0;
        dto.setBeginPos(beginPos);

        totalCount = DataSupport.count(RecommendMajor.class);

        List<RecommendMajor> dataList = DataSupport.limit(dto.getSize())
                .offset(dto.getBeginPos())
                .find(RecommendMajor.class);

        recyclerAdapter.setNewData(dataList);

        //判断是否全部加载结束
        if (recyclerAdapter.getData().size() >= totalCount) {
            //全部加载结束
            recyclerAdapter.loadMoreEnd(true);
        } else {
            //本次加载完成
            recyclerAdapter.loadMoreComplete();
        }

        //移动索引
        beginPos += dto.getSize();
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        dto.setBeginPos(beginPos);

        totalCount = DataSupport.count(RecommendMajor.class);

        List<RecommendMajor> dataList = DataSupport.limit(dto.getSize())
                .offset(dto.getBeginPos())
                .find(RecommendMajor.class);

        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

        //当前列表无数据
        if (recyclerAdapter.getData().size() == 0) {
            recyclerAdapter.setNewData(dataList);

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

        //移动索引
        beginPos += dto.getSize();
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
