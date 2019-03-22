package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.ExamEntity;
import com.cheersmind.cheersgenie.features.entity.SeminarEntity;
import com.cheersmind.cheersgenie.features.entity.SeminarRootEntity;
import com.cheersmind.cheersgenie.features.event.LocationExamInListEvent;
import com.cheersmind.cheersgenie.features.event.RefreshIntegralEvent;
import com.cheersmind.cheersgenie.features.event.RefreshTaskListEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExploreFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MineFragment;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineExamDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.PermissionUtil;
import com.cheersmind.cheersgenie.features.view.ViewPagerSlide;
import com.cheersmind.cheersgenie.features.view.dialog.TaskListDialog;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEduLevel;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEduLevelRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeProvince;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeProvinceRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankItem;
import com.cheersmind.cheersgenie.features_v2.modules.base.fragment.CareerPlanWrapFragment;
import com.cheersmind.cheersgenie.features_v2.modules.base.fragment.DiscoverFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.PackageUtils;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.util.VersionUpdateUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主页面（底部Tab页面）
 */
public class MasterTabActivity extends BaseActivity {

    //viewpager的索引位置
    public static final String VIEWPAGER_POSITION = "viewpager_position";

    //内容容器
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

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

    //任务说明按钮
    @BindView(R.id.fabTask)
    FloatingActionButton fabTask;

    //是否已经第一次访问过了任务列表
    private boolean hasFirstShowTaskList;
    //任务弹窗偏移
//    int offset_x;
//    int offset_y;
    //专题
    private SeminarEntity seminarEntity;
    //测评集合
    private List<ExamEntity> exams;
    //翻页监听
    ViewPageChangeListener pageChangeListener = new ViewPageChangeListener();
    //任务列表监听
    TaskListDialog.OnOperationListener onOperationListener =  new TaskListDialog.OnOperationListener() {
        @Override
        public void onOnClickItem(int position, final String examId, int examStatus, String examName) {
            switch (examStatus) {
                //测评未开始
                case Dictionary.EXAM_STATUS_INACTIVE: {
                    ToastUtil.showShort(getApplication(), getResources().getString(R.string.exam_inactive_tip));
                    break;
                }
                //测评已结束
                case Dictionary.EXAM_STATUS_OVER: {
                    //跳转历史测评明细页面
                    MineExamDetailActivity.startMineExamDetailActivity(MasterTabActivity.this, examId, examStatus, examName);
                    break;
                }
                //测评正在进行中
                case Dictionary.EXAM_STATUS_DOING: {
                    if (!TextUtils.isEmpty(examId)) {
                        //切换到测评tab
                        switchToExamTab();
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //发送定位测评的事件
                                EventBus.getDefault().post(new LocationExamInListEvent(examId));
                            }
                        }, 350);

                    } else {
                        ToastUtil.showShort(getApplication(), getResources().getString(R.string.operate_fail));
                    }
                    break;
                }
            }
        }
    };


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
        //向ViewPager添加各页面
        listFragment = new ArrayList<>();
        //首页改为探索
////        listFragment.add(new HomeFragment());
//        listFragment.add(new ExploreFragment());
//        listFragment.add(new ExamWrapFragment());
////        listFragment.add(new ReportFragment());//最近使用
//        listFragment.add(new MineFragment());

        listFragment.add(new ExploreFragment());
        listFragment.add(new CareerPlanWrapFragment());
        listFragment.add(new com.cheersmind.cheersgenie.features_v2.modules.base.fragment.ExamWrapFragment());
//        listFragment.add(new ExamWrapFragment());
        listFragment.add(new MineFragment());

        MyFragAdapter myAdapter = new MyFragAdapter(getSupportFragmentManager(), this, listFragment);
        viewPager.setAdapter(myAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
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
//        navigationBar
//                .addItem(new BottomNavigationItem(R.drawable.tab_home_checked,"探索")
//                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_home_normal)))
////                        .setBadgeItem(mShapeBadgeItem))
//                .addItem(new BottomNavigationItem(R.drawable.tab_exam_checked,"智评")
//                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_exam_normal)))
////                .addItem(new BottomNavigationItem(R.drawable.tab_report_checked,"报告")
////                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_report_normal)))
////                        .setBadgeItem(mTextBadgeItem))
//                .addItem(new BottomNavigationItem(R.drawable.tab_mine_checked,"我的")
//                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_mine_normal)))
//                .setFirstSelectedPosition(0)//设置默认选择的按钮
//                .initialise();//所有的设置需在调用该方法前完成
        navigationBar
                .addItem(new BottomNavigationItem(R.drawable.tab_home_checked,"发现")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_home_normal)))
//                        .setBadgeItem(mShapeBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_exam_checked,"生涯")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_exam_normal)))
                .addItem(new BottomNavigationItem(R.drawable.tab_report_checked,"测评")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_report_normal)))
