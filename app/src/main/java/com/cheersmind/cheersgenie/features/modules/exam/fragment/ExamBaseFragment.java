package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionBaseRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionLinearRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.features.event.LocationExamInListEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
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
import com.cheersmind.cheersgenie.features.view.CircleProgressBar;
import com.cheersmind.cheersgenie.features.view.EditTextPreIme;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.ExamRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 测评基础页面
 */
public class ExamBaseFragment extends LazyLoadFragment implements SearchListener, SearchLayoutControlListener {

    //话题状态（默认获取完成的）
    protected int topicStatus = Dictionary.TOPIC_STATUS_COMPLETE;

    //根布局
    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    @BindView(R.id.recycleView)
    protected RecyclerView recycleView;
    protected Unbinder unbinder;

    //测评列表（测评数据、话题基础数据、孩子话题的信息、量表）
    List<ExamEntity> examList;
    List<ExamEntity> examListSearch;

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
//            //网格
//            RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
//            //非Header模型
//            if (!data.isHeader) {
//                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
//                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
//                //点击量表项的操作
//                operateClickDimension(dimensionInfoEntity, topicInfoEntity);
//            }

            Object item = ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(position);
            //点击量表
            if (item instanceof DimensionInfoEntity) {
                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) item;
                int parentPosition = adapter.getParentPosition(dimensionInfoEntity);
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(parentPosition);
                if (topicInfoEntity != null) {
                    TopicInfoChildEntity childTopic = topicInfoEntity.getChildTopic();
                    //如果话题已完成则查看话题报告
                    if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
                        //查看话题报告
                        ReportActivity.startReportActivity(getContext(), topicInfoEntity);
                    } else {
                        //点击量表项的操作
                        operateClickDimension(dimensionInfoEntity, topicInfoEntity);
                    }
                }

            } else if (item instanceof TopicInfoEntity) {//点击header话题
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) item;
//                TopicInfoChildEntity childTopic = topicInfoEntity.getChildTopic();
//                //如果话题已完成则查看话题报告
//                if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
//                    //查看话题报告
//                    ReportActivity.startReportActivity(getContext(), topicInfoEntity);
//                }

                //跳转到话题详情页面
                TopicDetailActivity.startTopicDetailActivity(getContext(),
                        topicInfoEntity.getTopicId(), topicInfoEntity,
                        Dictionary.EXAM_STATUS_DOING,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);
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
                        Dictionary.EXAM_STATUS_DOING,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);
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
//    protected SwipeRefreshLayout.OnChildScrollUpCallback childScrollUpCallback = new SwipeRefreshLayout.OnChildScrollUpCallback() {
//        @Override
//        public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
//            int top = parent.getTop();
//            System.out.println("刷新视图的top：" + top);
//
//            if (child != null) {
//                int childTop = child.getTop();
//                System.out.println("刷新的孩子视图的top：" + childTop);
//            }
//            return false;
//        }
//    };

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
    //初始列表滚动的位置
