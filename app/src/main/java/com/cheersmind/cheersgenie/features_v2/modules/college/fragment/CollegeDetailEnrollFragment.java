package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeEnrollConstitutionRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.CollegeEnrollScoreRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorEnrollScoreRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.CollegeEnrollScoreDto;
import com.cheersmind.cheersgenie.features_v2.dto.MajorEnrollScoreDto;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollBaseInfo;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollConstitution;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollConstitutionItem;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollOffice;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeScoreCondition;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollScoreItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollScoreKind;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollScoreKindRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollScoreRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeProvince;
import com.cheersmind.cheersgenie.features_v2.entity.MajorEnrollScoreItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorEnrollScoreRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorScoreCondition;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 院校录取信息
 */
public class CollegeDetailEnrollFragment extends LazyLoadFragment {

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

    //招生办信息
    @BindView(R.id.ll_enroll_base_info)
    LinearLayout llEnrollBaseInfo;
    @BindView(R.id.cl_contact)
    ConstraintLayout clContact;
    @BindView(R.id.cl_website)
    ConstraintLayout clWebsite;
    @BindView(R.id.cl_address)
    ConstraintLayout clAddress;
    @BindView(R.id.tv_contact_val)
    TextView tvContactVal;
    @BindView(R.id.tv_website_val)
    TextView tvWebsiteVal;
    @BindView(R.id.tv_address_val)
    TextView tvAddressVal;

    //招生章程
    @BindView(R.id.ll_enroll_constitution)
    LinearLayout llEnrollConstitution;
    @BindView(R.id.recyclerViewEnrollConstitution)
    RecyclerView recyclerViewEnrollConstitution;

