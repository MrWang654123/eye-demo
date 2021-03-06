package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionBaseRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionLinearRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.features.event.DimensionOpenSuccessEvent;
import com.cheersmind.cheersgenie.features.event.ExamCompleteEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.TopicInExamCompleteEvent;
import com.cheersmind.cheersgenie.features.interfaces.OnBackPressListener;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.interfaces.SearchLayoutControlListener;
import com.cheersmind.cheersgenie.features.interfaces.SearchListener;
import com.cheersmind.cheersgenie.features.interfaces.SoftKeyboardStateHelper;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.ReportActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.TopicDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.features.view.EditTextPreIme;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 历史测评明细页面
 */
public class HistoryExamDetailFragment extends LazyLoadFragment implements SearchListener, SearchLayoutControlListener {

    //测评ID
    String examId;
    //测评状态
    int examStatus;

    //根布局
    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    @BindView(R.id.recycleView)
    protected RecyclerView recycleView;
    protected Unbinder unbinder;

    //话题列表（话题基础数据、孩子话题的信息、量表）
    List<TopicInfoEntity> topicList;
    List<TopicInfoEntity> topicListSearch;

    //适配器
    protected ExamDimensionBaseRecyclerAdapter recyclerAdapter;
    //适配器（网格式）
    protected ExamDimensionBaseRecyclerAdapter gridRecyclerAdapter;
    //适配器（线性式）
    protected ExamDimensionBaseRecyclerAdapter linearRecyclerAdapter;

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    //空布局
    @BindView(R.id.emptyLayout)
    protected XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    protected FloatingActionButton fabGotoTop;

    //默认网格
    protected int layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID;

    //recycler子项的点击监听
    protected BaseQuickAdapter.OnItemClickListener recyclerItemClickListener =  new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            Object item = ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(position);
            //点击量表
            if (item instanceof DimensionInfoEntity) {
                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) item;
                int parentPosition = adapter.getParentPosition(dimensionInfoEntity);
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(parentPosition);
                TopicInfoChildEntity childTopic = topicInfoEntity.getChildTopic();
                //如果话题已完成则查看话题报告
                if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
//                    //查看话题报告
//                    ReportActivity.startReportActivity(getContext(), topicInfoEntity);

                    ExamReportDto dto = new ExamReportDto();
                    dto.setChildExamId(dimensionInfoEntity.getChild_exam_id());//孩子测评ID
                    dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国

                    //只有一个量表则查看量表报告
                    if (ArrayListUtil.isNotEmpty(topicInfoEntity.getDimensions())
                            && topicInfoEntity.getDimensions().size() == 1) {
                        dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
                        dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
                        dto.setDimensionId(dimensionInfoEntity.getDimensionId());//量表ID（目前用于报告推荐内容）

                    } else {
                        dto.setRelationId(dimensionInfoEntity.getTopicId());
                        dto.setRelationType(Dictionary.REPORT_TYPE_TOPIC);
                    }

                    ExamReportActivity.startExamReportActivity(getContext(), dto);

                } else {
                    //点击量表项的操作
                    operateClickDimension(dimensionInfoEntity, topicInfoEntity);
                }

            } else if (item instanceof TopicInfoEntity) {//点击header话题
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) item;
                //跳转到话题详情页面
                TopicDetailActivity.startTopicDetailActivity(getContext(),
                        topicInfoEntity.getTopicId(), topicInfoEntity,
                        examStatus,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MINE);
            }
        }
    };

    //recycler子项的孩子视图点击监听
    protected BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//            //话题详情
//            if (view.getId() == R.id.iv_desc) {
//                RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
//                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
//                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
//                //跳转到话题详情页面
//                TopicDetailActivity.startTopicDetailActivity(getContext(), topicInfoEntity.getTopicId(), topicInfoEntity);
//
//            } else if (view.getId() == R.id.tv_nav_to_report) {//查看报告
//                RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
//                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
//                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
//                //查看话题报告
//                ReportActivity.startReportActivity(getContext(), topicInfoEntity);
//            }

            Object item = ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(position);
            TopicInfoEntity topicInfoEntity = (TopicInfoEntity) item;
            //话题详情
            if (view.getId() == R.id.iv_desc) {
                //跳转到话题详情页面
                TopicDetailActivity.startTopicDetailActivity(getContext(),
                        topicInfoEntity.getTopicId(), topicInfoEntity,
                        examStatus,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MINE);
            }
