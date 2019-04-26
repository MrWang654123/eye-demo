package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabViewPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.HistoryReportFragment;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamReportFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskDetailFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 测评报告页
 */
public class ExamReportActivity extends BaseActivity {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    //切换按钮
    @BindView(R.id.tv_switch)
    TextView tvSwitch;

    /**
     * 启动测评报告页
     * @param context 上下文
     * @param dto 报告dto
     */
    public static void startExamReportActivity(Context context, ExamReportDto dto) {
        Intent intent = new Intent(context, ExamReportActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DtoKey.EXAM_REPORT_DTO, dto);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_exam_report;
    }

    @Override
    protected String settingTitle() {
        return "报告";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        ExamReportDto dto = (ExamReportDto) getIntent().getSerializableExtra(DtoKey.EXAM_REPORT_DTO);
        //设置title
        if (tvToolbarTitle != null
                && !TextUtils.isEmpty(dto.getRelationName())) {
            tvToolbarTitle.setText(dto.getRelationName());
        }

        //title、Fragment组合对象的集合
        List<Pair<String, Fragment>> items = new ArrayList<>();
        ExamReportFragment examReportFragment = new ExamReportFragment();
        //添加初始数据
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtoKey.EXAM_REPORT_DTO, dto);
        examReportFragment.setArguments(bundle);

        items.add(new Pair<String, Fragment>("最新测评", examReportFragment));

        //话题报告才有历史记录
        if (dto != null && Dictionary.REPORT_TYPE_TOPIC.equals(dto.getRelationType())) {
            tvSwitch.setVisibility(View.VISIBLE);

            //往期记录
            HistoryReportFragment historyReportFragment = new HistoryReportFragment();
            historyReportFragment.setArguments(bundle);
            items.add(new Pair<String, Fragment>("往期记录", historyReportFragment));

        } else {
            tvSwitch.setVisibility(View.GONE);
        }

        viewPager.setAdapter(new TabViewPagerAdapter(getSupportFragmentManager(), items));
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //切换开关文本
                changeSwitchText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        String tag = ExamReportFragment.class.getSimpleName();
//        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
//        //空则添加
//        if (fragmentByTag == null) {
//            ExamReportFragment fragment = new ExamReportFragment();
//            //添加初始数据
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(DtoKey.EXAM_REPORT_DTO, dto);
//            fragment.setArguments(bundle);
//            //添加已完成的测评fragment到容器中
//            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
//        }
    }

    @OnClick(R.id.tv_switch)
    public void onViewClicked() {
        int curPosition = viewPager.getCurrentItem();
        if (curPosition == 0) {
            curPosition = 1;
        } else if (curPosition == 1) {
            curPosition = 0;
        }

        //切换开关文本
        changeSwitchText(curPosition);
        viewPager.setCurrentItem(curPosition);
    }

    /**
     * 切换开关文本
     * @param curPosition 当前索引
     */
    private void changeSwitchText(int curPosition) {
        if (curPosition == 0) {
            tvSwitch.setText("往期记录");
        } else if (curPosition == 1) {
            tvSwitch.setText("最新测评");
        }
    }

}