    CollegeEnrollConstitutionRecyclerAdapter recyclerAdapter;
    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            MultiItemEntity multiItem = recyclerAdapter.getData().get(position);
            if (multiItem instanceof CollegeEnrollConstitutionItem
                    && getActivity() != null) {
                CollegeEnrollConstitutionItem constitutionItem = (CollegeEnrollConstitutionItem) multiItem;
                ToastUtil.showShort(getActivity().getApplication(),
                        constitutionItem.getName());
            }
        }
    };

    private static final int CONDITION_TYPE_COLLEGE = 1;
    private static final int CONDITION_TYPE_MAJOR = 2;
    int conditionType = CONDITION_TYPE_COLLEGE;

    //院校录取
    @BindView(R.id.ll_college_score)
    LinearLayout llCollegeScore;
    @BindView(R.id.rvCollegeScore)
    RecyclerView rvCollegeScore;
    //无数据提示
    @BindView(R.id.tv_college_enroll_score_empty_tip)
    TextView tvCollegeEnrollScoreEmptyTip;
    //省份筛选器文本
    @BindView(R.id.tv_college_score_province)
    TextView tvCollegeScoreProvince;
    //年份筛选器文本
    @BindView(R.id.tv_college_score_year)
    TextView tvCollegeScoreYear;
    //文科分科筛选器文本
    @BindView(R.id.tv_college_score_kind)
    TextView tvCollegeScoreKind;
    //院校录取分数适配器
    CollegeEnrollScoreRecyclerAdapter collegeEnrollScoreRecyclerAdapter;

    //专业录取
    @BindView(R.id.ll_major_score)
    LinearLayout llMajorScore;
    @BindView(R.id.rvMajorScore)
    RecyclerView rvMajorScore;
    @BindView(R.id.tv_major_enroll_score_empty_tip)
    TextView tvMajorEnrollScoreEmptyTip;
    @BindView(R.id.tv_major_score_province)
    TextView tvMajorScoreProvince;
    @BindView(R.id.tv_major_score_year)
    TextView tvMajorScoreYear;
    @BindView(R.id.tv_major_score_kind)
    TextView tvMajorScoreKind;
    //专业录取分数适配器
    MajorEnrollScoreRecyclerAdapter majorEnrollScoreRecyclerAdapter;

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ArticleDto dto;

    //学校历年录取的筛选条件
    CollegeScoreCondition collegeScoreCondition;
    //专业历年录取的筛选条件
    MajorScoreCondition majorScoreCondition;

    //选择器
    private OptionsPickerView pvProvince, pvYear, pvKind, pvMajorProvince, pvMajorYear, pvMajorKind;
    //省份
    List<CollegeProvince> provinces;
    //年份
    List<String> years;
    //文理分科（院校录取）
    List<CollegeEnrollScoreKind> kindsForCollege;
    //文理分科（专业录取）
    List<CollegeEnrollScoreKind> kindsForMajor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail_enroll;
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
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_enroll));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //初始化为加载状态
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                //加载数据
                loadEnrollBaseInfo();
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        dto = new ArticleDto(pageNum, PAGE_SIZE);

        //设置recyclerView不影响嵌套滚动
        recyclerViewEnrollConstitution.setNestedScrollingEnabled(false);
        rvCollegeScore.setNestedScrollingEnabled(false);
        rvMajorScore.setNestedScrollingEnabled(false);
        //使其失去焦点。
        recyclerViewEnrollConstitution.setFocusable(false);
        rvCollegeScore.setFocusable(false);
        rvMajorScore.setFocusable(false);

        //初始化筛选条件数据
        initConditionData();
        //初始化筛选条件视图
        initConditionView();
        //初始化选择器
        initPickerView();
    }

    /**
     * 初始化筛选条件视图
     */
    private void initConditionView() {
        tvCollegeScoreProvince.setText(provinces.get(0).getName());
        tvCollegeScoreYear.setText(years.get(0));
        tvCollegeScoreKind.setText("文理分科");
    }

    /**
     * 初始化筛选条件数据
     */
    private void initConditionData() {
        provinces = DataSupport.findAll(CollegeProvince.class);
        years = new ArrayList<>();
        years.add("2018");
        years.add("2017");
        years.add("2016");
        years.add("2015");

        collegeScoreCondition = new CollegeScoreCondition();
        collegeScoreCondition.setProvince(provinces.get(0));
        collegeScoreCondition.setYear(years.get(0));

        majorScoreCondition = new MajorScoreCondition();
        majorScoreCondition.setProvince(provinces.get(0));
        majorScoreCondition.setYear(years.get(0));
    }

    /**
     * 初始化选择器
     */
    private void initPickerView() {
        pvProvince = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                CollegeProvince collegeProvince = provinces.get(options1);
                //设置省份筛选器文本
                tvCollegeScoreProvince.setText(collegeProvince.getName());
                //更新年份条件
                collegeScoreCondition.setProvince(collegeProvince);
                //加载文理分科
                loadKind(collegeScoreCondition.getProvince().getName(),
                        collegeScoreCondition.getYear(),
                        CONDITION_TYPE_COLLEGE, true);
            }
        })
                .setTitleText("生源地")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
//                .setBgColor(Color.BLACK)
//                .setTitleBgColor(Color.DKGRAY)
//                .setTitleColor(Color.LTGRAY)
//                .setCancelColor(Color.YELLOW)
//                .setSubmitColor(Color.YELLOW)
//                .setTextColorCenter(Color.LTGRAY)
//                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .setLabels("省", "市", "区")
//                .setOutSideColor(0x00000000) //设置外部遮罩颜色
//                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
//                    @Override
//                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
//                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
//                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
//                    }
//                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        pvProvince.setPicker(provinces);//一级选择器


        pvYear = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String year = years.get(options1);
                //设置年份筛选器文本
                tvCollegeScoreYear.setText(year);
                //更新年份条件
                collegeScoreCondition.setYear(year);
                //加载文理分科
                loadKind(collegeScoreCondition.getProvince().getName(),
                        collegeScoreCondition.getYear(),
                        CONDITION_TYPE_COLLEGE, true);
            }
        })
                .setTitleText("年份")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

        pvYear.setPicker(years);//一级选择器

        pvKind = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                CollegeEnrollScoreKind kind = kindsForCollege.get(options1);
                tvCollegeScoreKind.setText(kind.getKind());
                collegeScoreCondition.setKind(kind);
                loadCollegeEnrollScore(collegeScoreCondition, true);
            }
        })
                .setTitleText("文理分科")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

