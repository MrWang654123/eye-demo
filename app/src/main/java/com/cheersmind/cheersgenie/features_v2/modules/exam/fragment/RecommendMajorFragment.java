package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features_v2.adapter.RecommendMajorRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.RecommendMajorDto;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajorRootEntity;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorEvent;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.modules.major.activity.MajorDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 推荐专业
 */
public class RecommendMajorFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评ID
    private String childExamId;

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

    @BindView(R.id.cb1)
    CheckBox cb1;
    @BindView(R.id.cb2)
    CheckBox cb2;
    @BindView(R.id.cb3)
    CheckBox cb3;
    @BindView(R.id.cb4)
    CheckBox cb4;

    //条件集合
    private List<String> conditionList = new ArrayList<>();
    //已选专业
    private List<RecommendMajor> selectMajor = new ArrayList<>();

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String type = "";
            switch (buttonView.getId()) {
                case R.id.cb1: {
                    type = "1";
                    break;
                }
                case R.id.cb2: {
                    type = "2";
                    break;
                }
                case R.id.cb3: {
                    type = "3";
                    break;
                }
                case R.id.cb4: {
                    type = "4";
                    break;
                }
            }

            if (isChecked) {
                conditionList.add(type);
            } else {
                conditionList.remove(type);
            }

            //刷新数据
            dto.setFromTypes(selectListToFromTypesStr(conditionList));
            refreshData();
            //清空已选
            selectMajor.clear();
        }
    };

    //适配器
    RecommendMajorRecyclerAdapter recyclerAdapter;

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
            RecommendMajor item = recyclerAdapter.getItem(position);
            if (item != null) {
                MajorItem majorItem = new MajorItem();
                majorItem.setMajor_name(item.getMajor_name());
                majorItem.setMajor_code(item.getMajor_code());
                //跳转到详情
                MajorDetailActivity.startMajorDetailActivity(getContext(), majorItem);

            } else {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), R.string.operate_fail);
                }
            }
        }
    };

    //recycler子项的孩子视图点击监听
    protected BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            RecommendMajor entity = recyclerAdapter.getItem(position);
            if (entity != null) {
                switch (view.getId()) {
                    //选择图标
                    case R.id.iv_select: {
                        SimpleDraweeView ivSelect = (SimpleDraweeView) view;
                        //设置选中标志
                        entity.setSelected(!entity.isSelected());
                        if (entity.isSelected()) {
                            ivSelect.setImageResource(R.drawable.tab_mine_checked);
                            //处理选中集合
                            if (!selectMajor.contains(entity)) {
                                selectMajor.add(entity);
                            }
                        } else {
                            ivSelect.setImageResource(R.drawable.tab_mine_normal);
                            //处理选中集合
                            if (selectMajor.contains(entity)) {
                                selectMajor.remove(entity);
                            }
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
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    RecommendMajorDto dto;


    @Override
    protected int setContentView() {
        return R.layout.fragment_recommend_major;
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
        recyclerAdapter = new RecommendMajorRecyclerAdapter(getContext(), R.layout.recycleritem_recommend_major, null);
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
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_rmd_major));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        cb1.setOnCheckedChangeListener(checkedChangeListener);
        cb2.setOnCheckedChangeListener(checkedChangeListener);
        cb3.setOnCheckedChangeListener(checkedChangeListener);
        cb4.setOnCheckedChangeListener(checkedChangeListener);

        dto = new RecommendMajorDto(pageNum, PAGE_SIZE);
        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        dto.setChildId(childId);
        dto.setChildExamId(childExamId);
        conditionList.add("1");
        conditionList.add("2");
        conditionList.add("3");
        conditionList.add("4");
        String fromTypes = selectListToFromTypesStr(conditionList);
        dto.setFromTypes(fromTypes);
    }

    /**
     * 选择的条件转成FromTypes参数字符串
     * @param selectList 选择的集合
     */
    private String selectListToFromTypesStr(List<String> selectList) {
        if (ArrayListUtil.isEmpty(selectList)) {
            return "";
        }

        StringBuilder fromTypes = new StringBuilder();
        if (ArrayListUtil.isNotEmpty(selectList)) {
            for (String str : selectList) {
                fromTypes.append(str).append(",");
            }
        }

        return fromTypes.substring(0, fromTypes.length() - 1);
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

    @OnClick({R.id.btn_add_major})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //添加专业
            case R.id.btn_add_major: {
                //非空
                if (selectMajor.size() > 0) {
//                    getHandler();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
////                        DataSupport.deleteAll(RecommendMajor.class);
//                            //拼接查询条件，获取已存在的记录
//                            StringBuilder builder = new StringBuilder();
//                            String[] params = new String[selectMajor.size() + 1];
//                            for (int i = 0; i < selectMajor.size(); i++) {
//                                RecommendMajor major = selectMajor.get(i);
//                                params[i + 1] = major.getMajor_code();
//                                builder.append("?,");
//                            }
//                            String condition = builder.substring(0, builder.length() - 1);
//                            params[0] = "major_code in (" + condition + ")";
//                            List<RecommendMajor> recommendMajors = DataSupport.where(params).find(RecommendMajor.class);
//                            //移除已存在记录
//                            selectMajor.removeAll(recommendMajors);
//                            //保存
//                            DataSupport.saveAll(selectMajor);
//
//                            getHandler().post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (getActivity() != null) {
//                                        getActivity().finish();
//                                        ToastUtil.showShort(getActivity().getApplication(), "添加成功");
//                                        //发送添加观察专业的事件
//                                        EventBus.getDefault().post(new AddObserveMajorSuccessEvent(selectMajor.size()));
//                                        selectMajor.clear();
//                                    }
//                                }
//                            });
//                        }
//                    }).start();

//                    //请求保存观察专业
//                    doSaveObserveMajor(selectMajor);

                    //发送将要保存观察专业的通知事件
                    EventBus.getDefault().post(new AddObserveMajorEvent(selectMajor));
                    //关闭页面
                    if (getActivity() != null) {
                        getActivity().finish();
                    }

                } else {
                    if (getActivity() != null) {
                        ToastUtil.showShort(getActivity().getApplication(), "请选择专业");
                    }
                }

                break;
            }
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
        DataRequestService.getInstance().getRecommendMajor(dto, new BaseService.ServiceCallback() {
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
        DataRequestService.getInstance().getRecommendMajor(dto, new BaseService.ServiceCallback() {
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
     * 保存观察专业
     * @param selectMajor 观察专业
     */
    private void doSaveObserveMajor(final List<RecommendMajor> selectMajor) {
        //显示通信等待
        LoadingView.getInstance().show(getContext(), httpTag);

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().postSaveObserveMajor(childId, selectMajor, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信等待
                    LoadingView.getInstance().dismiss();

                    if (getActivity() != null) {
                        getActivity().finish();
                        ToastUtil.showShort(getActivity().getApplication(), "添加成功");
                        //发送添加观察专业成功的通知事件
                        EventBus.getDefault().post(new AddObserveMajorSuccessEvent(selectMajor.size()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getString(R.string.operate_fail)));
                }
            }
        }, httpTag, getActivity());
    }

}
