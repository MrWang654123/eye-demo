package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.event.RefreshIntegralEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExamWrapFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExploreFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MineFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ReportFragment;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.features.view.ViewPagerSlide;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.util.PackageUtils;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.util.VersionUpdateUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页面（底部Tab页面）
 */
public class MasterTabActivity extends BaseActivity {

    //viewpager的索引位置
    public static final String VIEWPAGER_POSITION = "viewpager_position";

//    @BindView(R.id.message)
//    TextView message;
    @BindView(R.id.navigationBar)
    BottomNavigationBar navigationBar;

    //内容viewpager
    @BindView(R.id.viewPager)
    ViewPagerSlide viewPager;
    List<Fragment> listFragment;//存储页面对象

    //是否能够退出的标志
    private boolean canExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检查更新
        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
            VersionUpdateUtil.checkUpdate(this,false, false);
        }
    }

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
        //向ViewPager添加各页面
        listFragment = new ArrayList<>();
        //首页改为探索
//        listFragment.add(new HomeFragment());
        listFragment.add(new ExploreFragment());
        listFragment.add(new ExamWrapFragment());
//        listFragment.add(new ReportFragment());//最近使用
        listFragment.add(new MineFragment());
        MyFragAdapter myAdapter = new MyFragAdapter(getSupportFragmentManager(), this, listFragment);
        viewPager.setAdapter(myAdapter);
        viewPager.addOnPageChangeListener(new ViewPageChangeListener());
        //缓存1+3页
        viewPager.setOffscreenPageLimit(3);

        //模式和背景样式
        navigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        //颜色
        navigationBar //值得一提，模式跟背景的设置都要在添加tab前面，不然不会有效果。
                .setActiveColor(R.color.color_777777)//选中颜色 图标和文字
                .setInActiveColor(R.color.color_333333)//默认未选择颜色
                .setBarBackgroundColor(R.color.white);//默认背景色
        //选中监听
        navigationBar.setTabSelectedListener(onTabSelectedListener);
        navigationBar.offsetTopAndBottom(20);
        //添加子项
        navigationBar
                .addItem(new BottomNavigationItem(R.drawable.tab_home_checked,"探索")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_home_normal)))
//                        .setBadgeItem(mShapeBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_exam_checked,"智评")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_exam_normal)))
//                .addItem(new BottomNavigationItem(R.drawable.tab_report_checked,"报告")
//                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_report_normal)))
//                        .setBadgeItem(mTextBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine_checked,"我的")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_mine_normal)))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成


        //测试
        DisplayMetrics metrics = QSApplication.getMetrics();
        Configuration configuration = getResources().getConfiguration();
        System.out.println("MasterTabActivity：测试【DisplayMetrics】【Configuration】");

    }

    @Override
    protected void onInitData() {
        //初始化声音
        SoundPlayUtils.init(this);

        //检查更新
//        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
//            VersionUpdateUtil.checkUpdate(this,false, false);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //如果当前是“我的”，则刷新积分
        if (viewPager.getCurrentItem() == 2) {
            //发送刷新积分通知
            EventBus.getDefault().post(new RefreshIntegralEvent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        //检查更新
//        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
//            VersionUpdateUtil.checkUpdate(this,false, false);
//        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    /**
     * BottomNavigationBar的选中监听
     */
    BottomNavigationBar.OnTabSelectedListener onTabSelectedListener = new BottomNavigationBar.OnTabSelectedListener() {

        @Override
        public void onTabSelected(int position) {
            int index = 0;
            switch (position){
                case 0:
                    index = 0;
                    //修改状态栏颜色
                    setStatusBarColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
                case 1:
                    index = 1;
                    //修改状态栏颜色
                    setStatusBarColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
                case 2:
                    index = 2;
                    //修改状态栏颜色
                    setStatusBarColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
                case 3:
                    index = 3;
                    //修改状态栏颜色
                    setStatusBarColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
            }

            //ViewPager切换页面
            viewPager.setCurrentItem(index);
        }

        @Override
        public void onTabUnselected(int position) {

        }

        @Override
        public void onTabReselected(int position) {

        }
    };


    /**
     * viewpager fragment适配器
     */
    class MyFragAdapter extends FragmentStatePagerAdapter {
        Context context;
        List<Fragment> listFragment;

        MyFragAdapter(FragmentManager fm, Context context, List<Fragment> listFragment) {
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
//            navigation.getMenu().getItem(position).setChecked(true);
            //这里使用navigation.setSelectedItemId(position);无效，
            //setSelectedItemId(position)的官网原句：Set the selected
            // menu item ID. This behaves the same as tapping on an item
            //未找到原因

            //切换底部导航栏
            navigationBar.selectTab(position);

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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //切换tab
        int position = intent.getIntExtra(VIEWPAGER_POSITION, -1);
        if (position >= 0) {
            viewPager.setCurrentItem(position);
        }
//        ToastUtil.showShort(getApplicationContext(), "主页面");
    }


    @Override
    public void onBackPressed() {
        //连按退出处理
        if (!canExit) {
            canExit = true;
            ToastUtil.showShort(MasterTabActivity.this, "再按一次退出");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canExit = false;
                }
            }, 1000);
            return;
        }

        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空toast
        ToastUtil.cancelToast();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //请求安装未知来源的应用的结果
            case PackageUtils.INSTALL_PACKAGES_REQUESTCODE:
                VersionUpdateUtil.handleBackFromRequestInstallPackages(this, grantResults);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //用户被引导至安装未知应用界面返回之后
            case PackageUtils.GET_UNKNOWN_APP_SOURCES: {
                VersionUpdateUtil.handleBackFromUnknownAppSource(this);
                break;
            }
        }
    }


}


