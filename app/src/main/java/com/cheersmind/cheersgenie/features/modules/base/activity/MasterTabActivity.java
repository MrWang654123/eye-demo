package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TimeLineAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.event.RefreshIntegralEvent;
import com.cheersmind.cheersgenie.features.manager.SynthesizerManager;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExamWrapFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.ExploreFragment;
import com.cheersmind.cheersgenie.features.modules.base.fragment.MineFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.ReplyQuestionActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.PermissionUtil;
import com.cheersmind.cheersgenie.features.view.ViewPagerSlide;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.TaskListDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.entity.TaskItemEntity;
import com.cheersmind.cheersgenie.main.entity.TaskListEntity;
import com.cheersmind.cheersgenie.main.entity.TaskListRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.PackageUtils;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.util.VersionUpdateUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    //通信标记
    private String httpTag;

    //是否已经第一次访问过了任务列表
    private boolean hasFirstShowTaskList;
    //任务弹窗偏移
    int offset_x;
    int offset_y;
    //任务列表
    private TaskListEntity taskListEntity;
    //任务列表项
    private List<TaskItemEntity> taskItems;
    //任务列表适配器
    private TimeLineAdapter mTimeLineAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检查更新
        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
            VersionUpdateUtil.checkUpdate(this,false, false);
        }

        // android 6.0以上动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.lacksPermissions(MasterTabActivity.this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
            } else {
                //初始化百度音频
                ((QSApplication)getApplication()).getSynthesizerManager().initialTts();
                getSynthesizerManager().initialTts();
            }
        } else {
            //初始化百度音频
            getSynthesizerManager().initialTts();
        }

        httpTag = String.valueOf(System.currentTimeMillis());
        //是否已经第一次访问过了任务列表
        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        hasFirstShowTaskList = getHasFirstShowTaskList(defaultChildId);
        //请求任务列表
//        doGetTaskList(null, null, null, httpTag);
        if (!hasFirstShowTaskList) {
            new TaskListDialog(MasterTabActivity.this, null, null, null).show();
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
//        ButterKnife.bind(this);
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
//        SoundPlayUtils.init(this);

        //检查更新
//        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
//            VersionUpdateUtil.checkUpdate(this,false, false);
//        }

//        initPermission(); // android 6.0以上动态权限申请

        //任务弹窗偏移
        offset_x = (int) getResources().getDimension(R.dimen.task_list_popup_window_offset_x);
        offset_y = (int) getResources().getDimension(R.dimen.task_list_popup_window_offset_y);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //如果当前是“我的”，则刷新积分
        if (viewPager.getCurrentItem() == 2) {
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

        //释放百度音频
        getSynthesizerManager().release();
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

        ArrayList<String> toApplyList = new ArrayList<String>();

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
//                popupTaskWindows();
//                doGetTaskList(null,null,null, httpTag);
                new TaskListDialog(MasterTabActivity.this, null, null, null).show();
                break;
            }
        }
    }


    /**
     * 弹出任务弹窗
     */
    private void popupTaskWindows() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(MasterTabActivity.this).inflate(
                R.layout.popup_window_task, null);
        //列表
        final RecyclerView mRecyclerView = contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        //标题
        final TextView tvTitle = contentView.findViewById(R.id.tv_title);
        //关闭按钮
        ImageView btnCloseRight = contentView.findViewById(R.id.iv_close_right);
        //空布局
        final XEmptyLayout emptyLayout = contentView.findViewById(R.id.emptyLayout);

        if (ArrayListUtil.isNotEmpty(taskItems)) {
            //列表数据
            mTimeLineAdapter = new TimeLineAdapter(MasterTabActivity.this, taskItems, true);
            mRecyclerView.setAdapter(mTimeLineAdapter);
            mRecyclerView.scrollToPosition(getFirstDoingTaskPosition());

            //标题
            if (taskListEntity != null) {
                tvTitle.setText(taskListEntity.getSeminar_name());
            } else {
                tvTitle.setText("");
            }

            //隐藏空布局
            emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

        } else {
            doGetTaskList(mRecyclerView, tvTitle, emptyLayout, httpTag);
        }

//        doGetTaskList(mRecyclerView, tvTitle, emptyLayout, httpTag);

        //空布局初始化
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_task_list));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                doGetTaskList(mRecyclerView, tvTitle, emptyLayout, httpTag);
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setAnimationStyle(R.style.popup_window_anim_style);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap)null));//必须设置
        //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg_goodslist));
        popupWindow.setFocusable(true);//内容的事件才会响应
        popupWindow.setOutsideTouchable(true);
        //设置背景半透明
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.6f; //0.0-1.0
//        getWindow().setAttributes(lp);
//        backgroundAlpha(0.6f);
        //清空所有消息
        mHandler.removeCallbacksAndMessages(null);
        Message.obtain(mHandler, MSG_WINDOWS_ALPHA_SUB).sendToTarget();

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                //取消通信
                BaseService.cancelTag(httpTag);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //清空所有消息
//                        mHandler.removeCallbacksAndMessages(null);
////                        //设置背景半透明
////                        WindowManager.LayoutParams lp = getWindow().getAttributes();
////                        lp.alpha = 1.0f; //0.0-1.0
////                        getWindow().setAttributes(lp);
//
//                        WindowManager.LayoutParams lp = getWindow().getAttributes();
//                        lp.alpha = 1.0f;
//                        getWindow().setAttributes(lp);
////                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                    }
//                }, 270);

//                mHandler.removeCallbacksAndMessages(null);
//                Message.obtain(mHandler, MSG_WINDOWS_ALPHA_ADD).sendToTarget();

                //清空所有消息
                mHandler.removeCallbacksAndMessages(null);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

