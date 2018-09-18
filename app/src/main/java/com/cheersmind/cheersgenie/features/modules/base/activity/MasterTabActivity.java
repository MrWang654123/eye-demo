package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.fragment.HomeFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MessageFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MineFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExamFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ReportFragment;
import com.cheersmind.cheersgenie.features.utils.BottomNavigationViewHelper;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页面（底部Tab页面）
 */
public class MasterTabActivity extends BaseActivity {
//    @BindView(R.id.message)
//    TextView message;
    //底部导航
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    //内容viewpager
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    List<Fragment> listFragment;//存储页面对象

    @Override
    protected int setContentView() {
        return R.layout.activity_master_tab;
    }

    @Override
    protected String settingTitle() {
        return null;
    }

    /**
     * 初始化视图
     */
    @Override
    protected void onInitView() {
        //ButterKnife绑定页面
        ButterKnife.bind(this);
        //清除动画效果
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //向ViewPager添加各页面
        listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment());
        listFragment.add(new ExamFragment());
//        listFragment.add(new MessageFragment());
        listFragment.add(new ReportFragment());
        listFragment.add(new MineFragment());
        MyFragAdapter myAdapter = new MyFragAdapter(getSupportFragmentManager(), this, listFragment);
        viewPager.setAdapter(myAdapter);
        viewPager.addOnPageChangeListener(new ViewPageChangeListener());
        //缓存1+3页
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void onInitData() {
        //初始化声音
        SoundPlayUtils.init(this);
    }


    /**
     * 底部tab选中监听
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //重置导航默认图标
            resetToDefaultIcon();
            switch (item.getItemId()) {
                case R.id.navigation_home://首页
                    viewPager.setCurrentItem(0);
                    //在这里替换图标
                    item.setIcon(R.drawable.tab_home_checked);
                    return true;
                case R.id.navigation_exam://测评
                    viewPager.setCurrentItem(1);
                    //在这里替换图标
                    item.setIcon(R.drawable.tab_exam_checked);
                    return true;
                case R.id.navigation_report://报告
                    viewPager.setCurrentItem(2);
                    //在这里替换图标
                    item.setIcon(R.drawable.tab_report_checked);
                    return true;
//                case R.id.navigation_message://消息
//                    viewPager.setCurrentItem(2);
//                    return true;
                case R.id.navigation_mine://我的
                    viewPager.setCurrentItem(3);
                    //在这里替换图标
                    item.setIcon(R.drawable.tab_mine_checked);
                    return true;
            }
            return false;
        }
    };

    /**
     * 重置导航默认图标
     */
    private void resetToDefaultIcon() {
        MenuItem home =  navigation.getMenu().findItem(R.id.navigation_home);
        home.setIcon(R.drawable.tab_home_normal);
        MenuItem exam =  navigation.getMenu().findItem(R.id.navigation_exam);
        exam.setIcon(R.drawable.tab_exam_normal);
        MenuItem report =  navigation.getMenu().findItem(R.id.navigation_report);
        report.setIcon(R.drawable.tab_report_normal);
        MenuItem mine =  navigation.getMenu().findItem(R.id.navigation_mine);
        mine.setIcon(R.drawable.tab_mine_normal);
    }

    /**
     * viewpager fragment适配器
     */
    class MyFragAdapter extends FragmentStatePagerAdapter {
        Context context;
        List<Fragment> listFragment;

        public MyFragAdapter(FragmentManager fm, Context context, List<Fragment> listFragment) {
            super(fm);
            this.context = context;
            this.listFragment = listFragment;
        }

        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }
    }

    /**
     * viewpager 页面切换监听
     */
    class ViewPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //注意这个方法滑动时会调用多次，下面是参数解释：
            //position当前所处页面索引,滑动调用的最后一次绝对是滑动停止所在页面
            //positionOffset:表示从位置的页面偏移的[0,1]的值。
            //positionOffsetPixels:以像素为单位的值，表示与位置的偏移
        }

        @Override
        public void onPageSelected(int position) {
            //该方法只在滑动停止时调用，position滑动停止所在页面位置
            // 当滑动到某一位置，导航栏对应位置被按下
            navigation.getMenu().getItem(position).setChecked(true);
            //这里使用navigation.setSelectedItemId(position);无效，
            //setSelectedItemId(position)的官网原句：Set the selected
            // menu item ID. This behaves the same as tapping on an item
            //未找到原因
        }

        @Override
        public void onPageScrollStateChanged(int state) {
//                这个方法在滑动是调用三次，分别对应下面三种状态
//                这个方法对于发现用户何时开始拖动，
//                何时寻呼机自动调整到当前页面，或何时完全停止/空闲非常有用。
//                state表示新的滑动状态，有三个值：
//                SCROLL_STATE_IDLE：开始滑动（空闲状态->滑动），实际值为0
//                SCROLL_STATE_DRAGGING：正在被拖动，实际值为1
//                SCROLL_STATE_SETTLING：拖动结束,实际值为2
        }
    }

    /**
     * 切换到报告页面
     */
    public void switchToReportPage() {
        viewPager.setCurrentItem(2, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

//        ToastUtil.showShort(getApplicationContext(), "主页面");
    }

}