//        pvYear.setPicker(years);//一级选择器

        pvMajorProvince = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                CollegeProvince collegeProvince = provinces.get(options1);
                //设置省份筛选器文本
                tvMajorScoreProvince.setText(collegeProvince.getName());
                //更新年份条件
                majorScoreCondition.setProvince(collegeProvince);
                //加载文理分科
                loadKind(majorScoreCondition.getProvince().getName(),
                        majorScoreCondition.getYear(),
                        CONDITION_TYPE_MAJOR, true);
            }
        })
                .setTitleText("生源地")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

//        pvOptions.setSelectOptions(1,1);
        pvMajorProvince.setPicker(provinces);//一级选择器


        pvMajorYear = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String year = years.get(options1);
                //设置年份筛选器文本
                tvMajorScoreYear.setText(year);
                //更新年份条件
                majorScoreCondition.setYear(year);
                //加载文理分科
                loadKind(majorScoreCondition.getProvince().getName(),
                        majorScoreCondition.getYear(),
                        CONDITION_TYPE_MAJOR, true);
            }
        })
                .setTitleText("年份")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

        pvMajorYear.setPicker(years);//一级选择器

        pvMajorKind = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                CollegeEnrollScoreKind kind = kindsForMajor.get(options1);
                tvMajorScoreKind.setText(kind.getKind());
                majorScoreCondition.setKind(kind);
                loadMajorEnrollScore(majorScoreCondition, true);
            }
        })
                .setTitleText("文理分科")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