//        popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                Log.i("mengdd", "onTouch : ");
//
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            }
//        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(null));

        // 设置好参数之后再show
//        popupWindow.showAsDropDown(fabTask);
//        popupWindow.showAsDropDown(fabTask, 10, 10);
//        popupWindow.showAtLocation(flContainer, Gravity.BOTTOM,
//                DensityUtil.dip2px(MasterTabActivity.this, 10),
//                DensityUtil.dip2px(MasterTabActivity.this, 120));
//        popupWindow.showAtLocation(flContainer, Gravity.BOTTOM|Gravity.RIGHT,
//                DensityUtil.dip2px(MasterTabActivity.this, 9),
//                DensityUtil.dip2px(MasterTabActivity.this, 125));
        popupWindow.showAtLocation(flContainer, Gravity.BOTTOM|Gravity.RIGHT,
                offset_x,
                offset_y);

        //关闭操作
        btnCloseRight.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                popupWindow.dismiss();
            }
        });

    }


    /**
     * 获取第一个正在进行的任务位置
     * @return 索引
     */
    private int getFirstDoingTaskPosition() {
        int position = 0;

        if (ArrayListUtil.isNotEmpty(taskItems)) {
            for (int i=0; i<taskItems.size(); i++) {
                TaskItemEntity taskItem = taskItems.get(i);
                if (taskItem.getStatus() == Dictionary.TASK_STATUS_DOING) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    /**
     * 请求任务列表
     */
    private void doGetTaskList(final RecyclerView recyclerView, final TextView tvTitle, final XEmptyLayout emptyLayout, String tag) {
        if (emptyLayout != null) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }
        //孩子ID
        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();

        DataRequestService.getInstance().getTaskList(defaultChildId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //设置空布局：网络错误
                if (emptyLayout != null) {
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //设置空布局：隐藏
                    if (emptyLayout != null) {
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
//                            }
//                        },2000);
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    }

//                    Map dataMap = JsonUtil.fromJson(testTaskStr, Map.class);
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TaskListRootEntity taskListRootEntity = InjectionWrapperUtil.injectMap(dataMap, TaskListRootEntity.class);

                    int totalCount = taskListRootEntity.getTotal();
                    List<TaskListEntity> dataList = taskListRootEntity.getItems();
                    taskListEntity = null;
                    taskItems = null;

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        if (emptyLayout != null) {
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        }
                        return;
                    } else {
                        taskListEntity = dataList.get(0);
                        taskItems = taskListEntity.getTaskItems();
                        if (ArrayListUtil.isEmpty(taskItems)) {
                            if (emptyLayout != null) {
                                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                                return;
                            }
                        }
                    }

//                    popupTaskWindows();
                    //任务列表
                    if (recyclerView != null) {
                        mTimeLineAdapter = new TimeLineAdapter(MasterTabActivity.this, taskItems, true);
                        recyclerView.setAdapter(mTimeLineAdapter);
                        recyclerView.scrollToPosition(getFirstDoingTaskPosition());
                    }

                    //标题
                    if (tvTitle != null) {
                        if (taskListEntity != null) {
                            tvTitle.setText(taskListEntity.getSeminar_name());
                        } else {
                            tvTitle.setText("");
                        }
                    }

                    //第一次访问默认弹出任务列表弹窗
                    if (!hasFirstShowTaskList) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                popupTaskWindows();
                                //设置已经第一次访问过了任务列表
                                hasFirstShowTaskList = true;
                                String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
                                setHasFirstShowTaskList(defaultChildId);
                            }
                        }, 500);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    if (emptyLayout != null) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    }
                }

            }
        }, tag);
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

    //windows背景透明度消息
    private final static int MSG_WINDOWS_ALPHA_ADD = 1;
    private final static int MSG_WINDOWS_ALPHA_SUB = 2;
    //延迟间隔
    private final static int DELAY_INTERVAL = 2;
    //透明度渐变间隔
    float intervalAlpha = 0.005f;

    @Override
    public void onHandleMessage(Message msg) {
        //屏幕透明度
        float alpha = getBackgroundAlpha();
        switch (msg.what) {
            case MSG_WINDOWS_ALPHA_ADD: {
                if (alpha < 0.995f) {
                    alpha += intervalAlpha;
                    backgroundAlpha(alpha);
                    mHandler.sendEmptyMessageDelayed(MSG_WINDOWS_ALPHA_ADD, DELAY_INTERVAL);

                } else {
                    backgroundAlpha(1.0f);
//                    alpha += intervalAlpha;
//                    backgroundAlpha(alpha);
                }

                break;
            }
            case MSG_WINDOWS_ALPHA_SUB: {
                if (alpha > 0.605f) {
                    alpha -= intervalAlpha;
                    backgroundAlpha(alpha);
                    mHandler.sendEmptyMessageDelayed(MSG_WINDOWS_ALPHA_SUB, DELAY_INTERVAL);

                } else {
                    backgroundAlpha(0.6f);
                }
                break;
            }
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha [0.0-1.0]
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 获取屏幕的背景透明度
     */
    public float getBackgroundAlpha()
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        return lp.alpha;
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
            "\t\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\t\"status\": 2\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\t\"status\": 1\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\t\"status\": 0\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\t\"status\": 0\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"exam_id\": \"10001\",\n" +
            "\t\t\t\t\"exam_name\": \"201810-福州一中高中\",\n" +
            "\t\t\t\t\"start_time\": \"2018-09-27T00:00:00.000+0800\",\n" +
            "\t\t\t\t\"end_time\": \"2018-12-28T23:11:29.000+0800\",\n" +
            "\t\t\t\t\"status\": 0\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t}]\n" +
            "}";

}


