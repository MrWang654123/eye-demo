package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.TrackRecordRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordEntity;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordRootEntity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业详情基本信息
 */
public class MajorDetailInfoFragment extends LazyLoadFragment {

    Unbinder unbinder;
    //专业ID
    private String majorId;

    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;
    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;
    //可伸缩文本
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandableTextView;
    //伸缩提示文本
    @BindView(R.id.tv_expandable_tip)
    TextView tvExpandableTip;
    @BindView(R.id.expand_collapse)
    ImageButton iBtnExpandCollapse;

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ArticleDto dto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_major_detail_info;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            majorId = bundle.getString(DtoKey.MAJOR_ID);
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_major_detail_info));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载数据
                loadData();
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        dto = new ArticleDto(pageNum, PAGE_SIZE);

        //提示文本点击监听
        tvExpandableTip.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                iBtnExpandCollapse.performClick();
            }
        });
        //伸缩监听
        expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if (isExpanded) {
                    tvExpandableTip.setText("收起全部");
                } else {
                    tvExpandableTip.setText("展开全部");
                }
            }
        });
        expandableTextView.setText("• 专业简介\n" +
                "哲学是人文科学领域内的基础学科，是对基本和普遍之问题的研究。在希腊文中，哲学是爱智慧的意思。学哲学，就是学习智慧。哲学的爱智，无论是对自然的惊讶，还是认识人自己，都不仅仅是一种对知识的追求，更重要的是一种对生活意义的关切，对生活境界的陶冶。哲学，是使人崇高起来的学问。哲学的爱智，还是一种反思的、批判的思想活动，它要追究各种知识的根据，思考历史进步的尺度，询问真善美的标准，探索生活信念的前提。\n" +
                "• 培养目标\n" +
                "哲学专业是培养具有一定马克思主义哲学理论素养和系统的专业基础知识，能运用科学的世界观和方法论分析当代世界与中国的现实问题的应用型、复合型高级专门人才的学科\n" +
                "• 培养要求\n" +
                "他们具有一定的哲学理论思维能力、创新能力、口头与文字表达能力、社会活动能力和一定的科研能力，具有较高外语水平的理论研究人才以及能在国家机关、文教事业新闻出版、企业等部门从事实际工作。\n" +
                "• 名人学者\n" +
                "老子、庄子、孔子、苏格拉底、柏拉图、歌德等。");
    }

    @Override
    protected void lazyLoad() {
        //加载数据
        loadData();
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

    /**
     * 停止Fling的消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopFlingNotice(StopFlingEvent event) {
        if (nsvMain != null) {
//            nsvMain.stopNestedScroll();
            nsvMain.stopNestedScroll(1);
        }
    }


    /**
     * 加载数据
     */
    private void loadData() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        try {
            dto.setPage(pageNum);
            DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    //空布局：网络连接有误，或者请求失败
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(Object obj) {
                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    try {
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        TrackRecordRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, TrackRecordRootEntity.class);

                        totalCount = rootEntity.getTotal();
                        List<TrackRecordEntity> dataList = rootEntity.getItems();

                        //空数据处理
                        if (ArrayListUtil.isEmpty(dataList)) {
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                            return;
                        }

                        //页码+1
                        pageNum++;

                    } catch (Exception e) {
                        e.printStackTrace();
                        //空布局：加载失败
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    }
                }
            }, httpTag, getActivity());

        } catch (Exception e) {
            e.printStackTrace();
            //空布局：网络连接有误，或者请求失败
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
        }
    }


}