//    protected int initScrollPosition = 0;
    //初始滚动到指定测评
    protected String initScrollToExamId;


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
    //圆形进度圈
    @BindView(R.id.circleProgressBar)
    CircleProgressBar circleProgressBar;
    //测评名称
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //有效日期
    @BindView(R.id.tv_date)
    TextView tvDate;


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

        try {
            gridRecyclerAdapter = new ExamDimensionRecyclerAdapter(this, null);
            linearRecyclerAdapter = new ExamDimensionLinearRecyclerAdapter(this,  null);
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
            scrollListener = new StickyRecyclerViewScrollListener(getContext(), fabGotoTop);
            recycleView.addOnScrollListener(scrollListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);
//        swipeRefreshLayout.setOnChildScrollUpCallback(childScrollUpCallback);

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
        if (ArrayListUtil.isNotEmpty(examListSearch)) {
            examListSearch.clear();
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
     * 问题提交成功的通知事件的处理
     * @param dimension 量表
     */
    protected void onQuestionSubmit(DimensionInfoEntity dimension) {
        //重置为第一页
        resetPageInfo();
        //刷新孩子话题
        refreshChildTopList();
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
        recyclerAdapter.notifyItemChanged(tempHeaderPosition);//局部刷新列表项，把header计算在内
    }

    /**
     * 刷新测评对应的列表项
     * @param examPosition 列表索引
     */
    protected void refreshExamItem(int examPosition) {
        int tempPosition = examPosition + recyclerAdapter.getHeaderLayoutCount();
        recyclerAdapter.notifyItemChanged(tempPosition);//局部刷新列表项，把header计算在内
    }

    /**
     * 重置为第一页
     */
    protected void resetPageInfo() {
        //页码
        pageNum = 1;
        //总数量
        totalCount = 0;
        //清空话题搜索集合
        if (ArrayListUtil.isNotEmpty(examListSearch)) {
            examListSearch.clear();
        }
        //清空话题集合
        if (ArrayListUtil.isNotEmpty(examList)) {
            examList.clear();
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
        DataRequestService.getInstance().loadChildTopicListByStatus(defaultChildId,topicStatus,
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
            ExamRootEntity examRoot = InjectionWrapperUtil.injectMap(dataMap, ExamRootEntity.class);

            //后台总记录数
            totalCount = examRoot.getTotal();
            List<ExamEntity> dataList = examRoot.getItems();

            //空数据处理
            if (ArrayListUtil.isEmpty(dataList)) {
                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                return;
            }

            //根据搜索文本进行过滤
            filterBySearchText(dataList);

            List recyclerItem = null;
            //测评搜索集合不为空，则使用测评搜索集合作为列表数据项
            if (ArrayListUtil.isNotEmpty(examListSearch)) {
                //转成列表的数据项
                recyclerItem = topicInfoEntityToRecyclerMulti(examListSearch);

            } else {
                //转成列表的数据项
                recyclerItem = topicInfoEntityToRecyclerMulti(dataList);
            }

            //下拉刷新
            recyclerAdapter.setNewData(recyclerItem);

            //初始展开
            recyclerAdapter.expandAll();

            examList = dataList;

            //判断是否全部加载结束
            if (examList.size() >= totalCount) {
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

            //用第一项测评刷新粘性布局
            refreshStickyHeaderView(examList.get(0));

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
     * 根据搜索文本进行过滤
     * @param dataList 测评集合
     */
    private void filterBySearchText(List<ExamEntity> dataList) {
        //搜索文本如果不为空，则需要过滤
        if (!TextUtils.isEmpty(lastSearchText)) {
            if (ArrayListUtil.isEmpty(examListSearch)) {
                examListSearch = new ArrayList<>();
            } else {
                examListSearch.clear();
            }

            //测评
            for (ExamEntity exam : dataList) {
                if (!TextUtils.isEmpty(exam.getExamName()) && exam.getExamName().contains(lastSearchText)) {
                    examListSearch.add(exam);
                    continue;
                }

                //话题
                List<TopicInfoEntity> topics = exam.getTopics();
                if (ArrayListUtil.isNotEmpty(topics)) {
                    for (TopicInfoEntity topicInfo : topics) {
                        if (!TextUtils.isEmpty(topicInfo.getTopicName()) && topicInfo.getTopicName().contains(lastSearchText)) {
                            examListSearch.add(exam);
                            continue;
                        }

                        //量表
                        if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
                            for (DimensionInfoEntity dimensionInfo : topicInfo.getDimensions()) {
                                if (!TextUtils.isEmpty(dimensionInfo.getDimensionName()) && dimensionInfo.getDimensionName().contains(lastSearchText)) {
                                    examListSearch.add(exam);
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
        if (ArrayListUtil.isEmpty(examList)) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildTopicListByStatus(defaultChildId,topicStatus,
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
            ExamRootEntity examRoot = InjectionWrapperUtil.injectMap(dataMap, ExamRootEntity.class);

            //后台总记录数
            totalCount = examRoot.getTotal();
            List<ExamEntity> dataList = examRoot.getItems();

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

            if (examList == null) {
                examList = new ArrayList<>();
            }
            examList.addAll(dataList);

            //判断是否全部加载结束
            if (examList.size() >= totalCount) {
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

            //初始滚动位置
            if (!TextUtils.isEmpty(initScrollToExamId)) {
                doLocationExamInList(initScrollToExamId);
                initScrollToExamId = null;
                //测试滚动
//                recycleView.scrollToPosition(7);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (ArrayListUtil.isEmpty(examList)) {
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
     * @param e
     */
    protected void onLoadMoreChildTopicListFailed(QSCustomException e) {
        //开启下拉刷新功能
        swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

        if (ArrayListUtil.isEmpty(examList)) {
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
     * 测评集合转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
     *
     * @param examList 测评集合
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> topicInfoEntityToRecyclerMulti(List<ExamEntity> examList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(examList)) {
            resList = new ArrayList<>();
            boolean isFirstInSeminar = true;
            for (ExamEntity exam : examList) {
                //标记是第一个测评
                if (isFirstInSeminar) {
                    exam.setFirstInSeminar(true);
                    isFirstInSeminar = false;
                }
                //设置布局type
                exam.setItemType(ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_EXAM);
                //添加适配器的测评模型
                resList.add(exam);
                //话题
                List<TopicInfoEntity> topicList = exam.getTopics();
                if (ArrayListUtil.isNotEmpty(topicList)) {
                    boolean isFirstInExam = true;
                    //遍历话题列表
                    for (TopicInfoEntity topicInfo : topicList) {
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
                            //模拟footer
//                            DimensionInfoEntity footerDimensionInfo = new DimensionInfoEntity();
//                            footerDimensionInfo.setItemType(ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_SIMULATE_FOOTER);
//                            topicInfo.addSubItem(footerDimensionInfo);
                        }
                    }
                }
            }
        }

        return resList;
    }


    /**
     * 获取共总有多少项（测评、话题、量表）
     *
     * @param examList 测评集合
     * @return 适配recycler的分组数据模型
     */
    protected int getTotalItem(List<ExamEntity> examList) {
        int total = 0;

        if (ArrayListUtil.isNotEmpty(examList)) {
            //测评数量
            total += examList.size();
            for (ExamEntity exam : examList) {
                List<TopicInfoEntity> topicList = exam.getTopics();

                if (ArrayListUtil.isNotEmpty(topicList)) {
                    //话题数量
                    total += topicList.size();
                    //遍历话题列表
                    for (TopicInfoEntity topicInfo : topicList) {
                        List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                        if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                            total += dimensionInfoList.size();
                        }
                    }
                }
            }
        }

        return total;
    }


    /**
     * 操作点击量表
     * @param dimensionInfoEntity
     */
    protected void operateClickDimension(final DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        if( dimensionInfoEntity == null ){
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), "打开测评失败，请稍后再试");
            }
            return;
        }

        //被锁定
        if(dimensionInfoEntity.getIsLocked() == 1){
            //锁定提示
            lockedDimensionTip(dimensionInfoEntity, topicInfoEntity);

        } else {
            //孩子量表对象
            DimensionInfoChildEntity entity = dimensionInfoEntity.getChildDimension();
            //从未开启过的状态
            if (entity == null) {
                //跳转到量表详细页面，传递量表对象和话题对象
                DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                        dimensionInfoEntity, topicInfoEntity,
                        Dictionary.EXAM_STATUS_DOING,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);

            } else {//已经开启过的状态
                //未完成状态
                if(entity.getStatus() == 0){
                    //跳转到量表详细页面，传递量表对象和话题对象
                    DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                            dimensionInfoEntity, topicInfoEntity,
                            Dictionary.EXAM_STATUS_DOING,
                            Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);

                } else {
                    //已完成状态，显示报告
                    showDimensionReport(dimensionInfoEntity);
                }
            }
        }

    }

    /**
     * 显示量表报告
     */
    protected void showDimensionReport(DimensionInfoEntity dimensionInfo) {
//        ToastUtil.showShort(getContext(), "查看该量表报告");
        try {
            new DimensionReportDialog(getContext(), dimensionInfo, null).show();
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
     * @param layoutType 列表布局类型
     */
    protected void changeRecyclerViewLayout(int layoutType) {

        List examList = null;
        //如果话题搜索集合不为空，则赋值话题搜索集合
        if (ArrayListUtil.isNotEmpty(this.examListSearch)) {
            examList = this.examListSearch;
        } else {
            examList = this.examList;
        }

        //线性
        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {
            List<MultiItemEntity> newData = topicInfoEntityToRecyclerMulti(examList);
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

            List<MultiItemEntity> newData = topicInfoEntityToRecyclerMulti(examList);
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
            if (examListSearch != null) {
                examListSearch.clear();
            }

            //空布局为无数据状态，则置为隐藏
            if (emptyLayout.getErrorState() == XEmptyLayout.NO_DATA) {
                //测评集合不为空
                if (ArrayListUtil.isNotEmpty(examList)) {
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                }
            }

        } else {
            //从examList的测评名称、话题名称和量表名称中进行检索
            if (ArrayListUtil.isNotEmpty(examList)) {
                if (ArrayListUtil.isEmpty(examListSearch)) {
                    examListSearch = new ArrayList<>();
                } else {
                    examListSearch.clear();
                }

                for (ExamEntity exam : examList) {
                    if (!TextUtils.isEmpty(exam.getExamName()) && exam.getExamName().contains(searchText)) {
                        examListSearch.add(exam);
                        continue;
                    }
                    //话题
                    List<TopicInfoEntity> topicList = exam.getTopics();
                    if (ArrayListUtil.isNotEmpty(topicList)) {
                        //遍历话题列表
                        for (TopicInfoEntity topicInfo : topicList) {
                            if (!TextUtils.isEmpty(topicInfo.getTopicName()) && topicInfo.getTopicName().contains(searchText)) {
                                examListSearch.add(exam);
                                break;
                            }

                            boolean exist = false;
                            if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
                                for (DimensionInfoEntity dimensionInfo : topicInfo.getDimensions()) {
                                    if (!TextUtils.isEmpty(dimensionInfo.getDimensionName()) && dimensionInfo.getDimensionName().contains(searchText)) {
                                        examListSearch.add(exam);
                                        exist = true;
                                        break;
                                    }
                                }
                            }
                            if (exist) {
                                break;
                            }
                        }
                    }
                }
            }

            //搜索结果空处理
            if (ArrayListUtil.isEmpty(examListSearch)) {
                //备份原先的无数据提示
                String noDataTip = emptyLayout.getNoDataTip();
                //设置新的无数据提示文本
                emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_article_for_search));
                //空布局
                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                //设置原先的无数据提示文本
                emptyLayout.setNoDataTip(noDataTip);

                //调整总数
                if (ArrayListUtil.isNotEmpty(examList)) {
                    totalCount = examList.size();
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
        if (ArrayListUtil.isNotEmpty(examListSearch)) {
            totalCount = examListSearch.size();
        } else {
            if (ArrayListUtil.isNotEmpty(examList)) {
                totalCount = examList.size();
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
                if (ArrayListUtil.isNotEmpty(examList)) {
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
     * 定位到指定测评的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationExamInListNotice(LocationExamInListEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getExamId())) {
            try {
                //已经加载了测评列表
                if (recyclerAdapter.getData().size() > 0) {
                    doLocationExamInList(event.getExamId());
                } else {
                    //赋值列表加载后初始滚动到指定测评
                    initScrollToExamId = event.getExamId();
                }

                //切换到tab页面
//                if (getActivity() != null) {
//                    ((MasterTabActivity) getActivity()).switchToExamTab();
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 定位到指定测评
     * @param examId 测评ID
     */
    private void doLocationExamInList(String examId) {
        if (TextUtils.isEmpty(examId)) {
            return;
        }
        //局部刷新量表对应的视图项
        List<MultiItemEntity> data = recyclerAdapter.getData();
        if (ArrayListUtil.isNotEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                MultiItemEntity item = data.get(i);
                if (item instanceof ExamEntity) {
                    ExamEntity exam = (ExamEntity) item;
                    if (examId.equals(exam.getExamId())) {
                        //滚动到指定位置
                        recycleView.scrollToPosition(i);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 粘性布局监听
     */
    private class StickyRecyclerViewScrollListener extends RecyclerViewScrollListener {
        //粘性布局的分隔布局高度
        int dividerHeight;

        //开始推动粘性视图的阈值
        int transInfoThreshold;

        StickyRecyclerViewScrollListener(Context context, FloatingActionButton fabGotoTop) throws Exception {
            super(context, fabGotoTop);
            dividerHeight = (int) getResources().getDimension(R.dimen.exam_item_vertical_margin);
            transInfoThreshold = 0;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //不能够向下划的时候就隐藏
            if (!recyclerView.canScrollVertically(-1)) {
                stickyHeaderView.setVisibility(View.INVISIBLE);
            } else {
                stickyHeaderView.setVisibility(View.VISIBLE);
            }

            View stickyInfoView = recyclerView.findChildViewUnder(
                    stickyHeaderView.getMeasuredWidth() / 2, 5);
            if (stickyInfoView != null && stickyInfoView.getTag() != null) {
                //测评
                if (stickyInfoView.getTag() instanceof ExamEntity) {
                    refreshStickyHeaderView((ExamEntity) stickyInfoView.getTag());

                } else if (stickyInfoView.getTag() instanceof DimensionInfoEntity) {//最后一个量表
                    DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) stickyInfoView.getTag();
                    if (dimensionInfo.isLastInTopic()) {
                        ExamEntity exam = getBelongToExam(dimensionInfo);
                        refreshStickyHeaderView(exam);
                    }
                }
            }

            View transInfoView = recyclerView.findChildViewUnder(
                    stickyHeaderView.getMeasuredWidth() / 2, stickyHeaderView.getMeasuredHeight() + 1);

            if (transInfoView != null) {
                if (transInfoView.getTag() != null && transInfoView.getTag() instanceof ExamEntity) {

                    ExamEntity exam = (ExamEntity) transInfoView.getTag();
                    int dealtY = transInfoView.getTop() + dividerHeight - stickyHeaderView.getMeasuredHeight();
                    //非第一个测评
                    if (!exam.isFirstInSeminar()) {
                        if (BuildConfig.DEBUG) {
                            System.out.println("dealtY：" + dealtY);
                            System.out.println("实际top：" + transInfoView.getTop());
                            System.out.println("预期top：" + (transInfoView.getTop() + dividerHeight));
                        }
                        if (transInfoView.getTop() + dividerHeight > 0) {
                            if (dealtY < 0) {
                                stickyHeaderView.setTranslationY(dealtY);
                            }
                        } else {
                            stickyHeaderView.setTranslationY(0);
                        }
                    } else {
                        stickyHeaderView.setTranslationY(0);
                    }
                } else {
                    stickyHeaderView.setTranslationY(0);
                }
//                } else if (transViewStatus == StickyExampleAdapter.NONE_STICKY_VIEW) {
//                    tvStickyHeaderView.setTranslationY(0);
//                }
            }
        }
    }


    /**
     * 获取所属测评
     * @param dimension
     * @return
     */
    protected ExamEntity getBelongToExam(DimensionInfoEntity dimension) {
        ExamEntity exam = null;

        if (dimension != null && !TextUtils.isEmpty(dimension.getTopicId())) {
            String topicId = dimension.getTopicId();
            for (ExamEntity tempExam : examList) {
                List<TopicInfoEntity> topics = tempExam.getTopics();
                if (ArrayListUtil.isNotEmpty(topics)) {
                    for (TopicInfoEntity topic : topics) {
                        if (topicId.equals(topic.getTopicId())) {
                            exam = tempExam;
                            break;
                        }
                    }
                }

                if (exam != null) {
                    break;
                }
            }
        }

        return exam;
    }


    SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 刷新粘性布局视图
     * @param exam 测评
     */
    protected void refreshStickyHeaderView(ExamEntity exam) {
        if (exam == null) {
            return;
        }
        //测评名称
        if (!TextUtils.isEmpty(exam.getExamName())) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(exam.getExamName());
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        //有效期
        if (!TextUtils.isEmpty(exam.getStartTime())  && !TextUtils.isEmpty(exam.getEndTime())) {
            //有效期
            String startTime = exam.getStartTime();
            String endTime = exam.getEndTime();//ISO8601 时间字符串
            try {
                Date startDate = formatIso8601.parse(startTime);
                Date endDate = formatIso8601.parse(endTime);
                String startDateStr = formatNormal.format(startDate);
                String endDateStr = formatNormal.format(endDate);
                tvDate.setText(getResources().getString(R.string.exam_date,startDateStr, endDateStr));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //圆形进度条
        try {
            float progress = (float)exam.getCompleteDimensions() / exam.getTotalDimensions() * 100;
            circleProgressBar.setProgress((int) progress);
//                circleProgressBar.setProgress(progress, true); // 使用数字过渡动画
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