//                        .setBadgeItem(mTextBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine_checked,"我的")
                        .setInactiveIcon(ContextCompat.getDrawable(MasterTabActivity.this,R.drawable.tab_mine_normal)))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成

        //初始隐藏悬浮按钮
        fabTask.setVisibility(View.GONE);

        //修改状态栏颜色
//        setStatusBarBackgroundColor(MasterTabActivity.this, getResources().getColor(R.color.white));

        //测试
        DisplayMetrics metrics = QSApplication.getMetrics();
        Configuration configuration = getResources().getConfiguration();
        System.out.println("MasterTabActivity：测试【DisplayMetrics】【Configuration】");

//        String childId = UCManager.getInstance().getDefaultChild().getChildId();
//        System.out.println(childId);
    }

    @Override
    protected void onInitData() {
        //注册事件
        EventBus.getDefault().register(this);

        //检查更新
        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
            VersionUpdateUtil.checkUpdate(this,false, false, httpTag);
        }

        // android 6.0以上动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.lacksPermissions(MasterTabActivity.this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
            } else {
                //初始化百度音频
                getSynthesizerManager().initialTts();
            }
        } else {
            //初始化百度音频
            getSynthesizerManager().initialTts();
        }

        //是否已经第一次访问过了任务列表
//        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
//        hasFirstShowTaskList = getHasFirstShowTaskList(defaultChildId);
        //请求任务列表
//        doGetTaskList(httpTag);

        //初始化声音
        SoundPlayUtils.getInstance().init(getApplicationContext());

        try {
            //绑定本机和班级号
            final CloudPushService pushService = PushServiceFactory.getCloudPushService();
            //班级Id
            String classId = UCManager.getInstance().getDefaultChild().getClassId();
            pushService.bindTag(CloudPushService.DEVICE_TARGET, new String[]{classId}, null, new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("绑定设备和班级号成功");
                }

                @Override
                public void onFailed(String s, String s1) {
                    System.out.println("绑定设备和班级号失败");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //初始请求院校库的查询条件信息
        doGetCollegeProvince();//大学省份
        doGetEducationLevel();//大学学历层次
//        doGetCollegeCategory();//大学院校类别
    }

    @Override
    protected void onStart() {
        super.onStart();

        //如果当前是“我的”，则刷新积分
        if (viewPager != null && viewPager.getCurrentItem() == 3) {
            //发送刷新积分通知
            EventBus.getDefault().post(new RefreshIntegralEvent());
        }

//        //第一次访问默认弹出任务列表弹窗
//        if (!hasFirstShowTaskList) {
//            popupTaskWindows();
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
//                    setStatusBarBackgroundColor(MasterTabActivity.this, getResources().getColor(R.color.white));
                    setStatusBarBackgroundColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
                case 1:
                    index = 1;
                    //修改状态栏颜色
                    setStatusBarBackgroundColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
                case 2:
                    index = 2;
                    //修改状态栏颜色
                    setStatusBarBackgroundColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
                    break;
                case 3:
                    index = 3;
                    //修改状态栏颜色
                    setStatusBarBackgroundColor(MasterTabActivity.this, getResources().getColor(R.color.colorPrimary));
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
            ToastUtil.showShort(getApplication(), "再按一次退出");
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canExit = false;
                }
            }, 1000);
            return;
        }

        super.onBackPressed();

        //防止InputMethodManager内存泄漏
