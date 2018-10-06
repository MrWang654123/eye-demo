package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.fragment.HomeFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MessageFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MineFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExamFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ReportFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamCompletedFragment;
import com.cheersmind.cheersgenie.features.utils.BottomNavigationViewHelper;
import com.cheersmind.cheersgenie.main.util.PackageUtils;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.util.VersionUpdateUtil;

import java.lang.reflect.Field;
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
    //底部导航
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.navigationBar)
    BottomNavigationBar navigationBar;

    //内容viewpager
    @BindView(R.id.viewPager)
    ViewPager viewPager;
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
        //清除动画效果
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //向ViewPager添加各页面
        listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment());
        listFragment.add(new ExamFragment());
//        listFragment.add(new MessageFragment());
        listFragment.add(new ReportFragment());
        //由于不需要标题栏，又需要懒加载，所以直接使用它，用ReportFragment嵌套懒加载会失效
//        listFragment.add(new ExamCompletedFragment());
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
                .addItem(new BottomNavigationItem(R.drawable.tab_home_checked,"首页")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_home_normal)))
//                        .setBadgeItem(mShapeBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_exam_checked,"智评")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_exam_normal)))
                .addItem(new BottomNavigationItem(R.drawable.tab_report_checked,"报告")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_report_normal)))
//                        .setBadgeItem(mTextBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine_checked,"我的")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_mine_normal)))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成
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
    protected void onResume() {
        super.onResume();

//        //检查更新
//        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
//            VersionUpdateUtil.checkUpdate(this,false, false);
//        }
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
//            navigation.getMenu().getItem(position).setChecked(true);
            //这里使用navigation.setSelectedItemId(position);无效，
            //setSelectedItemId(position)的官网原句：Set the selected
            // menu item ID. This behaves the same as tapping on an item
            //未找到原因

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

        int position = intent.getIntExtra(VIEWPAGER_POSITION, -1);
        if (position >= 0) {
            viewPager.setCurrentItem(position);
        }
//        ToastUtil.showShort(getApplicationContext(), "主页面");
    }


    @Override
    public void onBackPressed() {

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


