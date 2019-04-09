package com.cheersmind.cheersgenie.features_v2.modules.occupation.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationSuitMajorRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationDetail;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationIntroduce;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;
import com.cheersmind.cheersgenie.features_v2.interfaces.AttentionBtnCtrlListener;
import com.cheersmind.cheersgenie.features_v2.modules.major.activity.MajorDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 行业详情
 */
public class OccupationDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //行业
    private OccupationItem occupation;

//    @BindView(R.id.nsv_main)
//    NestedScrollView nsvMain;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //行业名称
    @BindView(R.id.tv_occupation_name)
    TextView tvOccupationName;
    //行业领域
    @BindView(R.id.tv_realm_val)
    TextView tvRealmVal;
    //所属门类
    @BindView(R.id.tv_category_val)
    TextView tvCategoryVal;

    @BindView(R.id.ll_introduce)
    LinearLayout llIntroduce;
    @BindView(R.id.ll_suit_major)
    LinearLayout llSuitMajor;

    //可伸缩文本
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandableTextView;
    @BindView(R.id.expand_collapse)
    ImageButton iBtnExpandCollapse;

    //对口专业列表
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    OccupationSuitMajorRecyclerAdapter recyclerAdapter;

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            MajorItem entity = recyclerAdapter.getData().get(position);
            MajorDetailActivity.startMajorDetailActivity(getContext(), entity);
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_occupation_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            occupation = (OccupationItem) bundle.getSerializable(DtoKey.OCCUPATION);
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_major_detail_info));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //初始化为加载状态
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                //加载行业详情
                if (occupation != null && occupation.getOccupation_id() > 0) {
                    loadOccupationDetail(occupation.getOccupation_id());
                }
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //设置recyclerView不影响嵌套滚动
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void lazyLoad() {
        //加载行业详情
        if (occupation != null && occupation.getOccupation_id() > 0) {
            loadOccupationDetail(occupation.getOccupation_id());
        } else {
            //空布局：无数据
            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 加载行业详情
     *
     * @param occupationId 行业Id
     */
    private void loadOccupationDetail(long occupationId) {

        DataRequestService.getInstance().getOccupationDetail(occupationId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    final OccupationDetail occupationDetail = InjectionWrapperUtil.injectMap(dataMap, OccupationDetail.class);

                    //设置基本信息
                    settingBaseInfo(occupationDetail);
                    //初始化块视图
                    initBlockViews(occupationDetail);
                    //调用关注回调
                    if (getActivity() != null && getActivity() instanceof AttentionBtnCtrlListener) {
                        ((AttentionBtnCtrlListener) getActivity()).ctrlStatus(occupationDetail.isFollow());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //视为找不到文章数据
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag, getContext());
    }

    /**
     * 设置基本信息
     * @param occupationDetail 行业详情
     */
    private void settingBaseInfo(OccupationDetail occupationDetail) {
        tvOccupationName.setText(occupationDetail.getOccupation_name());
        tvRealmVal.setText(occupationDetail.getRealm());
        tvCategoryVal.setText(occupationDetail.getCategory());
    }

    /**
     * 初始化块视图
     * @param occupationDetail 行业详情
     */
    private void initBlockViews(OccupationDetail occupationDetail) {
        if (occupationDetail == null) return;

        //简介
        List<OccupationIntroduce> introduces = occupationDetail.getIntroduces();
        if (ArrayListUtil.isNotEmpty(introduces)) {
            StringBuilder builder = new StringBuilder();
            for (int i=0; i<introduces.size(); i++) {
                OccupationIntroduce introduce = introduces.get(i);

                builder.append("<b>");
                builder.append(introduce.getTitle());
                builder.append("</b><br/>");
                builder.append(introduce.getContent());
                if (i != introduces.size() - 1) {
                    builder.append("<br/>");
                }
            }

            if (builder.length() > 0) {
                expandableTextView.setText(Html.fromHtml(builder.toString()));
            }
        } else {
            llIntroduce.setVisibility(View.GONE);
        }

        //对口专业
        if (ArrayListUtil.isNotEmpty(occupationDetail.getSuitMajors())) {
            recyclerAdapter = new OccupationSuitMajorRecyclerAdapter(
                    R.layout.recycleritem_major_suit_occupation, occupationDetail.getSuitMajors());
            recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(recyclerAdapter);

        } else {
            llSuitMajor.setVisibility(View.GONE);
        }
    }

}

