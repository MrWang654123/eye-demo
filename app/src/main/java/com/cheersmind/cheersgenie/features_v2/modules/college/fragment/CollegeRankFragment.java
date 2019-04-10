package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankCategoryRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankLevelRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankProvinceRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankSubjectRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.CollegeRankDto;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEduLevel;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeProvince;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRank;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankArea;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankCategory;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankItem;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankLevel;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankSubject;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRootEntity;
import com.cheersmind.cheersgenie.features_v2.interfaces.BackPressedHandler;
import com.cheersmind.cheersgenie.features_v2.modules.college.activity.CollegeDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import org.litepal.crud.DataSupport;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 院校排名
 */
public class CollegeRankFragment extends LazyLoadFragment implements BackPressedHandler {

    Unbinder unbinder;

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

    //适配器的数据列表
    CollegeRankRecyclerAdapter recyclerAdapter;

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
            CollegeEntity college = recyclerAdapter.getData().get(position);
            CollegeDetailActivity.startCollegeDetailActivity(getContext(), college);
        }
    };

    //页长度
    private static final int PAGE_SIZE = 20;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;
    //院校排名dto
    CollegeRankDto dto;

    //排名触发器：多选框
    @BindView(R.id.cb_rank_area)
    CheckBox cb_rank_area;
    @BindView(R.id.cb_rank_level)
    CheckBox cb_rank_level;
    @BindView(R.id.cb_rank_subject)
    CheckBox cb_rank_subject;
    //排名布局
    @BindView(R.id.fl_rank)
    FrameLayout flRank;
    //排名省份布局
    @BindView(R.id.ll_rank_province)
    LinearLayout llRankProvince;
    //排名学历层次布局
    @BindView(R.id.ll_rank_level)
    LinearLayout llRankLevel;
    //排名类别布局
//    @BindView(R.id.ll_rank_category)
//    LinearLayout llRankCategory;
    //排名主题布局
    @BindView(R.id.ll_rank_subject)
    LinearLayout llRankSubject;
    //排名省份
    @BindView(R.id.rv_rank_province)
    RecyclerView rvRankProvince;
    //排名学历层次
    @BindView(R.id.rv_rank_level)
    RecyclerView rvRankLevel;
    //排名院校类别
//    @BindView(R.id.rv_rank_category)
//    RecyclerView rvRankCategory;
    //排名主题
    @BindView(R.id.rv_rank_subject)
    RecyclerView rvRankSubject;

    CollegeRankProvinceRecyclerAdapter provinceRecyclerAdapter;
    CollegeRankLevelRecyclerAdapter levelRecyclerAdapter;
//    CollegeRankCategoryRecyclerAdapter categoryRecyclerAdapter;
    CollegeRankSubjectRecyclerAdapter subjectRecyclerAdapter;

    //大学排名条件
