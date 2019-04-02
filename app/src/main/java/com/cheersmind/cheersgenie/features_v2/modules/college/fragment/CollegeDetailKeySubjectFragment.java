package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.KeySubjectRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.KeySubject;
import com.cheersmind.cheersgenie.features_v2.entity.KeySubjectItem;
import com.cheersmind.cheersgenie.features_v2.entity.KeySubjectRootEntity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 学校的重点学科
 */
public class CollegeDetailKeySubjectFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //院校ID
    private String collegeId;
    //重点学科类型
    private String keySubjectType;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器的数据列表
    KeySubjectRecyclerAdapter recyclerAdapter;

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多数据
            loadData();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail_major;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            collegeId = bundle.getString(DtoKey.COLLEGE_ID);
            keySubjectType = bundle.getString(DtoKey.KEY_SUBJECT_TYPE);
        }

        //适配器
        recyclerAdapter = new KeySubjectRecyclerAdapter( null);
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
//        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //滑动监听
//        try {
//            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //设置无数据提示文本
        if (Dictionary.KEY_SUBJECT_TYPE_COUNTRY.equals(keySubjectType)) {
            emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_key_subject_country));

        } else if (Dictionary.KEY_SUBJECT_TYPE_FIRST_RATE.equals(keySubjectType)) {
            emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_key_subject_first_rate));

        } else {
            emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_major));
        }

        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                loadData();
            }
        });
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void lazyLoad() {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 停止Fling的消息
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopFlingNotice(StopFlingEvent event) {
        if (recycleView != null) {
            recycleView.stopScroll();
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
//        if (recyclerAdapter.getData().size() == 0) {
//            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
//        }

        DataRequestService.getInstance().getCollegeKeySubject(collegeId, keySubjectType, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    KeySubjectRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, KeySubjectRootEntity.class);

                    List<KeySubject> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //数据转换
                    List<MultiItemEntity> multiItemEntities = majorToRecyclerMulti(dataList);

                    recyclerAdapter.setNewData(multiItemEntities);
                    //全部加载结束
                    recyclerAdapter.loadMoreEnd();

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }

            }
        }, httpTag, getActivity());
    }


    /**
     * List<KeySubject>转成转成用于适配recycler的分组数据模型List<KeySubject>
     * @param dataList 重点学科集合
     * @return List<KeySubject>
     */
    protected List<MultiItemEntity> majorToRecyclerMulti(List<KeySubject> dataList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(dataList)) {
            resList = new ArrayList<>();
            //重点学科
            for (KeySubject subject : dataList) {
                //学科项
                String items = subject.getItems();
                try {
                    JSONArray jsonArray = new JSONArray(items);
                    if (jsonArray.length() > 0) {
                        subject.setItemType(Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0);
                        resList.add(subject);

                        for (int i=0; i<jsonArray.length(); i++) {
                            try {
                                String item = jsonArray.getString(i);
                                resList.add(new KeySubjectItem(item).setItemType(Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return resList;
    }

}