//        super.onBackPressed();
//        startActivity(new Intent(this, CleanLeakActivity.class));
//        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空toast
        ToastUtil.cancelToast();

        //释放百度音频
        getSynthesizerManager().release();
        //清空设置监听
        getSynthesizerManager().setSpeechSynthesizerListener(null);

        //取消通信
        BaseService.cancelTag(httpTag);

        //释放声音资源
        SoundPlayUtils.getInstance().release();

        //释放监听器
        viewPager.removeOnPageChangeListener(pageChangeListener);
        pageChangeListener = null;

        //注销事件
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //请求安装未知来源的应用的结果
            case PackageUtils.INSTALL_PACKAGES_REQUESTCODE: {
                VersionUpdateUtil.handleBackFromRequestInstallPackages(this, grantResults);
                break;
            }

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


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
//                Manifest.permission.INTERNET,
//                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_SETTINGS,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @OnClick({R.id.fabTask})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //任务说明（分期测评）
            case R.id.fabTask:{
//                ToastUtil.showShort(getApplicationContext(), "显示任务");
//                doGetTaskList(null,null,null, httpTag);
                new TaskListDialog(MasterTabActivity.this, seminarEntity, exams, onOperationListener).show();
                break;
            }
        }
    }


    /**
     * 请求任务列表
     */
    private void doGetTaskList(String tag) {
        //孩子ID
        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();

        DataRequestService.getInstance().getTaskList(defaultChildId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Object obj) {
                try {
//                    Map dataMap = JsonUtil.fromJson(testTaskStr, Map.class);
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    SeminarRootEntity taskListRootEntity = InjectionWrapperUtil.injectMap(dataMap, SeminarRootEntity.class);

                    int totalCount = taskListRootEntity.getTotal();
                    List<SeminarEntity> dataList = taskListRootEntity.getItems();
                    seminarEntity = null;
                    exams = null;

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        fabTask.setVisibility(View.GONE);
                        return;
                    } else {
                        //取第一个专题
                        seminarEntity = dataList.get(0);
                        exams = seminarEntity.getExams();
                        if (ArrayListUtil.isEmpty(exams)) {
                            fabTask.setVisibility(View.GONE);
                            return;
                        }
                    }

                    //显示悬浮按钮
                    fabTask.setVisibility(View.VISIBLE);
                    //第一次访问默认弹出任务列表弹窗
                    if (!hasFirstShowTaskList) {
                        //显示弹窗
                        new TaskListDialog(MasterTabActivity.this, seminarEntity, exams, onOperationListener).show();
                        //设置已经第一次访问过了任务列表
                        hasFirstShowTaskList = true;
                        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
                        setHasFirstShowTaskList(defaultChildId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, tag, MasterTabActivity.this);
    }


    /**
     * 设置已经第一次显示了任务列表
     *
     * @param childId 孩子ID
     */
    private void setHasFirstShowTaskList(String childId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(childId, true);
        editor.apply();
    }


    /**
     * 获取是否已经第一次显示了任务列表
     *
     * @param childId 孩子ID
     */
    private boolean getHasFirstShowTaskList(String childId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(childId, false);
    }


    /**
     * 切换到测评tab
     */
    public void switchToExamTab() {
        viewPager.setCurrentItem(1, true);
    }


    /**
     * 刷新任务（测评）列表的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshTaskListNotice(RefreshTaskListEvent event) {
        //先清空测评数据
        seminarEntity = null;
        exams = null;
//        //请求任务列表
//        doGetTaskList(httpTag);
    }


    String testTaskStr = "{\n" +
            "\t\"total\": 1,\n" +
            "\t\"items\": [{\n" +
            "\t\t\"seminar_id\": \"b985b694-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"201810-福州一中高中测评测试专题\",\n" +
            "\t\t\"description\": \"专题描述专题描述\",\n" +
            "\t\t\"start_time\": \"2018-09-25T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2019-06-02T11:24:07.000+0800\",\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 0,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 1,\n" +
            "\t\t\t\"child_exam_status\": 1\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\"status\": 2,\n" +
            "\t\t\t\"child_exam_status\": 0\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";



    /**
     * 请求大学的省份
     */
    private void doGetCollegeProvince() {
        DataRequestService.getInstance().getCollegeProvince(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CollegeProvinceRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeProvinceRootEntity.class);

                    List<CollegeProvince> list = rootEntity.getItems();
//                    System.out.println(list);
                    //存到数据库
                    DataSupport.deleteAll(CollegeProvince.class);
                    DataSupport.saveAll(list);
//                    List<CollegeProvince> all = DataSupport.findAll(CollegeProvince.class);
//                    System.out.println(all);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, httpTag, MasterTabActivity.this);
    }

    /**
     * 请求大学的学历层次
     */
    private void doGetEducationLevel() {
        DataRequestService.getInstance().getCollegeEducationLevel(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    DataSupport.deleteAll(CollegeRankItem.class);
                    DataSupport.deleteAll(CollegeEduLevel.class);
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CollegeEduLevelRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeEduLevelRootEntity.class);

                    List<CollegeEduLevel> list = rootEntity.getItems();
                    //存到数据库
                    //排名项
                    List<CollegeRankItem>  rankItems = new ArrayList<>();
                    for (CollegeEduLevel eduLevel : list) {
                        rankItems.addAll(eduLevel.getRanking_items());
                    }
                    DataSupport.saveAll(rankItems);
//                    List<CollegeRankItem> all2 = DataSupport.findAll(CollegeRankItem.class);
//                    System.out.println(all2);
                    //学历层次
                    DataSupport.saveAll(list);
//                    List<CollegeEduLevel> all = DataSupport.findAll(CollegeEduLevel.class, true);
//                    System.out.println(all);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, httpTag, MasterTabActivity.this);
    }

//    /**
//     * 请求大学的院校类型
//     */
//    private void doGetCollegeCategory() {
//        DataRequestService.getInstance().getCollegeCategory(new BaseService.ServiceCallback() {
//            @Override
//            public void onFailure(QSCustomException e) {
//                e.getLocalizedMessage();
//            }
//
//            @Override
//            public void onResponse(Object obj) {
//                try {
//                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
//                    CollegeCategoryRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CollegeCategoryRootEntity.class);
//
//                    List<CollegeCategory> list = rootEntity.getItems();
//                    //存到数据库
//                    DataSupport.deleteAll(CollegeCategory.class);
//                    DataSupport.saveAll(list);
//                    List<CollegeCategory> all = DataSupport.findAll(CollegeCategory.class);
//                    System.out.println(all);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, httpTag, MasterTabActivity.this);
//    }

}