//            } else if (view.getId() == R.id.tv_nav_to_report) {//查看报告
//                //查看话题报告
//                ReportActivity.startReportActivity(getContext(), topicInfoEntity);
//
//            } else if (view.getId() == R.id.iv_expand) {//伸缩按钮
//                if (topicInfoEntity != null) {
//                    if (topicInfoEntity.isExpanded()) {
//                        adapter.collapse(position);
//                    } else {
//                        adapter.expand(position);
//                    }
//                }
//            }

        }
    };


    //下拉刷新的监听
    protected SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //刷新孩子话题
            refreshChildTopList();
        }
    };
    //上拉加载更多的监听
    protected BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多孩子话题
            loadMoreChildTopicList();
        }
    };

    //页长度
    protected static final int PAGE_SIZE = 5;
    //页码
    protected int pageNum = 1;
    //后台总记录数
    protected int totalCount = 0;

    //滚动监听
    RecyclerViewScrollListener scrollListener;


    //搜索布局
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.et_search)
    EditTextPreIme etSearch;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    //搜索覆盖布局
    @BindView(R.id.rl_search_overlay)
    RelativeLayout rlSearchOverlay;

    //最后一次搜索文本
    String lastSearchText = "";

    //顶部粘性布局
    @BindView(R.id.sticky_header_view)
    View stickyHeaderView;


    @Override
    protected int setContentView() {
        return R.layout.fragment_report_completed;
    }

    @Override
    protected void lazyLoad() {
        //加载更多孩子话题
        loadMoreChildTopicList();
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

        //测评ID
        Bundle bundle = getArguments();
        if(bundle!=null) {
            examId = bundle.getString("exam_id");
            examStatus = bundle.getInt("exam_status", Dictionary.EXAM_STATUS_OVER);
        }

        try {
            gridRecyclerAdapter = new ExamDimensionRecyclerAdapter(this, null).setExamStatus(examStatus);
            linearRecyclerAdapter = new ExamDimensionLinearRecyclerAdapter(this,  null).setExamStatus(examStatus);
        } catch (QSCustomException e) {
            e.printStackTrace();
        }
        //设置适配器属性
        settingAdapterProperty(gridRecyclerAdapter);
        settingAdapterProperty(linearRecyclerAdapter);

        //默认设置网格布局
        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {
            recyclerAdapter = gridRecyclerAdapter;
        } else {
            recyclerAdapter = linearRecyclerAdapter;
        }

        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());

        recycleView.setAdapter(recyclerAdapter);

        //线性布局
        if (recyclerAdapter instanceof ExamDimensionLinearRecyclerAdapter) {
            recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        } else if (recyclerAdapter instanceof ExamDimensionRecyclerAdapter) {//网格布局
            //网格布局管理器
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.exam_grid_layout_span_count));
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return recyclerAdapter.getItemViewType(position) == ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_DIMENSION ? 1 : gridLayoutManager.getSpanCount();
                }
            });
            //必须在setAdapter之后
            recycleView.setLayoutManager(gridLayoutManager);

        } else {
            recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        //滑动监听
        try {
            scrollListener = new RecyclerViewScrollListener(getContext(), fabGotoTop);
            recycleView.addOnScrollListener(scrollListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //设置添加空点击监听，防止点击渗透到被覆盖的视图
        emptyLayout.setAttachBlankClickListener();
        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_report));
        //空布局重载点击监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多孩子话题
                loadMoreChildTopicList();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);


        //初始隐藏搜索布局
        llSearch.setVisibility(View.GONE);
        //初始隐藏搜索覆盖布局
        rlSearchOverlay.setVisibility(View.GONE);

        //监听搜索输入框的软键盘回车键
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //搜索
                    doSearch();
                    return true;
                }

                return false;
            }
        });

        //清空按钮的显隐
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ivClear.getVisibility() == View.GONE) {
                        ivClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClear.getVisibility() == View.VISIBLE) {
                        ivClear.setVisibility(View.GONE);
                    }
                }
            }
        });

        //编辑框点击监听
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlay();
            }
        });

        //返回键监听
        etSearch.setBackPressListener(new OnBackPressListener() {
            @Override
            public void onBackPress() {
                //隐藏覆盖层
                if (rlSearchOverlay.getVisibility() == View.VISIBLE) {
                    hideOverlay();
                }
            }
        });


        //软键盘开关监听
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(getContext(),llRoot);
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                //键盘打开
                System.out.println("软键盘打开");
            }
            @Override
            public void onSoftKeyboardClosed() {
                //键盘关闭
                System.out.println("软键盘关闭");
                hideOverlay();
            }
        });

        /*rlSearchOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int rootHeight = rlSearchOverlay.getRootView().getHeight();
                int curHeight = rlSearchOverlay.getHeight();
                int heightDiff = rootHeight - curHeight;
                if (curHeight > 0 ) {
                    if (getActivity().getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
                        //hide input method
                    } else {
                        hideOverlay();
                    }
                }
            }
        });*/

        //初始隐藏顶部粘性布局
        stickyHeaderView.setVisibility(View.GONE);

    }


    /**
     * 设置适配器属性
     * @param recyclerAdapter 适配器
     */
    protected void settingAdapterProperty(BaseQuickAdapter recyclerAdapter) {
        //        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解绑ButterKnife
        unbinder.unbind();

        //注销事件
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.iv_search, R.id.iv_clear, R.id.rl_search_overlay})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //清空搜索框的按钮
            case R.id.iv_clear: {
                etSearch.setText("");
                break;
            }
            //搜索
            case R.id.iv_search: {
                doSearch();
                break;
            }

            //搜索覆盖图
            case R.id.rl_search_overlay: {
                //隐藏软键盘和搜索覆盖层
                SoftInputUtil.closeSoftInput(getActivity());
                hideOverlay();
                break;
            }
        }
    }


    /**
     * 搜索
     */
    private void doSearch() {
        String searchText = etSearch.getText().toString();
        if (!lastSearchText.equals(searchText)) {
            //搜索
            search(searchText);
            //记录最后一次搜索文本
            lastSearchText = searchText;
        }

        //隐藏软键盘和搜索覆盖层
        SoftInputUtil.closeSoftInput(getActivity());
        hideOverlay();
    }


    /**
     * 显示搜索布局
     */
    private void showSearchLayout() {
        llSearch.setVisibility(View.VISIBLE);
        //初始化搜索框
        lastSearchText = "";
        etSearch.setText(lastSearchText);
        etSearch.requestFocus();
        //搜索覆盖布局
        rlSearchOverlay.setVisibility(View.VISIBLE);
        rlSearchOverlay.setEnabled(true);
        //打开软键盘
        SoftInputUtil.openSoftInput(getActivity(), etSearch);
    }


    /**
     * 隐藏搜索布局
     */
    private void hideSearchLayout() {
        llSearch.setVisibility(View.GONE);
        //隐藏软键盘和搜索覆盖层
        SoftInputUtil.closeSoftInput(getActivity());
        hideOverlay();
        //清空最后一次搜索文本记录
        lastSearchText = "";

        //清空话题搜索集合
        if (ArrayListUtil.isNotEmpty(topicListSearch)) {
            topicListSearch.clear();
            //重新布局
            switchLayout(layoutType);
        }
    }


    /**
     * 隐藏覆盖物
     */
    private void hideOverlay() {
        //搜索覆盖布局
        rlSearchOverlay.setVisibility(View.GONE);
        rlSearchOverlay.setEnabled(false);
    }


    /**
     * 显示搜索覆盖层
     */
    private void showOverlay() {
        //搜索覆盖布局
        rlSearchOverlay.setVisibility(View.VISIBLE);
        rlSearchOverlay.setEnabled(true);
    }


    /**
     * 问题提交成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionSubmitNotice(QuestionSubmitSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            DimensionInfoEntity dimension = event.getDimension();
            onQuestionSubmit(dimension);
        }

    }

    /**
     * 量表开启成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDimensionOpenSuccessNotice(DimensionOpenSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            DimensionInfoEntity dimension = event.getDimension();
            onDimensionOpenSuccess(dimension);
        }
    }


    /**
     * 是否满足解锁条件
     * @param topicInfo 话题
     * @return true 满足
     */
    protected boolean isMeetUnlockCondition(TopicInfoEntity topicInfo) {
        //是否满足解锁条件
        boolean isMeetUnlockCondition = false;

        if (topicInfo != null) {
            List<DimensionInfoEntity> dimensions = topicInfo.getDimensions();
            if (ArrayListUtil.isNotEmpty(dimensions)) {
                //是否有被锁的量表
                boolean hasLockedDimension = false;
                //被锁的量表
                DimensionInfoEntity lockedDimension = null;

                for (DimensionInfoEntity dimension : dimensions) {
                    //被锁状态
                    if (dimension.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                        lockedDimension = dimension;
                        hasLockedDimension = true;
                        break;
                    }
                }

                //存在被锁的量表则判断是否满足解锁条件
                if (hasLockedDimension) {

                    //解锁必须先做完的量表id集合
                    String [] dimensionIds = lockedDimension.getPreDimensions().split(",");
                    Set<String> dimensionSet = new HashSet<>(Arrays.asList(dimensionIds));

                    isMeetUnlockCondition = true;
                    //存在一个未做完的量表，则视为不满足解锁条件
                    for (DimensionInfoEntity dimension : dimensions) {
                        if (dimensionSet.contains(dimension.getDimensionId())) {
                            if (dimension.getChildDimension() != null
                                    && dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                                isMeetUnlockCondition = false;
                            }
                        }
                    }
                }
            }
        }

        return isMeetUnlockCondition;
    }

    /**
     * 判断话题（场景）是否完成，如果完成则设置完成状态
     * @param topicInfo 话题对象
     */
    protected boolean handleTopicIsComplete(TopicInfoEntity topicInfo) {
        boolean isComplete = false;

        if (topicInfo != null) {
            List<DimensionInfoEntity> dimensions = topicInfo.getDimensions();
            //量表非空
            if (ArrayListUtil.isNotEmpty(dimensions)) {
                //话题是否完成
                boolean isTopicComplete = true;
                for (DimensionInfoEntity tempDimension : dimensions) {
                    //孩子量表对象为空或者状态为0，视为未完成
                    if (tempDimension.getChildDimension() == null
                            || tempDimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                        isTopicComplete = false;
                        break;
                    }
                }

                //话题（场景）完成，则刷新header模型对应的列表项
                if (isTopicComplete) {
                    //孩子话题非空
                    if (topicInfo.getChildTopic() != null) {
                        //设置话题为完成状态
                        topicInfo.getChildTopic().setStatus(Dictionary.TOPIC_STATUS_COMPLETE);
                        isComplete = true;
                    }
                }
            }
        }

        return isComplete;
    }


    /**
     * 刷新header模型对应的列表项
     * @param headerPosition 列表索引
     */
    protected void refreshHeader(int headerPosition) {
        int tempHeaderPosition = headerPosition + recyclerAdapter.getHeaderLayoutCount();
        recyclerAdapter.notifyItemChanged(tempHeaderPosition);//局部数显列表项，把header计算在内
    }


    /**
     * 重置为第一页
     */
    protected void resetPageInfo() {
        //页码
        pageNum = 1;
        //话题集合
        topicList = null;
        //总数量
        totalCount = 0;
        //清空话题搜索集合
        if (ArrayListUtil.isNotEmpty(topicListSearch)) {
            topicListSearch.clear();
        }
    }


    /**
     * 刷新孩子话题列表
     */
    protected void refreshChildTopList() {
        //重置为第一页
        resetPageInfo();
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildHistoryExamDetail(defaultChildId,examId,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onRefreshChildTopListFailed(e);
            }

            @Override
            public void onResponse(Object obj) {
                onRefreshChildTopListSuccess(obj);

            }
        },httpTag, getActivity());
    }


    /**
     * 刷新孩子话题列表成功
     * @param obj 通信响应结果
     */
    protected void onRefreshChildTopListSuccess(Object obj) {
        try {
            //开启上拉加载功能
            recyclerAdapter.setEnableLoadMore(true);
            //结束下拉刷新动画
            swipeRefreshLayout.setRefreshing(false);
            //设置空布局：隐藏
            emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
            TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);

            //后台总记录数
            totalCount = topicData.getTotal();
            List<TopicInfoEntity> dataList = topicData.getItems();

            //空数据处理
            if (ArrayListUtil.isEmpty(dataList)) {
                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                return;
            }

            //搜索文本如果不为空，则需要过滤
            if (!TextUtils.isEmpty(lastSearchText)) {
                if (ArrayListUtil.isEmpty(topicListSearch)) {
                    topicListSearch = new ArrayList<>();
                } else {
                    topicListSearch.clear();
                }

                for (TopicInfoEntity topicInfo : dataList) {
                    if (!TextUtils.isEmpty(topicInfo.getTopicName()) && topicInfo.getTopicName().contains(lastSearchText)) {
                        topicListSearch.add(topicInfo);
                        continue;
                    }

                    if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
                        for (DimensionInfoEntity dimensionInfo : topicInfo.getDimensions()) {
                            if (!TextUtils.isEmpty(dimensionInfo.getDimensionName()) && dimensionInfo.getDimensionName().contains(lastSearchText)) {
                                topicListSearch.add(topicInfo);
                                break;
                            }
                        }
                    }
                }
            }

            List recyclerItem = null;
            //话题搜索集合不为空，则使用话题搜索集合作为列表数据项
            if (ArrayListUtil.isNotEmpty(topicListSearch)) {
                //转成列表的数据项
                recyclerItem = topicInfoEntityToRecyclerMulti(topicListSearch);

            } else {
                //转成列表的数据项
                recyclerItem = topicInfoEntityToRecyclerMulti(dataList);
            }

            //下拉刷新
            recyclerAdapter.setNewData(recyclerItem);

            //初始展开
            recyclerAdapter.expandAll();

            topicList = dataList;

            //判断是否全部加载结束
            if (topicList.size() >= totalCount) {
                //全部加载结束
                recyclerAdapter.loadMoreEnd();
            } else {
                //本次加载完成
                recyclerAdapter.loadMoreComplete();
            }

            //页码+1
            pageNum++;

            //回调布局切换