//        pvYear.setPicker(years);//一级选择器
    }

    @Override
    protected void lazyLoad() {
        //加载招生基本信息
        loadEnrollBaseInfo();
        //加载文理科信息（院校录取）
        loadKind(collegeScoreCondition.getProvince().getName(),
                collegeScoreCondition.getYear(),
                CONDITION_TYPE_COLLEGE, false);
        //加载文理科信息（专业录取）
        loadKind(majorScoreCondition.getProvince().getName(),
                majorScoreCondition.getYear(),
                CONDITION_TYPE_MAJOR, false);
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

    @OnClick({R.id.tv_college_score_province, R.id.tv_college_score_year, R.id.tv_college_score_kind,
            R.id.tv_major_score_province, R.id.tv_major_score_year, R.id.tv_major_score_kind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //生源地省份
            case R.id.tv_college_score_province: {
                pvProvince.show();
                break;
            }
            //年份
            case R.id.tv_college_score_year: {
                pvYear.show();
                break;
            }
            //文理科
            case R.id.tv_college_score_kind: {
                if (ArrayListUtil.isNotEmpty(kindsForCollege)) {
//                    pvKind.setPicker(kinds);
                    pvKind.show();
                }
                break;
            }
            //专业生源地省份
            case R.id.tv_major_score_province: {
                pvMajorProvince.show();
                break;
            }
            //专业年份
            case R.id.tv_major_score_year: {
                pvMajorYear.show();
                break;
            }
            //专业文理科
            case R.id.tv_major_score_kind: {
                if (ArrayListUtil.isNotEmpty(kindsForMajor)) {
//                    pvKind.setPicker(kinds);
                    pvMajorKind.show();
                }
                break;
            }
        }
    }

    /**
     * 获取院校录取分数
     */
    private void loadCollegeEnrollScore(CollegeScoreCondition condition, final boolean showLoading) {
        //通信等待提示
        if (showLoading) {
            LoadingView.getInstance().show(getContext(), httpTag);
        }

        CollegeEnrollScoreDto dto = new CollegeEnrollScoreDto(
                condition.getProvince().getName(),
                condition.getYear(),
                condition.getKind().getKind());

        DataRequestService.getInstance().getCollegeEnrollScore(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                //通信等待提示
                if (showLoading) {
                    LoadingView.getInstance().dismiss();
                }
                tvCollegeEnrollScoreEmptyTip.setVisibility(View.VISIBLE);
                rvCollegeScore.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //通信等待提示
                    if (showLoading) {
                        LoadingView.getInstance().dismiss();
                    }
                    tvCollegeEnrollScoreEmptyTip.setVisibility(View.GONE);
                    rvCollegeScore.setVisibility(View.VISIBLE);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CollegeEnrollScoreRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeEnrollScoreRootEntity.class);
                    List<CollegeEnrollScoreItemEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        throw new QSCustomException("无院校录取分数");
                    }

                    List<MultiItemEntity> multiItems = new ArrayList<>();

                    CollegeEnrollScoreItemEntity header = new CollegeEnrollScoreItemEntity();
                    header.setItemType(CollegeEnrollScoreRecyclerAdapter.LAYOUT_TYPE_HEADER);
                    multiItems.add(header);

                    if (collegeEnrollScoreRecyclerAdapter == null) {
                        collegeEnrollScoreRecyclerAdapter = new CollegeEnrollScoreRecyclerAdapter(null);
                        rvCollegeScore.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvCollegeScore.setAdapter(collegeEnrollScoreRecyclerAdapter);
                    }

                    for (int i = 0; i < dataList.size(); i++) {
                        CollegeEnrollScoreItemEntity item = dataList.get(i);
                        if (i / 2 == 0) {
                            item.setItemType(CollegeEnrollScoreRecyclerAdapter.LAYOUT_TYPE_ITEM1);
                        } else {
                            item.setItemType(CollegeEnrollScoreRecyclerAdapter.LAYOUT_TYPE_ITEM2);
                        }
                        multiItems.add(item);
                    }

                    collegeEnrollScoreRecyclerAdapter.setNewData(multiItems);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 获取专业录取分数
     */
    private void loadMajorEnrollScore(MajorScoreCondition condition, final boolean showLoading) {
        //通信等待提示
        if (showLoading) {
            LoadingView.getInstance().show(getContext(), httpTag);
        }

        MajorEnrollScoreDto dto = new MajorEnrollScoreDto(
                condition.getProvince().getName(),
                condition.getYear(),
                condition.getKind().getKind());

        DataRequestService.getInstance().getMajorEnrollScore(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                //通信等待提示
                if (showLoading) {
                    LoadingView.getInstance().dismiss();
                }
                tvMajorEnrollScoreEmptyTip.setVisibility(View.VISIBLE);
                rvMajorScore.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //通信等待提示
                    if (showLoading) {
                        LoadingView.getInstance().dismiss();
                    }
                    tvMajorEnrollScoreEmptyTip.setVisibility(View.GONE);
                    rvMajorScore.setVisibility(View.VISIBLE);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    MajorEnrollScoreRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, MajorEnrollScoreRootEntity.class);
                    List<MajorEnrollScoreItemEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        throw new QSCustomException("无专业录取分数");
                    }

                    List<MultiItemEntity> multiItems = new ArrayList<>();

                    MajorEnrollScoreItemEntity header = new MajorEnrollScoreItemEntity();
                    header.setItemType(MajorEnrollScoreRecyclerAdapter.LAYOUT_TYPE_HEADER);
                    multiItems.add(header);

                    if (majorEnrollScoreRecyclerAdapter == null) {
                        majorEnrollScoreRecyclerAdapter = new MajorEnrollScoreRecyclerAdapter(null);
                        rvMajorScore.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvMajorScore.setAdapter(majorEnrollScoreRecyclerAdapter);
                    }

                    for (int i = 0; i < dataList.size(); i++) {
                        MajorEnrollScoreItemEntity item = dataList.get(i);
                        if (i / 2 == 0) {
                            item.setItemType(MajorEnrollScoreRecyclerAdapter.LAYOUT_TYPE_ITEM1);
                        } else {
                            item.setItemType(MajorEnrollScoreRecyclerAdapter.LAYOUT_TYPE_ITEM2);
                        }
                        multiItems.add(item);
                    }

                    majorEnrollScoreRecyclerAdapter.setNewData(multiItems);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 加载文理科信息
     */
    private void loadKind(String province, String year, final int type, final boolean showLoading) {
        //通信等待提示
        if (showLoading) {
            LoadingView.getInstance().show(getContext(), httpTag);
        }

        DataRequestService.getInstance().getCollegeEnrollKinds(province, year, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                if (ArrayListUtil.isNotEmpty(kindsForCollege)) {
                    kindsForCollege.clear();
                }
                tvCollegeScoreKind.setText("文理分科");
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CollegeEnrollScoreKindRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeEnrollScoreKindRootEntity.class);
                    List<CollegeEnrollScoreKind> dataList = rootEntity.getItems();
                    if (ArrayListUtil.isNotEmpty(dataList)) {
                        //院校录取
                        if (type == CONDITION_TYPE_COLLEGE) {
                            kindsForCollege = dataList;
                            //设置文科分科选筛选器数据
                            pvKind.setPicker(kindsForCollege);
                            //设置文科分科选筛选器文本
                            tvCollegeScoreKind.setText(kindsForCollege.get(0).getKind());
                            //设置条件数据
                            collegeScoreCondition.setKind(kindsForCollege.get(0));
                            loadCollegeEnrollScore(collegeScoreCondition, showLoading);

                        } else if (type == CONDITION_TYPE_MAJOR) {//专业录取
                            kindsForMajor = dataList;
                            //设置文科分科选筛选器数据
                            pvMajorKind.setPicker(kindsForMajor);
                            //设置文科分科选筛选器文本
                            tvMajorScoreKind.setText(kindsForMajor.get(0).getKind());
                            //设置条件数据
                            majorScoreCondition.setKind(kindsForMajor.get(0));
                            loadMajorEnrollScore(majorScoreCondition, showLoading);
                        }

                    } else {
                        throw new QSCustomException("");
                    }

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 加载招生基本信息（招生办信息和招生简章）
     */
    private void loadEnrollBaseInfo() {
        //通信等待提示
//        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getCollegeEnrollBaseInfo(collegeId, new BaseService.ServiceCallback() {
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
                    CollegeEnrollBaseInfo enrollBaseInfo = InjectionWrapperUtil.injectMap(dataMap, CollegeEnrollBaseInfo.class);

                    //空数据处理
                    if (enrollBaseInfo == null) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //刷新子模块视图
                    refreshBlockViews(enrollBaseInfo);

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
     * @param enrollBaseInfo 院校招生基础信息
     */
    private void refreshBlockViews(CollegeEnrollBaseInfo enrollBaseInfo) {
        if (enrollBaseInfo == null) {
            llEnrollBaseInfo.setVisibility(View.GONE);
            return;
        }

        //招生办信息
        CollegeEnrollOffice enrollOffice = enrollBaseInfo.getEnrollOffice();
        if (enrollOffice == null
                || (TextUtils.isEmpty(enrollOffice.getContact_info())
                && TextUtils.isEmpty(enrollOffice.getWebsite())
                && TextUtils.isEmpty(enrollOffice.getAddress()))) {
            llEnrollBaseInfo.setVisibility(View.GONE);

        } else {
            //联系电话
            if (!TextUtils.isEmpty(enrollOffice.getContact_info())) {
                tvContactVal.setText(enrollOffice.getContact_info());

            } else {
                clContact.setVisibility(View.GONE);
            }
            //官网
            if (!TextUtils.isEmpty(enrollOffice.getWebsite())) {
                tvWebsiteVal.setText(enrollOffice.getWebsite());

            } else {
                clWebsite.setVisibility(View.GONE);
            }
            //地址
            if (!TextUtils.isEmpty(enrollOffice.getAddress())) {
                tvAddressVal.setText(enrollOffice.getAddress());

            } else {
                clAddress.setVisibility(View.GONE);
            }
        }

        //招生章程
        List<CollegeEnrollConstitution> constitutions = enrollBaseInfo.getItems();
        if (ArrayListUtil.isNotEmpty(enrollBaseInfo.getItems())) {
            List<MultiItemEntity> dataList = new ArrayList<>();

            for (int i = 0; i < constitutions.size(); i++) {
                CollegeEnrollConstitution constitution = constitutions.get(i);
                List<CollegeEnrollConstitutionItem> constitutionItems = constitution.getItems();

                //标记第一个
                if (i == 0) {
                    constitution.setFirst(true);
                }

                if (ArrayListUtil.isNotEmpty(constitutionItems)) {
                    for (CollegeEnrollConstitutionItem constitutionItem : constitutionItems) {
                        constitution.addSubItem(constitutionItem);
                    }

                    dataList.add(constitution);
                }
            }

            if (dataList.size() > 0) {
                recyclerAdapter = new CollegeEnrollConstitutionRecyclerAdapter(dataList);
                recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
                recyclerViewEnrollConstitution.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewEnrollConstitution.setAdapter(recyclerAdapter);
                recyclerAdapter.expandAll();

            } else {
                llEnrollConstitution.setVisibility(View.GONE);
            }

        } else {
            llEnrollConstitution.setVisibility(View.GONE);
        }

    }

}

