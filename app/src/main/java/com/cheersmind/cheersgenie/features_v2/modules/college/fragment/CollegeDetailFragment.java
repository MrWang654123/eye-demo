package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeBasicInfo;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 院校详情
 */
public class CollegeDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;
    //院校
    private CollegeEntity college;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.iv_main)
    SimpleDraweeView ivMain;
    @BindView(R.id.tv_college_name)
    TextView tvCollegeName;

    @BindView(R.id.tv_tag0)
    TextView tvTag0;
    @BindView(R.id.tv_tag1)
    TextView tvTag1;
    @BindView(R.id.tv_tag2)
    TextView tvTag2;
    @BindView(R.id.tv_public)
    TextView tvPublic;
    @BindView(R.id.tv_level)
    TextView tvLevel;

    @BindView(R.id.tv_tag)
    TextView tv_tag;

    //最大的质量标签数量
    private static final int MAX_QUALITY_TAG_COUNT = 3;


    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            college = (CollegeEntity) bundle.getSerializable(DtoKey.COLLEGE);
        }

        List<Pair<String, Fragment>> items = new ArrayList<>();

        Bundle bundle1 = new Bundle();
        bundle1.putString(DtoKey.COLLEGE_ID, college.getId());
        bundle1.putString(DtoKey.COLLEGE_NAME, college.getCn_name());

        CollegeDetailInfoFragment fragment1 = new CollegeDetailInfoFragment();
        fragment1.setArguments(bundle1);

        CollegeDetailEnrollFragment fragment2 = new CollegeDetailEnrollFragment();
        fragment2.setArguments(bundle1);

        CollegeDetailMajorWrapFragment fragment3 = new CollegeDetailMajorWrapFragment();
        fragment3.setArguments(bundle1);

        CollegeDetailGraduationFragment fragment4 = new CollegeDetailGraduationFragment();
        fragment4.setArguments(bundle1);

        items.add(new Pair<String, Fragment>("概况", fragment1));
        items.add(new Pair<String, Fragment>("录取招生", fragment2));
        items.add(new Pair<String, Fragment>("专业", fragment3));
        items.add(new Pair<String, Fragment>("毕业信息", fragment4));

        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        viewPager.setOffscreenPageLimit(3);
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    //发送停止Fling的事件
                    EventBus.getDefault().post(new StopFlingEvent());
                }

            }
        });

        //基本信息
        ivMain.setImageURI(college.getLogo_url());
        tvCollegeName.setText(college.getCn_name());

        //标签
        if (college.getBasicInfo().getInstitute_quality() == null
                || college.getBasicInfo().getInstitute_quality().size() == 0) {
            tvTag0.setVisibility(View.GONE);
            tvTag1.setVisibility(View.GONE);
            tvTag2.setVisibility(View.GONE);
        } else {
            //设置标签
            int tagLength = college.getBasicInfo().getInstitute_quality().size();
            for (int i = 0; i < tagLength; i++) {
                String tag = college.getBasicInfo().getInstitute_quality().get(i);

                if (i == 0) {
                    tvTag0.setVisibility(View.VISIBLE);
                    tvTag0.setText(tag);
                } else if (i == 1) {
                    tvTag1.setVisibility(View.VISIBLE);
                    tvTag1.setText(tag);
                } else if (i == 2) {
                    tvTag2.setVisibility(View.VISIBLE);
                    tvTag2.setText(tag);
                }
            }

            //剩余未被占用的TextView数量
            int remainSize = MAX_QUALITY_TAG_COUNT - tagLength;
            if (remainSize > 0) {
                for (int i = remainSize - 1; i >= 0; i--) {
                    if (i == 0) {
                        tvTag0.setVisibility(View.GONE);
                    } else if (i == 1) {
                        tvTag1.setVisibility(View.GONE);
                    } else if (i == 2) {
                        tvTag2.setVisibility(View.GONE);
                    }
                }
            }
        }

        //公立私立
        if ("public".equals(college.getBasicInfo().getPublic_or_private())) {
            tvPublic.setText("公立");
        } else {
            tvPublic.setText("私立");
        }

        //学历层级
        tvLevel.setText(college.getChina_degree());
//        helper.setText(R.id.tv_category, item.getBasicInfo().getInstitute_type());

        //基础属性
        List<String> tags = new ArrayList<>();
        CollegeBasicInfo basicInfo = college.getBasicInfo();

        //所在地
        String location = "";
        //省份
        if (!TextUtils.isEmpty(college.getState())) {
            location += college.getState();
            //城市
            if (!TextUtils.isEmpty(college.getCity_data())
                    && !college.getState().equals(college.getCity_data())) {
                location += ("-" + college.getCity_data());
            }
        }
        if (!TextUtils.isEmpty(location)) {
            tags.add(location);
        }

        //院校类型
        if (!TextUtils.isEmpty(basicInfo.getInstitute_type())) {
            tags.add(basicInfo.getInstitute_type());
        }

        //院校所属
        if (!TextUtils.isEmpty(basicInfo.getChina_belong_to())) {
            tags.add(basicInfo.getChina_belong_to());
        }

        StringBuilder builder = new StringBuilder();
        for (String tag : tags) {
            builder.append(tag);
            builder.append(" | ");
        }

        String substring = builder.substring(0, builder.length() - 3);
        tv_tag.setText(substring);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