//    CollegeRank collegeRank;

    //排名省份的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener provinceRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            CollegeProvince collegeProvince = provinceRecyclerAdapter.getData().get(position);
            //点击是不同项
            if (!dto.getProvince().getName().equals(collegeProvince.getName())) {
                dto.setProvince(collegeProvince);
                //刷新触发布局文本
                refreshRankToggleText(dto);
                //刷新列表
                provinceRecyclerAdapter.notifyDataSetChanged();
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    //排名学历层次的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener levelRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            CollegeEduLevel eduLevel = levelRecyclerAdapter.getData().get(position);
            //点击是不同项
            if (!dto.getEduLevel().getName().equals(eduLevel.getName())) {
                //改变排名主题数据
                subjectRecyclerAdapter.setNewData(eduLevel.getRanking_items());
                dto.setRankItem(eduLevel.getRanking_items().get(0));
                dto.setEduLevel(eduLevel);
                //刷新触发布局文本
                refreshRankToggleText(dto);
                //刷新列表
                levelRecyclerAdapter.notifyDataSetChanged();
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    //排名主题的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener subjectRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            CollegeRankItem rankItem = subjectRecyclerAdapter.getData().get(position);
            if (!dto.getRankItem().getName().equals(rankItem.getName())) {
                dto.setRankItem(rankItem);
                //刷新触发布局文本
                refreshRankToggleText(dto);
                //刷新列表
                subjectRecyclerAdapter.notifyDataSetChanged();
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_college_rank;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new CollegeRankRecyclerAdapter(getContext(), R.layout.recycleritem_college_rank, null);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
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
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_rank));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        dto = new CollegeRankDto(pageNum, PAGE_SIZE);
        //设置到适配器中，用于显示排名值
        recyclerAdapter.setDto(dto);
        //初始化排名数据
        initRankData();
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
     * 初始化排名数据
     */
    private void initRankData() {
        //初始隐藏排名布局
        flRank.setVisibility(View.GONE);
        //初始化筛选条件选中数据
        List<CollegeProvince> provinces = DataSupport.findAll(CollegeProvince.class);
        provinces.add(0, new CollegeProvince("", "全国"));
        List<CollegeEduLevel> levels = DataSupport.findAll(CollegeEduLevel.class, true);
        dto.setProvince(provinces.get(0));
        dto.setEduLevel(levels.get(0));
        dto.setRankItem(levels.get(0).getRanking_items().get(0));

        //省份列表
        provinceRecyclerAdapter = new CollegeRankProvinceRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_province, provinces, dto);
        provinceRecyclerAdapter.setOnItemClickListener(provinceRecyclerItemClickListener);
        rvRankProvince.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankProvince.setAdapter(provinceRecyclerAdapter);

        //学历层次
        levelRecyclerAdapter = new CollegeRankLevelRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_common, levels, dto);
        levelRecyclerAdapter.setOnItemClickListener(levelRecyclerItemClickListener);
        rvRankLevel.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankLevel.setAdapter(levelRecyclerAdapter);

        //主题列表
        List<CollegeRankItem> ranking_items = levels.get(0).getRanking_items();
        subjectRecyclerAdapter = new CollegeRankSubjectRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_common, ranking_items, dto);
        subjectRecyclerAdapter.setOnItemClickListener(subjectRecyclerItemClickListener);
        rvRankSubject.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankSubject.setAdapter(subjectRecyclerAdapter);

        //初始化排名布局触发器
        cb_rank_area.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_rank_level.setChecked(false);
                    cb_rank_subject.setChecked(false);

                    if (flRank.getVisibility() != View.VISIBLE) {
                        showRankLayout();
                    }
                    llRankProvince.setVisibility(View.VISIBLE);
                    llRankLevel.setVisibility(View.GONE);
                    llRankSubject.setVisibility(View.GONE);
                } else {
                    if (isAllNoCheck()) {
                        hideRankLayout();
                    }
                }
            }
        });
        cb_rank_level.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_rank_area.setChecked(false);
                    cb_rank_subject.setChecked(false);

                    if (flRank.getVisibility() != View.VISIBLE) {
                        showRankLayout();
                    }
                    llRankProvince.setVisibility(View.GONE);
                    llRankLevel.setVisibility(View.VISIBLE);
                    llRankSubject.setVisibility(View.GONE);

                } else {
                    if (isAllNoCheck()) {
                        hideRankLayout();
                    }
                }
            }
        });
        cb_rank_subject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_rank_area.setChecked(false);
                    cb_rank_level.setChecked(false);

                    if (flRank.getVisibility() != View.VISIBLE) {
                        showRankLayout();
                    }
                    llRankProvince.setVisibility(View.GONE);
                    llRankLevel.setVisibility(View.GONE);
                    llRankSubject.setVisibility(View.VISIBLE);

                } else {
                    if (isAllNoCheck()) {
                        hideRankLayout();
                    }
                }
            }
        });

        //刷新排名触发布局的文本
        refreshRankToggleText(dto);
    }

    /**
     * 点击事件
     * @param view 被点击的视图
     */
    @OnClick({R.id.fl_rank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //排名布局
            case R.id.fl_rank: {
                hideRankLayout();
                break;
            }
        }
    }

    /**
     * 显示排名布局
     */
    private void showRankLayout() {
        flRank.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏排名布局
     */
    private void hideRankLayout() {
        flRank.setVisibility(View.GONE);
        cb_rank_area.setChecked(false);
        cb_rank_level.setChecked(false);
        cb_rank_subject.setChecked(false);
    }

    /**
     * 是否都没有选中
     */
    private boolean isAllNoCheck() {
        return !cb_rank_area.isChecked() && !cb_rank_level.isChecked() && !cb_rank_subject.isChecked();
    }

    /**
     * 刷新排名触发布局的文本
     * @param collegeRankDto 排名条件
     */
    private void refreshRankToggleText(CollegeRankDto collegeRankDto) {
        if (collegeRankDto == null) return;

        //区域、省份
        cb_rank_area.setText(collegeRankDto.getProvince().getName());

        //学历层次
        cb_rank_level.setText(collegeRankDto.getEduLevel().getName());

        //排名主题
        cb_rank_subject.setText(collegeRankDto.getRankItem().getName());
    }

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
        DataRequestService.getInstance().getCollegeRankList(dto, new BaseService.ServiceCallback() {
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
                    CollegeRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<CollegeEntity> dataList = rootEntity.getItems();

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
        DataRequestService.getInstance().getCollegeRankList(dto, new BaseService.ServiceCallback() {
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
                    CollegeRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<CollegeEntity> dataList = rootEntity.getItems();
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
     * 是否处理了回退键
     * @return true：处理了
     */
    @Override
    public boolean hasHandlerBackPressed() {
        if (flRank.getVisibility() == View.VISIBLE) {
            hideRankLayout();
            return true;
        }
        return false;
    }

}