//            Fragment parentFragment = getParentFragment();
//            if (parentFragment != null && parentFragment instanceof ExamLayoutListener && pageNum == 2) {
//                ((ExamLayoutListener)parentFragment).change(layoutType, true);
//            }

        } catch (Exception e) {
            e.printStackTrace();
            //设置空布局：没有数据，可重载
            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
            //清空列表数据
            recyclerAdapter.setNewData(null);

            //回调布局切换
//            Fragment parentFragment = getParentFragment();
//            if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
//                ((ExamLayoutListener)parentFragment).change(layoutType, false);
//            }
        }
    }


    /**
     * 刷新孩子话题列表失败
     * @param e 异常
     */
    protected void onRefreshChildTopListFailed(QSCustomException e) {
        //开启上拉加载功能
        recyclerAdapter.setEnableLoadMore(true);
        //结束下拉刷新动画
        swipeRefreshLayout.setRefreshing(false);
        //设置空布局：网络错误
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
        //清空列表数据
        recyclerAdapter.setNewData(null);

        //回调布局切换
//        Fragment parentFragment = getParentFragment();
//        if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
//            ((ExamLayoutListener)parentFragment).change(layoutType, false);
//        }
    }


    /**
     * 加载更多孩子话题列表
     */
    protected void loadMoreChildTopicList() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildHistoryExamDetail(defaultChildId,examId,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onLoadMoreChildTopicListFailed(e);
            }

            @Override
            public void onResponse(Object obj) {
                onLoadMoreChildTopicListSuccess(obj);
            }
        }, httpTag, getActivity());
    }


    /**
     * 加载更多孩子话题列表成功
     * @param obj 通信响应结果
     */
    protected void onLoadMoreChildTopicListSuccess(Object obj) {
        try {
            //开启下拉刷新功能
            swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突
            //设置空布局：隐藏
            emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
            TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);

            //后台总记录数
            totalCount = topicData.getTotal();
            List<TopicInfoEntity> dataList = topicData.getItems();

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

            if (topicList == null) {
                topicList = new ArrayList<>();
            }
            topicList.addAll(dataList);

            //判断是否全部加载结束
            if (topicList.size() >= totalCount) {
                //全部加载结束
                recyclerAdapter.loadMoreEnd();
            } else {
                //本次加载完成
                recyclerAdapter.loadMoreComplete();
            }

            //页码+1
            pageNum++;

            //回调布局切换
//            Fragment parentFragment = getParentFragment();
//            if (parentFragment != null && parentFragment instanceof ExamLayoutListener && pageNum == 2) {
//                ((ExamLayoutListener)parentFragment).change(layoutType, true);
//            }

        } catch (Exception e) {
            e.printStackTrace();
            if (recyclerAdapter.getData().size() == 0) {
                //设置空布局：没有数据，可重载
                emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);

                //回调布局切换
//                Fragment parentFragment = getParentFragment();
//                if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
//                    ((ExamLayoutListener)parentFragment).change(layoutType, false);
//                }

            } else {
                //加载失败处理
                recyclerAdapter.loadMoreFail();
            }
        }
    }


    /**
     * 加载更多孩子话题列表失败
     * @param e 异常
     */
    protected void onLoadMoreChildTopicListFailed(QSCustomException e) {
        //开启下拉刷新功能
        swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

        if (recyclerAdapter.getData().size() == 0) {
            //设置空布局：网络错误
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);

            //回调布局切换
//            Fragment parentFragment = getParentFragment();
//            if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
//                ((ExamLayoutListener)parentFragment).change(layoutType, false);
//            }

        } else {
            //加载失败处理
            recyclerAdapter.loadMoreFail();
        }
    }


    /**
     * 话题列表转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
     *
     * @param topicList 话题集合
     * @return 适配recycler的分组数据模型
     */
    protected List<RecyclerCommonSection<DimensionInfoEntity>> topicInfoEntityToRecyclerSection(List<TopicInfoEntity> topicList) {
        List<RecyclerCommonSection<DimensionInfoEntity>> resList = null;

        if (ArrayListUtil.isNotEmpty(topicList)) {
            resList = new ArrayList<RecyclerCommonSection<DimensionInfoEntity>>();
            //遍历话题列表
            for (TopicInfoEntity topicInfo : topicList) {
                List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                    //添加适配器的header模型
                    resList.add(new RecyclerCommonSection<DimensionInfoEntity>(true, topicInfo.getTopicName(), topicInfo));
                    //遍历维度列表（量表）
                    for (DimensionInfoEntity dimensionInfo : dimensionInfoList) {
                        //添加适配器的普通模型
                        resList.add(new RecyclerCommonSection<DimensionInfoEntity>(dimensionInfo, topicInfo));
                    }
                }
            }
        }

        return resList;
    }


    /**
     * 话题列表转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
     *
     * @param topicList 话题集合
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> topicInfoEntityToRecyclerMulti(List<TopicInfoEntity> topicList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(topicList)) {
            resList = new ArrayList<>();
            boolean isFirstInExam = true;
            //遍历话题列表
            for (TopicInfoEntity topicInfo : topicList) {
                //设置第一个的标记
                if (isFirstInExam) {
                    topicInfo.setFirstInExam(true);
                    isFirstInExam = false;
                }
                //已经添加过了就清空
                if (topicInfo.hasSubItem()) {
                    topicInfo.getSubItems().clear();
                }
                //默认设置为不展开
                topicInfo.setExpanded(false);

                List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                    //添加适配器的1级模型
                    resList.add(topicInfo);
                    //遍历维度列表（量表）
                    for (int i=0; i<dimensionInfoList.size(); i++) {
                        DimensionInfoEntity dimensionInfo = dimensionInfoList.get(i);
                        //标记是最后一个量表
                        if (i == dimensionInfoList.size() - 1) {
                            dimensionInfo.setLastInTopic(true);
                        }
                        //添加适配器的2级模型
                        topicInfo.addSubItem(dimensionInfo);
                    }
                }
            }
        }

        return resList;
    }


    /**
     * 获取共总有多少项（话题加量表）
     *
     * @param topicList 话题集合
     * @return 适配recycler的分组数据模型
     */
    protected int getTotalItem(List<TopicInfoEntity> topicList) {
        int total = 0;

        if (ArrayListUtil.isNotEmpty(topicList)) {
            //话题数量
            total = topicList.size();
            //遍历话题列表
            for (TopicInfoEntity topicInfo : topicList) {
                List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                    total += dimensionInfoList.size();
                }
            }
        }

        return total;
    }


    /**
     * 操作点击量表
     * @param dimensionInfoEntity 量表
     * @param topicInfoEntity 话题
     */
    protected void operateClickDimension(final DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        if( dimensionInfoEntity == null ){
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), "打开测评失败，请稍后再试");
            }
            return;
        }

        //被锁定
        if(dimensionInfoEntity.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE){
            //测评已结束
            if (examStatus == Dictionary.EXAM_STATUS_OVER) {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), getResources().getString(R.string.exam_over_tip));
                }
            } else if (examStatus == Dictionary.EXAM_STATUS_INACTIVE) {//测评未开始
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), getResources().getString(R.string.exam_inactive_tip));
                }
            } else {
                //锁定提示
                lockedDimensionTip(dimensionInfoEntity, topicInfoEntity);
            }

        } else {
            //孩子量表对象
            DimensionInfoChildEntity entity = dimensionInfoEntity.getChildDimension();
            //从未开启过的状态
            if (entity == null) {
                //开启量表
                startDimension(dimensionInfoEntity, topicInfoEntity);

            } else {//已经开启过的状态
                //未完成状态
                if(entity.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE){
                    //开启量表
                    startDimension(dimensionInfoEntity, topicInfoEntity);

                } else {
//                    //已完成状态，显示报告
//                    showDimensionReport(getContext(), dimensionInfoEntity);

                    ExamReportDto dto = new ExamReportDto();
                    dto.setChildExamId(dimensionInfoEntity.getChild_exam_id());//孩子测评ID
                    dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                    dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
                    dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
                    dto.setDimensionId(dimensionInfoEntity.getDimensionId());//量表ID（目前用于报告推荐内容）

                    ExamReportActivity.startExamReportActivity(getContext(), dto);
                }
            }
        }

    }


    /**
     * 开启量表
     * @param dimensionInfoEntity 量表
     * @param topicInfoEntity 话题
     */
    private void startDimension(DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        //测评已结束
        if (examStatus == Dictionary.EXAM_STATUS_OVER) {
//            if (getActivity() != null) {
//                ToastUtil.showShort(getActivity().getApplication(), getResources().getString(R.string.exam_over_tip));
//            }
            //跳转到量表详细页面，传递量表对象和话题对象
            DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                    dimensionInfoEntity, topicInfoEntity,
                    examStatus,
                    Dictionary.FROM_ACTIVITY_TO_QUESTION_MINE);

        } else if (examStatus == Dictionary.EXAM_STATUS_INACTIVE) {//测评未开始
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), getResources().getString(R.string.exam_inactive_tip));
            }
        } else {
            //跳转到量表详细页面，传递量表对象和话题对象
            DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                    dimensionInfoEntity, topicInfoEntity,
                    examStatus,
                    Dictionary.FROM_ACTIVITY_TO_QUESTION_MINE);
        }
    }

    /**
     * 显示量表报告
     */
    protected void showDimensionReport(Context context, DimensionInfoEntity dimensionInfo) {
//        ToastUtil.showShort(getContext(), "查看该量表报告");
        try {
            new DimensionReportDialog(context, dimensionInfo, null).show();
        } catch (Exception e) {
            e.printStackTrace();
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), e.getMessage());
            }
        }
    }

    /**
     * 量表被锁定的提示
     */
    protected void lockedDimensionTip(DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
//        ToastUtil.showShort(getContext(), "被锁定");

        if(!TextUtils.isEmpty(dimensionInfoEntity.getPreDimensions())){
            String [] dimensionIds = dimensionInfoEntity.getPreDimensions().split(",");
            List<DimensionInfoEntity> dimensions = topicInfoEntity.getDimensions();
            if(dimensionIds.length>0 && dimensions.size()>0){
                StringBuffer stringBuffer = new StringBuffer("");
                for(int i=0;i<dimensions.size();i++){
                    for(int j=0;j<dimensionIds.length;j++){
                        if(dimensionIds[j].equals(dimensions.get(i).getDimensionId())){
                            stringBuffer.append(dimensions.get(i).getDimensionName());
                            if(j!=dimensionIds.length-1){
                                stringBuffer.append("、");
                            }
                        }
                    }
                }
                if(!TextUtils.isEmpty(stringBuffer.toString())){
                    String str = getActivity().getResources().getString(R.string.lock_tip,stringBuffer.toString());
                    if (getActivity() != null) {
                        ToastUtil.showLong(getActivity().getApplication(), str);
                    }
                }
            }
        }
    }


    /**
     * 切换布局
     */
    public void switchLayout(int layoutType) {
        //改变布局标记值
        this.layoutType = layoutType;

        //改变列表布局
        changeRecyclerViewLayout(layoutType);
    }


    /**
     * 改变列表布局
     * @param layoutType
     */
    protected void changeRecyclerViewLayout(int layoutType) {

        List topicList = null;
        //如果话题搜索集合不为空，则赋值话题搜索集合
        if (ArrayListUtil.isNotEmpty(this.topicListSearch)) {
            topicList = this.topicListSearch;
        } else {
            topicList = this.topicList;
        }

        //线性
        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {
            List<MultiItemEntity> newData = topicInfoEntityToRecyclerMulti(topicList);
            linearRecyclerAdapter.setNewData(newData);
            recyclerAdapter = linearRecyclerAdapter;
            recycleView.setAdapter(recyclerAdapter);
            recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerAdapter.expandAll();

        } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {//网格
//            List<RecyclerCommonSection<DimensionInfoEntity>> newData = topicInfoEntityToRecyclerSection(topicList);
//            gridRecyclerAdapter.setNewData(newData);
//            recyclerAdapter = gridRecyclerAdapter;
//            //setAdapter之前调用
//            recycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//            recycleView.setAdapter(recyclerAdapter);

            List<MultiItemEntity> newData = topicInfoEntityToRecyclerMulti(topicList);
            gridRecyclerAdapter.setNewData(newData);
            recyclerAdapter = gridRecyclerAdapter;
            recycleView.setAdapter(recyclerAdapter);
            //网格布局管理器（切换后受到了影响，得重新设置对象）
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.exam_grid_layout_span_count));
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return recyclerAdapter.getItemViewType(position) == ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_DIMENSION ? 1 : gridLayoutManager.getSpanCount();
                }
            });
            //必须在setAdapter之后调用
            recycleView.setLayoutManager(gridLayoutManager);
            recyclerAdapter.expandAll();
        }

        //判断是否全部加载结束
        if (recyclerAdapter.getData().size() >= totalCount) {
            //全部加载结束
            recyclerAdapter.loadMoreEnd();
        } else {
            //本次加载完成
            recyclerAdapter.loadMoreComplete();
        }

        //清除滚动数据
        scrollListener.clearScrollYData();

    }



    @Override
    public void search(String searchText) {
        //搜索文本为空，则清空话题搜索集合
        if (TextUtils.isEmpty(searchText)) {
            if (topicListSearch != null) {
                topicListSearch.clear();
            }

            //空布局为无数据状态，则置为隐藏
            if (emptyLayout.getErrorState() == XEmptyLayout.NO_DATA) {
                //话题集合不为空
                if (ArrayListUtil.isNotEmpty(topicList)) {
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                }
            }

        } else {
            //从topicList的话题名称和量表名称中进行检索
            if (ArrayListUtil.isNotEmpty(topicList)) {
                if (ArrayListUtil.isEmpty(topicListSearch)) {
                    topicListSearch = new ArrayList<>();
                } else {
                    topicListSearch.clear();
                }

                for (TopicInfoEntity topicInfo : topicList) {
                    if (!TextUtils.isEmpty(topicInfo.getTopicName()) && topicInfo.getTopicName().contains(searchText)) {
                        topicListSearch.add(topicInfo);
                        continue;
                    }

                    if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
                        for (DimensionInfoEntity dimensionInfo : topicInfo.getDimensions()) {
                            if (!TextUtils.isEmpty(dimensionInfo.getDimensionName()) && dimensionInfo.getDimensionName().contains(searchText)) {
                                topicListSearch.add(topicInfo);
                                break;
                            }
                        }
                    }
                }
            }

            //搜索结果空处理
            if (ArrayListUtil.isEmpty(topicListSearch)) {
                //备份原先的无数据提示
                String noDataTip = emptyLayout.getNoDataTip();
                //设置新的无数据提示文本
                emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_article_for_search));
                //空布局
                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                //设置原先的无数据提示文本
                emptyLayout.setNoDataTip(noDataTip);

                //调整总数
                if (ArrayListUtil.isNotEmpty(topicList)) {
                    totalCount = topicList.size();
                } else {
                    totalCount = 0;
                }

                return;

            } else {
                //隐藏空布局
                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
            }
        }

        //调整总数
        if (ArrayListUtil.isNotEmpty(topicListSearch)) {
            totalCount = topicListSearch.size();
        } else {
            if (ArrayListUtil.isNotEmpty(topicList)) {
                totalCount = topicList.size();
            } else {
                totalCount = 0;
            }
        }

        switchLayout(layoutType);
    }


    @Override
    public void searchLayoutControl(boolean show) {
        if (show) {
            showSearchLayout();
        } else {
            //空布局为无数据状态，则置为隐藏
            if (emptyLayout.getErrorState() == XEmptyLayout.NO_DATA) {
                //话题集合不为空
                if (ArrayListUtil.isNotEmpty(topicList)) {
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                }
            }

            hideSearchLayout();
        }
    }

    @Override
    public void hideSoftInputAndOverlay() {
        //已经加载过数据
        if (hasLoaded) {
            if (rlSearchOverlay.getVisibility() == View.VISIBLE) {
                //隐藏软键盘和搜索覆盖层
                SoftInputUtil.closeSoftInput(getActivity());
                hideOverlay();
            }
        }
    }


    /**
     * 量表开启成功的通知事件的处理
     * @param dimension 量表对象
     */
    protected void onDimensionOpenSuccess(DimensionInfoEntity dimension) {
        //已经加载了数据
        if (hasLoaded) {
            if (dimension == null) {
                //重置为第一页
                resetPageInfo();
                //刷新孩子话题
                refreshChildTopList();

            } else {
//                //局部刷新量表对应的视图项
//                List<RecyclerCommonSection<DimensionInfoEntity>> data = recyclerAdapter.getData();
//                if (ArrayListUtil.isNotEmpty(data)) {
//                    //header模型位置
//                    int headerPosition = 0;
//
//                    for (int i = 0; i < data.size(); i++) {
//                        RecyclerCommonSection<DimensionInfoEntity> item = data.get(i);
//                        TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
//                        DimensionInfoEntity t = (DimensionInfoEntity) item.t;
//
//                        //t不为空
//                        if (t != null) {
//                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
//                            if (t.getTopicId().equals(dimension.getTopicId())
//                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
//                                //孩子话题如果为空，则创建孩子话题对象，并设置为未完成状态
//                                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
//                                if (childTopic == null) {
//                                    childTopic = new TopicInfoChildEntity();
//                                    childTopic.setStatus(Dictionary.TOPIC_STATUS_INCOMPLETE);
//                                    topicInfo.setChildTopic(childTopic);
//                                }
//
//                                //刷新对应量表的列表项
//                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
//                                int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
//                                recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内
//
//                                break;
//                            }
//
//                        } else {
//                            //t为空则代表是header模型
//                            headerPosition = i;
//                        }
//
//                    }
//                }

                /*-------------------------------------------*/

                //是否在当前列表中（搜索功能可能使得当前量表不在当前列表中）
                boolean existInList = false;
                //局部刷新量表对应的视图项
                List<MultiItemEntity> data = recyclerAdapter.getData();
                if (ArrayListUtil.isNotEmpty(data)) {
                    //header模型位置
//                    int headerPosition = 0;
                    TopicInfoEntity topicInfo = null;
                    DimensionInfoEntity t = null;

                    for (int i = 0; i < data.size(); i++) {
                        MultiItemEntity item = data.get(i);
                        if (item instanceof TopicInfoEntity) {
                            topicInfo = (TopicInfoEntity) item;
//                            headerPosition = i;

                        } else if (item instanceof DimensionInfoEntity) {
                            t = (DimensionInfoEntity) item;
                        }

                        //t不为空
                        if (t != null) {
                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
                            if (t.getTopicId().equals(dimension.getTopicId())
                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
                                //孩子话题如果为空，则创建孩子话题对象，并设置为未完成状态
                                //（目前用于开始答题了，但未做答案完成的提交就进入了话题详情页面，判断话题是否开始做过）
                                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
                                if (childTopic == null) {
                                    childTopic = new TopicInfoChildEntity();
                                    childTopic.setStatus(Dictionary.TOPIC_STATUS_INCOMPLETE);
                                    topicInfo.setChildTopic(childTopic);
                                }

                                //刷新对应量表的列表项
                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                                int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                                recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内

                                //标记当前量表存在当前列表中
                                existInList = true;

                                break;
                            }

                        }

                    }
                }

                //当前量表不存在当前列表中
                if (!existInList) {

                    for (int i = 0; i < topicList.size(); i++) {
                        TopicInfoEntity topicInfo = topicList.get(i);

                        if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
                            for (DimensionInfoEntity t : topicInfo.getDimensions()) {

                                //找出同一个量表，设置孩子量表，然后局部刷新列表项
                                if (t.getTopicId().equals(dimension.getTopicId())
                                        && t.getDimensionId().equals(dimension.getDimensionId())) {

                                    //孩子话题如果为空，则创建孩子话题对象，并设置为未完成状态
                                    //（目前用于开始答题了，但未做答案完成的提交就进入了话题详情页面，判断话题是否开始做过）
                                    TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
                                    if (childTopic == null) {
                                        childTopic = new TopicInfoChildEntity();
                                        childTopic.setStatus(Dictionary.TOPIC_STATUS_INCOMPLETE);
                                        topicInfo.setChildTopic(childTopic);
                                    }

                                    t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象

                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
    }


    /**
     * 问题提交成功的通知事件的处理
     * @param dimension 量表对象
     */
    protected void onQuestionSubmit(DimensionInfoEntity dimension) {
        //孩子量表对象
        DimensionInfoChildEntity childDimension = dimension.getChildDimension();

        //量表
        if (childDimension == null) {

            //局部刷新量表对应的视图项
            List<MultiItemEntity> data = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(data)) {
                TopicInfoEntity topicInfo = null;
                DimensionInfoEntity t = null;

                for (int i = 0; i < data.size(); i++) {
                    MultiItemEntity item = data.get(i);
                    if (item instanceof TopicInfoEntity) {
                        topicInfo = (TopicInfoEntity) item;

                    } else if (item instanceof DimensionInfoEntity) {
                        t = (DimensionInfoEntity) item;
                        //找出同一个量表，设置孩子量表，然后局部刷新列表项
                        if (t.getTopicId().equals(dimension.getTopicId())
                                && t.getDimensionId().equals(dimension.getDimensionId())) {

                            //话题只有一个量表，则视为话题完成了
                            if (topicInfo != null && topicInfo.getDimensions().size() == 1) {
                                //设置话题为完成状态
                                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
                                if (childTopic == null) {
                                    childTopic = new TopicInfoChildEntity();
                                    topicInfo.setChildTopic(childTopic);
                                }
                                childTopic.setStatus(Dictionary.TOPIC_STATUS_COMPLETE);
                                //发送测评下某个话题完成的通知事件
                                EventBus.getDefault().post(new TopicInExamCompleteEvent(examId));
                                //判断当前测评是否完成，做相应处理
                                handleChildExamStatus();
                            }

                            break;
                        }
                    }
                }
            }

            //重置为第一页
            resetPageInfo();
            //刷新孩子话题
            refreshChildTopList();

        } else {

//            //局部刷新量表对应的视图项
//            List<RecyclerCommonSection<DimensionInfoEntity>> data = recyclerAdapter.getData();
//            if (ArrayListUtil.isNotEmpty(data)) {
//                //header模型位置
//                int headerPosition = 0;
//
//                for (int i = 0; i < data.size(); i++) {
//                    RecyclerCommonSection<DimensionInfoEntity> item = data.get(i);
//                    TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
//                    DimensionInfoEntity t = (DimensionInfoEntity) item.t;
//
//                    //t不为空
//                    if (t != null ) {
//                        //找出同一个量表，设置孩子量表，然后局部刷新列表项
//                        if (t.getTopicId().equals(dimension.getTopicId())
//                                && t.getDimensionId().equals(dimension.getDimensionId())) {
//                            //刷新对应量表的列表项
//                            t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
//                            int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
//                            recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内
//
//                            //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
//                            if (isMeetUnlockCondition(topicInfo)) {
//                                //重置为第一页
//                                resetPageInfo();
//                                //刷新孩子话题
//                                refreshChildTopList();
//
//                            } else {
//                                //判断话题（场景）是否完成，完成则刷新header模型对应的列表项
//                                refreshHeader(headerPosition, topicInfo);
//                            }
//                            break;
//                        }
//
//                    } else {
//                        //t为空则代表是header模型
//                        headerPosition = i;
//                    }
//
//                }
//            }

            /*-------------------------------------------*/

            //是否在当前列表中（搜索功能可能使得当前量表不在当前列表中）
            boolean existInList = false;
            //局部刷新量表对应的视图项
            List<MultiItemEntity> data = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(data)) {
                //header模型位置
                int headerPosition = 0;
                TopicInfoEntity topicInfo = null;
                DimensionInfoEntity t = null;

                for (int i = 0; i < data.size(); i++) {
                    MultiItemEntity item = data.get(i);
                    if (item instanceof TopicInfoEntity) {
                        topicInfo = (TopicInfoEntity) item;
                        headerPosition = i;

                    } else if (item instanceof DimensionInfoEntity) {
                        t = (DimensionInfoEntity) item;
                    }

                    //t不为空
                    if (t != null ) {
                        //找出同一个量表，设置孩子量表，然后局部刷新列表项
                        if (t.getTopicId().equals(dimension.getTopicId())
                                && t.getDimensionId().equals(dimension.getDimensionId())) {
                            //刷新对应量表的列表项
                            t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                            int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                            recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内

                            //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
                            if (isMeetUnlockCondition(topicInfo)) {
                                //重置为第一页
                                resetPageInfo();
                                //刷新孩子话题
                                refreshChildTopList();

                            } else {
                                //处理话题是否为完成状态，如果已完成则刷新header模型对应的列表项
                                if (handleTopicIsComplete(topicInfo)) {
                                    refreshHeader(headerPosition);
                                    //发送测评下某个话题完成的通知事件
                                    EventBus.getDefault().post(new TopicInExamCompleteEvent(examId));
                                    //判断当前测评是否完成，做相应处理
                                    handleChildExamStatus();
                                }
                            }

                            //标记当前量表存在当前列表中
                            existInList = true;

                            break;
                        }

                    }
                }
            }

            //当前量表不存在当前列表中
            if (!existInList) {

                for (int i = 0; i < topicList.size(); i++) {
                    TopicInfoEntity topicInfo = topicList.get(i);

                    if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
                        for (DimensionInfoEntity t : topicInfo.getDimensions()) {
                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
                            if (t.getTopicId().equals(dimension.getTopicId())
                                    && t.getDimensionId().equals(dimension.getDimensionId())) {

                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象

                                //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
                                if (isMeetUnlockCondition(topicInfo)) {
                                    //重置为第一页
                                    resetPageInfo();
                                    //刷新孩子话题
                                    refreshChildTopList();

                                } else {
                                    //处理话题是否为完成状态
                                    if (handleTopicIsComplete(topicInfo)) {
                                        //判断当前测评是否完成，做相应处理
                                        handleChildExamStatus();
                                    }
                                }

                                break;
                            }
                        }
                    }

                }

            }

        }

    }


    /**
     * 判断当前测评是否完成，做相应处理
     */
    private void handleChildExamStatus() {
        if (ArrayListUtil.isNotEmpty(topicList)) {
            boolean examHasComplete = true;
            for (TopicInfoEntity topicInfo : topicList) {
                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
                if (childTopic == null || childTopic.getStatus() != Dictionary.TOPIC_STATUS_COMPLETE) {
                    examHasComplete = false;
                    break;
                }
            }

            //完成则发送测评完成通知事件
            if (examHasComplete) {
                EventBus.getDefault().post(new ExamCompleteEvent(examId));
            }
        }
    }


}

