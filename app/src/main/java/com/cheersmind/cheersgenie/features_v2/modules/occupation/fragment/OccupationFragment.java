package com.cheersmind.cheersgenie.features_v2.modules.occupation.fragment;

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
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationCategoryRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationRealmRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationTypeRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.OccupationDto;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItemRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRealm;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationType;
import com.cheersmind.cheersgenie.features_v2.interfaces.BackPressedHandler;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 职业列表
 */
public class OccupationFragment extends LazyLoadFragment implements BackPressedHandler {

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
    OccupationRecyclerAdapter recyclerAdapter;

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
            OccupationItem entity = recyclerAdapter.getData().get(position);
            OccupationDetailActivity.startOccupationDetailActivity(getContext(), entity);
        }
    };

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //职业列表Dto
    OccupationDto dto;

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

    OccupationTypeRecyclerAdapter typeRecyclerAdapter;
    OccupationRealmRecyclerAdapter realmRecyclerAdapter;
    OccupationCategoryRecyclerAdapter categoryRecyclerAdapter;

    //职业类型的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener typeRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            OccupationType type = typeRecyclerAdapter.getData().get(position);
            //点击是不同项
            if (!dto.getType().getName().equals(type.getName())) {

                //领域
                List<OccupationRealm> realms = DataSupport.where("type = ?", String.valueOf(type.getType())).find(OccupationRealm.class);
                dto.setRealm(realms.get(0));
                realmRecyclerAdapter.setNewData(realms);

                //门类
                List<OccupationCategory> categories = realms.get(0).getCategoriesFromDB();
                dto.setCategory(categories.get(0));
                categoryRecyclerAdapter.setNewData(categories);

                dto.setType(type);
                //刷新列表
                typeRecyclerAdapter.notifyDataSetChanged();

                //刷新触发布局文本
                refreshToggleText(dto);
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    //职业领域的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener realmRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            OccupationRealm realm = realmRecyclerAdapter.getData().get(position);
            //点击是不同项
            if (!dto.getRealm().getRealm().equals(realm.getRealm())) {
                //改变门类数据
                List<OccupationCategory> categories = realm.getCategoriesFromDB();
                categoryRecyclerAdapter.setNewData(categories);
                dto.setCategory(categories.get(0));
                dto.setRealm(realm);
                //刷新触发布局文本
                refreshToggleText(dto);
                //刷新列表
                realmRecyclerAdapter.notifyDataSetChanged();
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    //门类的recycler item点击监听
    BaseQuickAdapter.OnItemClickListener categoryRecyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            OccupationCategory category = categoryRecyclerAdapter.getData().get(position);
            if (!dto.getCategory().getName().equals(category.getName())) {
                dto.setCategory(category);
                //刷新触发布局文本
                refreshToggleText(dto);
                //刷新列表
                categoryRecyclerAdapter.notifyDataSetChanged();
                //刷新数据
                refreshData();
            }

            hideRankLayout();
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_occupation_list;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new OccupationRecyclerAdapter(getContext(), R.layout.recycleritem_occupation, null);
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
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_occupation_list));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        dto = new OccupationDto(pageNum, PAGE_SIZE);
        //初始条件数据
        initConditionData();
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
     * 初始化条件数据
     */
    private void initConditionData() {
        //初始隐藏排名布局
        flRank.setVisibility(View.GONE);
        //初始化筛选条件选中数据
        List<OccupationType> types = new ArrayList<>();
        types.add(new OccupationType(Dictionary.OCCUPATION_TYPE_ACT, "ACT"));
        types.add(new OccupationType(Dictionary.OCCUPATION_TYPE_INDUSTRY, "国标"));
//        List<OccupationRealm> realms = DataSupport.findAll(OccupationRealm.class);
        List<OccupationRealm> realms = DataSupport.where("type = ?", String.valueOf(types.get(0).getType())).find(OccupationRealm.class);
        //设置dto的默认值
        dto.setType(types.get(0));
        dto.setRealm(realms.get(0));
        List<OccupationCategory> categories = dto.getRealm().getCategoriesFromDB();
        dto.setCategory(categories.get(0));

        //类型
        typeRecyclerAdapter = new OccupationTypeRecyclerAdapter(
                getContext(), R.layout.recycleritem_occupation_common, types, dto);
        typeRecyclerAdapter.setOnItemClickListener(typeRecyclerItemClickListener);
        rvRankProvince.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankProvince.setAdapter(typeRecyclerAdapter);

        //领域
        realmRecyclerAdapter = new OccupationRealmRecyclerAdapter(
                getContext(), R.layout.recycleritem_occupation_common, realms, dto);
        realmRecyclerAdapter.setOnItemClickListener(realmRecyclerItemClickListener);
        rvRankLevel.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankLevel.setAdapter(realmRecyclerAdapter);

        //门类
        categoryRecyclerAdapter = new OccupationCategoryRecyclerAdapter(
                getContext(), R.layout.recycleritem_occupation_common, categories, dto);
        categoryRecyclerAdapter.setOnItemClickListener(categoryRecyclerItemClickListener);
        rvRankSubject.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankSubject.setAdapter(categoryRecyclerAdapter);

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

        //刷新筛条件布局的文本
        refreshToggleText(dto);
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
     * 刷新筛条件布局的文本
     * @param dto 筛选dto
     */
    private void refreshToggleText(OccupationDto dto) {
        if (dto == null) return;
        //类型
        cb_rank_area.setText(dto.getType().getName());
        //领域
        cb_rank_level.setText(dto.getRealm().getRealm());
        //门类
        cb_rank_subject.setText(dto.getCategory().getName());
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
        DataRequestService.getInstance().getOccupationList(dto, new BaseService.ServiceCallback() {
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
                    OccupationItemRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, OccupationItemRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<OccupationItem> dataList = rootEntity.getItems();

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
        DataRequestService.getInstance().getOccupationList(dto, new BaseService.ServiceCallback() {
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
                    OccupationItemRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, OccupationItemRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<OccupationItem> dataList = rootEntity.getItems();

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

