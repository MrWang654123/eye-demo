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
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankAreaRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankCategoryRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankProvinceRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankSubjectRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRank;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankArea;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankCategory;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankSubject;
import com.cheersmind.cheersgenie.features_v2.interfaces.BackPressedHandler;
import com.cheersmind.cheersgenie.features_v2.modules.college.activity.CollegeDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

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
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //排名触发器：多选框
    @BindView(R.id.cb_rank_area)
    CheckBox cb_rank_area;
    @BindView(R.id.cb_rank_category)
    CheckBox cb_rank_category;
    @BindView(R.id.cb_rank_subject)
    CheckBox cb_rank_subject;
    //排名布局
    @BindView(R.id.fl_rank)
    FrameLayout flRank;
    //排名区域布局
    @BindView(R.id.ll_rank_area)
    LinearLayout llRankArea;
    //排名类别布局
    @BindView(R.id.ll_rank_category)
    LinearLayout llRankCategory;
    //排名主题布局
    @BindView(R.id.ll_rank_subject)
    LinearLayout llRankSubject;
    //排名区域
    @BindView(R.id.rv_rank_area)
    RecyclerView rvRankArea;
    //排名省份
    @BindView(R.id.rv_rank_province)
    RecyclerView rvRankProvince;
    //排名院校类别
    @BindView(R.id.rv_rank_category)
    RecyclerView rvRankCategory;
    //排名主题
    @BindView(R.id.rv_rank_subject)
    RecyclerView rvRankSubject;

    CollegeRankAreaRecyclerAdapter areaRecyclerAdapter;
    CollegeRankProvinceRecyclerAdapter provinceRecyclerAdapter;
    CollegeRankCategoryRecyclerAdapter categoryRecyclerAdapter;
    CollegeRankSubjectRecyclerAdapter subjectRecyclerAdapter;

    //大学排名条件
    CollegeRank collegeRank;

    //排名省份的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener provinceRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            String province = provinceRecyclerAdapter.getData().get(position);
            //点击是不同项
            if (!collegeRank.getCollegeRankArea().getProvince().equals(province)) {
                collegeRank.getCollegeRankArea().setProvince(province);
                //刷新触发布局文本
                refreshRankToggleText(collegeRank);
                //刷新列表
                provinceRecyclerAdapter.notifyDataSetChanged();
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    //排名院校类别的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener categoryRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            String category = categoryRecyclerAdapter.getData().get(position);
            if (!collegeRank.getCollegeRankCategory().getCategoryName().equals(category)) {
                collegeRank.getCollegeRankCategory().setCategoryName(category);
                //刷新触发布局文本
                refreshRankToggleText(collegeRank);
                //刷新列表
                categoryRecyclerAdapter.notifyDataSetChanged();
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
            String subject = subjectRecyclerAdapter.getData().get(position);
            if (!collegeRank.getCollegeRankSubject().getSubjectName().equals(subject)) {
                collegeRank.getCollegeRankSubject().setSubjectName(subject);
                //刷新触发布局文本
                refreshRankToggleText(collegeRank);
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
        //初始化排名选中数据
        collegeRank = new CollegeRank();
        CollegeRankArea collegeRankArea = new CollegeRankArea();
        collegeRankArea.setAreaName("中国大学");
        collegeRankArea.setProvince("全部");
        CollegeRankCategory collegeRankCategory = new CollegeRankCategory();
        collegeRankCategory.setCategoryName("全部");
        CollegeRankSubject collegeRankSubject = new CollegeRankSubject();
        collegeRankSubject.setSubjectName("全部");
        collegeRank.setCollegeRankArea(collegeRankArea);
        collegeRank.setCollegeRankCategory(collegeRankCategory);
        collegeRank.setCollegeRankSubject(collegeRankSubject);

        //区域列表
        String[] rankAreas = getResources().getStringArray(R.array.college_rank_area);
        areaRecyclerAdapter = new CollegeRankAreaRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_common, Arrays.asList(rankAreas), collegeRank);
        rvRankArea.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankArea.setAdapter(areaRecyclerAdapter);

        //省份列表
        String[] rankProvinces = getResources().getStringArray(R.array.college_rank_province);
        provinceRecyclerAdapter = new CollegeRankProvinceRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_province, Arrays.asList(rankProvinces), collegeRank);
        provinceRecyclerAdapter.setOnItemClickListener(provinceRecyclerItemClickListener);
        rvRankProvince.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankProvince.setAdapter(provinceRecyclerAdapter);

        //院校类别列表
        String[] rankCategory = getResources().getStringArray(R.array.college_rank_category);
        categoryRecyclerAdapter = new CollegeRankCategoryRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_common, Arrays.asList(rankCategory), collegeRank);
        categoryRecyclerAdapter.setOnItemClickListener(categoryRecyclerItemClickListener);
        rvRankCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankCategory.setAdapter(categoryRecyclerAdapter);

        //主题列表
        String[] rankSubject = getResources().getStringArray(R.array.college_rank_subject);
        subjectRecyclerAdapter = new CollegeRankSubjectRecyclerAdapter(
                getContext(), R.layout.recycleritem_college_rank_common, Arrays.asList(rankSubject), collegeRank);
        subjectRecyclerAdapter.setOnItemClickListener(subjectRecyclerItemClickListener);
        rvRankSubject.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankSubject.setAdapter(subjectRecyclerAdapter);

        //初始化排名布局触发器
        cb_rank_area.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_rank_category.setChecked(false);
                    cb_rank_subject.setChecked(false);

                    if (flRank.getVisibility() != View.VISIBLE) {
                        showRankLayout();
                    }
                    llRankArea.setVisibility(View.VISIBLE);
                    llRankCategory.setVisibility(View.GONE);
                    llRankSubject.setVisibility(View.GONE);
                } else {
                    if (isAllNoCheck()) {
                        hideRankLayout();
                    }
                }
            }
        });
        cb_rank_category.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_rank_area.setChecked(false);
                    cb_rank_subject.setChecked(false);

                    if (flRank.getVisibility() != View.VISIBLE) {
                        showRankLayout();
                    }
                    llRankArea.setVisibility(View.GONE);
                    llRankCategory.setVisibility(View.VISIBLE);
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
                    cb_rank_category.setChecked(false);

                    if (flRank.getVisibility() != View.VISIBLE) {
                        showRankLayout();
                    }
                    llRankArea.setVisibility(View.GONE);
                    llRankCategory.setVisibility(View.GONE);
                    llRankSubject.setVisibility(View.VISIBLE);
                } else {
                    if (isAllNoCheck()) {
                        hideRankLayout();
                    }
                }
            }
        });

        //刷新排名触发布局的文本
        refreshRankToggleText(collegeRank);
    }

    /**
     * 点击事件
     * @param view 被点击的视图
     */
    @OnClick({R.id.fl_rank, R.id.ll_rank_area_toggle, R.id.ll_rank_category_toggle, R.id.ll_rank_subject_toggle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //排名布局
            case R.id.fl_rank: {
                hideRankLayout();
                break;
            }
//            //排名区域触发布局
//            case R.id.ll_rank_area_toggle: {
//                if (flRank.getVisibility() == View.VISIBLE) {
//                    //排名布局是显示状态，如果当前排名数据布局是同一个则隐藏排名布局，否则直接替换
//                    if (llRankArea.getVisibility() == View.VISIBLE) {
//                        hideRankLayout();
//                    } else {
//                        llRankArea.setVisibility(View.VISIBLE);
//                        llRankCategory.setVisibility(View.GONE);
//                        llRankSubject.setVisibility(View.GONE);
//                    }
//                } else {
//                    showRankLayout();
//                    llRankArea.setVisibility(View.VISIBLE);
//                    llRankCategory.setVisibility(View.GONE);
//                    llRankSubject.setVisibility(View.GONE);
//                }
//                break;
//            }
//            //排名院校类别触发布局
//            case R.id.ll_rank_category_toggle: {
//                if (flRank.getVisibility() == View.VISIBLE) {
//                    //排名布局是显示状态，如果当前排名数据布局是同一个则隐藏排名布局，否则直接替换
//                    if (llRankCategory.getVisibility() == View.VISIBLE) {
//                        hideRankLayout();
//                    } else {
//                        llRankArea.setVisibility(View.GONE);
//                        llRankCategory.setVisibility(View.VISIBLE);
//                        llRankSubject.setVisibility(View.GONE);
//                    }
//                } else {
//                    showRankLayout();
//                    llRankArea.setVisibility(View.GONE);
//                    llRankCategory.setVisibility(View.VISIBLE);
//                    llRankSubject.setVisibility(View.GONE);
//                }
//                break;
//            }
//            //排名主题触发布局
//            case R.id.ll_rank_subject_toggle: {
//                if (flRank.getVisibility() == View.VISIBLE) {
//                    //排名布局是显示状态，如果当前排名数据布局是同一个则隐藏排名布局，否则直接替换
//                    if (llRankSubject.getVisibility() == View.VISIBLE) {
//                        hideRankLayout();
//                    } else {
//                        llRankArea.setVisibility(View.GONE);
//                        llRankCategory.setVisibility(View.GONE);
//                        llRankSubject.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    showRankLayout();
//                    llRankArea.setVisibility(View.GONE);
//                    llRankCategory.setVisibility(View.GONE);
//                    llRankSubject.setVisibility(View.VISIBLE);
//                }
//                break;
//            }
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
        cb_rank_category.setChecked(false);
        cb_rank_subject.setChecked(false);
    }

    /**
     * 是否都没有选中
     */
    private boolean isAllNoCheck() {
        return !cb_rank_area.isChecked() && !cb_rank_category.isChecked() && !cb_rank_subject.isChecked();
    }

    /**
     * 刷新排名触发布局的文本
     * @param collegeRank 排名数据
     */
    private void refreshRankToggleText(CollegeRank collegeRank) {
        if (collegeRank == null) return;

        //区域、省份
        if (collegeRank.getCollegeRankArea().getProvince().equals("全部")) {
            cb_rank_area.setText("全国");
        } else {
            cb_rank_area.setText(collegeRank.getCollegeRankArea().getProvince());
        }

        //院校类别
        if (collegeRank.getCollegeRankCategory().getCategoryName().equals("全部")) {
            cb_rank_category.setText("院校类别");
        } else {
            cb_rank_category.setText(collegeRank.getCollegeRankCategory().getCategoryName());
        }

        //主题
        if (collegeRank.getCollegeRankSubject().getSubjectName().equals("全部")) {
            cb_rank_subject.setText("其他");
        } else {
            cb_rank_subject.setText(collegeRank.getCollegeRankSubject().getSubjectName());
        }
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

        ArticleDto dto = new ArticleDto(pageNum, PAGE_SIZE);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
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

        ArticleDto dto = new ArticleDto(pageNum, PAGE_SIZE);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
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

