package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeFacultyStrengthRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeRankResultRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeDetailInfo;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeGenderRadio;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankResultItem;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeStudentData;
import com.cheersmind.cheersgenie.features_v2.entity.FacultyStrengthItem;
import com.cheersmind.cheersgenie.features_v2.entity.FacultyStrengthItemChild;
import com.cheersmind.cheersgenie.features_v2.view.CircleScaleView;
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
 * 院校详情基本信息
 */
public class CollegeDetailInfoFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //院校ID
    private String collegeId;

    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;
    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //简介
    @BindView(R.id.ll_introduce)
    LinearLayout ll_introduce;
    //可伸缩文本
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandableTextView;

    //院校排名
    @BindView(R.id.ll_rank)
    LinearLayout ll_rank;
    @BindView(R.id.recyclerViewRank)
    RecyclerView recyclerViewRank;

    //师资力量
    @BindView(R.id.ll_faculty_strength)
    LinearLayout ll_faculty_strength;
    @BindView(R.id.etv_faculty_strength)
    ExpandableTextView etv_faculty_strength;
    @BindView(R.id.recyclerViewFacultyStrength)
    RecyclerView recyclerViewFacultyStrength;

    //学生概括
    @BindView(R.id.ll_student_summary)
    LinearLayout llStudentSummary;
    //在校生数据
    @BindView(R.id.ll_student_data)
    LinearLayout llStudentData;
    @BindView(R.id.tv_count_item1)
    TextView tvCountItem1;
    @BindView(R.id.tv_count_item2)
    TextView tvCountItem2;
    @BindView(R.id.tv_count_item3)
    TextView tvCountItem3;
    @BindView(R.id.csv_student_data)
    CircleScaleView csvStudentData;
    //男女比例
    @BindView(R.id.ll_gender_ratio)
    LinearLayout llGenderRatio;
    @BindView(R.id.tv_male_ratio)
    TextView tvMaleRatio;
    @BindView(R.id.tv_female_ratio)
    TextView tvFemaleRatio;
    @BindView(R.id.csv_gender_ratio)
    CircleScaleView csvGenderRatio;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail_info;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            collegeId = bundle.getString(DtoKey.COLLEGE_ID);
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_info));
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

        //设置recyclerView不影响嵌套滚动
        recyclerViewRank.setNestedScrollingEnabled(false);
        //使其失去焦点。
        recyclerViewRank.setFocusable(false);

        //设置recyclerView不影响嵌套滚动
        recyclerViewFacultyStrength.setNestedScrollingEnabled(false);
        //使其失去焦点。
        recyclerViewFacultyStrength.setFocusable(false);
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
     *
     * @param event 事件
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

        DataRequestService.getInstance().getCollegeDetailInfo(collegeId, new BaseService.ServiceCallback() {
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
                    CollegeDetailInfo collegeDetailInfo = InjectionWrapperUtil.injectMap(dataMap, CollegeDetailInfo.class);

                    //空数据处理
                    if (collegeDetailInfo == null) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //刷新子模块视图
                    refreshBlockViews(collegeDetailInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                    //空布局：加载失败
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 刷新子模块视图
     *
     * @param collegeDetailInfo 院校详情信息
     */
    private void refreshBlockViews(CollegeDetailInfo collegeDetailInfo) {
        if (collegeDetailInfo == null) return;

        //简介
        if (!TextUtils.isEmpty(collegeDetailInfo.getBrief_introduction())) {
            expandableTextView.setText(collegeDetailInfo.getBrief_introduction());

        } else {
            ll_introduce.setVisibility(View.GONE);
        }

        //院校排名
        List<CollegeRankResultItem> ranking = collegeDetailInfo.getRanking();
        if (ArrayListUtil.isNotEmpty(ranking)) {
            CollegeRankResultRecyclerAdapter recyclerAdapter = new CollegeRankResultRecyclerAdapter(
                    R.layout.recycleritem_college_rank_result, ranking);
            recyclerViewRank.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewRank.setAdapter(recyclerAdapter);

        } else {
            ll_rank.setVisibility(View.GONE);
        }

        //师资力量
        if (collegeDetailInfo.getFaculty_strength() != null) {
            //介绍
            if (!TextUtils.isEmpty(collegeDetailInfo.getFaculty_strength().getSummary())) {
                etv_faculty_strength.setText(collegeDetailInfo.getFaculty_strength().getSummary());
            } else {
                etv_faculty_strength.setVisibility(View.GONE);
            }

            //师资力量数据（博士点等）
            List<FacultyStrengthItem> facultyStrengthItems = collegeDetailInfo.getFaculty_strength().getItems();
            if (ArrayListUtil.isNotEmpty(facultyStrengthItems)) {
                FacultyStrengthItem facultyStrengthItem = facultyStrengthItems.get(0);
                List<FacultyStrengthItemChild> itemsChild = facultyStrengthItem.getItems();
                //子项集合
                if (ArrayListUtil.isNotEmpty(itemsChild)) {
                    CollegeFacultyStrengthRecyclerAdapter recyclerAdapter = new CollegeFacultyStrengthRecyclerAdapter(
                            R.layout.recycleritem_college_faculty_strength, itemsChild);
                    recyclerViewFacultyStrength.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    recyclerViewFacultyStrength.setAdapter(recyclerAdapter);

                } else {
                    recyclerViewFacultyStrength.setVisibility(View.GONE);
                }
            }

        } else {
            ll_faculty_strength.setVisibility(View.GONE);
        }

        //学生概括
        if (collegeDetailInfo.getGenderRadio() == null && collegeDetailInfo.getStudentData() == null) {
            llStudentSummary.setVisibility(View.GONE);

        } else {
            llStudentSummary.setVisibility(View.VISIBLE);
            //在校生数据
            CollegeStudentData studentData = collegeDetailInfo.getStudentData();
            if (studentData != null) {
                tvCountItem1.setText(getString(R.string.undergraduate_count, String.valueOf(studentData.getUndergraduate_students())));
                tvCountItem2.setText(getString(R.string.postgraduate_count, String.valueOf(studentData.getPostgraduates_students())));
                tvCountItem3.setText(getString(R.string.international_count, String.valueOf(studentData.getInternational_students())));
                //比例圆环
                csvStudentData.setCirclePercent(
                        studentData.getUndergraduate_students(),
                        studentData.getPostgraduates_students(),
                        studentData.getInternational_students());

            } else {
                llStudentData.setVisibility(View.GONE);
            }

            //男女比例
            CollegeGenderRadio genderRadio = collegeDetailInfo.getGenderRadio();
            if (genderRadio != null) {
                tvMaleRatio.setText(getString(R.string.male_ratio, genderRadio.getMen_ratio() * 100 + "%"));
                tvFemaleRatio.setText(getString(R.string.female_ratio, genderRadio.getWomen_ratio() * 100 + "%"));
                //比例圆环
                csvGenderRatio.setCirclePercent((float)genderRadio.getMen_ratio(), (float)genderRadio.getWomen_ratio());

            } else {
                llGenderRatio.setVisibility(View.GONE);
            }
        }

    }

}

