package com.cheersmind.cheersgenie.features.modules.exam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabViewPagerAdapter;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamCompletedFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamDoingFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.HistoryReportFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.LastReportFragment;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 报告页面（测评结果）
 */
public class ReportActivity extends BaseActivity {

    private static final String TOPIC_INFO = "topic_info";
    //话题
    private TopicInfoEntity topicInfo;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    //切换按钮
    @BindView(R.id.tv_switch)
    TextView tvSwitch;


    /**
     * 启动报告页面
     * @param context
     * @param topicInfoEntity
     */
    public static void startReportActivity(Context context, TopicInfoEntity topicInfoEntity) {
        Intent intent = new Intent(context, ReportActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(TOPIC_INFO, topicInfoEntity);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_report;
    }

    @Override
    protected String settingTitle() {
        return "测评结果";
    }

    @Override
    protected void onInitView() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(ReportActivity.this, "数据传递有误");
            //关闭页面
            finish();
            return;
        }
        topicInfo = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);

        //话题数据包
        Bundle bundle = new Bundle();
        bundle.putSerializable(TOPIC_INFO, topicInfo);

        //最新测评
        LastReportFragment lastReportFragment = new LastReportFragment();
        lastReportFragment.setArguments(bundle);
        //往期记录
        HistoryReportFragment historyReportFragment = new HistoryReportFragment();
        historyReportFragment.setArguments(bundle);

        //title、Fragment组合对象的集合
        List<Pair<String, Fragment>> items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("最新测评", lastReportFragment));
        items.add(new Pair<String, Fragment>("往期记录", historyReportFragment));
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
    }

    @Override
    protected void onInitData() {

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
     * @param curPosition
     */
    private void changeSwitchText(int curPosition) {
        if (curPosition == 0) {
            tvSwitch.setText("往期记录");
        } else if (curPosition == 1) {
            tvSwitch.setText("最新测评");
        }
    }

}
