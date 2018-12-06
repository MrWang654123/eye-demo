package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TimeLineAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.TaskItemEntity;
import com.cheersmind.cheersgenie.main.entity.TaskListEntity;
import com.cheersmind.cheersgenie.main.entity.TaskListRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 任务列表对话框
 */
public class TaskListDialog extends Dialog {

    //上下文
    private Context context;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //任务列表
    private TaskListEntity taskListEntity;
    //任务列表项
    private List<TaskItemEntity> taskItems;
    //任务列表适配器
    private TimeLineAdapter mTimeLineAdapter;

    //操作监听
    private OnOperationListener listener;

    //通信tag
    private String tag = System.currentTimeMillis() + "";


    public TaskListDialog(@NonNull Context context) {
        super(context, R.style.loading_dialog);
        this.context = context;
    }

    public TaskListDialog(@NonNull Context context, TaskListEntity taskListEntity, List<TaskItemEntity> taskItems, OnOperationListener listener) {
        super(context, R.style.loading_dialog);
        this.context = context;
        this.taskListEntity = taskListEntity;
        this.taskItems = taskItems;


        this.listener = listener;
        if (this.listener != null) {
            //设置对话框cancel监听
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    TaskListDialog.this.listener.onExit();
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局文件
        setContentView(R.layout.popup_window_task);
        //ButterKnife绑定
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        //初始化视图
        initView();

        if (taskListEntity == null && taskItems == null) {
            doGetTaskList(tag);
        } else {
            //直接刷新页面
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.setHasFixedSize(true);
        emptyLayout.setNoDataTip(context.getString(R.string.empty_tip_task_list));
        //点击重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                doGetTaskList(tag);
            }
        });
        //设置背景
        emptyLayout.setBackgroundResource(R.drawable.shape_corner_task_popup_window);
    }

    /**
     * 请求任务列表
     */
    private void doGetTaskList(String tag) {
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
                        mTimeLineAdapter = new TimeLineAdapter(context, taskItems, true);
                        recyclerView.setAdapter(mTimeLineAdapter);
                        recyclerView.scrollToPosition(getFirstDoingTaskPosition(taskItems));
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
                    String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
                    setHasFirstShowTaskList(defaultChildId);

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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(childId, false);
    }


    /**
     * 获取第一个正在进行的任务位置
     * @return 索引
     */
    private int getFirstDoingTaskPosition(List<TaskItemEntity> taskItems) {
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


    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @OnClick({R.id.iv_close_right})
    public void onViewClick(View v) {
        if (!RepetitionClickUtil.isFastClick()) {
            return;
        }

        switch (v.getId()) {
            //右侧关闭按钮
            case R.id.iv_close_right: {
//                if (listener != null) {
//                    listener.onExit();
//                }
                dismiss();
                break;
            }
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        //取消当前对话框的所有通信
        BaseService.cancelTag(tag);
    }

    //操作监听
    public interface OnOperationListener {

        //退出操作
        void onExit();
    }

    @Override
    public void show() {
        //动画
        if (getWindow() != null) {
            getWindow().setWindowAnimations(R.style.popup_window_anim_style);
        }

        super.show();
        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //位于底部
        layoutParams.gravity = Gravity.BOTTOM;
        //宽度
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //高度
//        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //DecorView的内间距（目前测试的机型还没发现有影响）
//        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        //背景灰度
        layoutParams.dimAmount = 0.4f;
        //设置属性
        getWindow().setAttributes(layoutParams);

    }


    String testTaskStr = "{\n" +
            "\t\"total\": 1,\n" +
            "\t\"items\": [{\n" +
            "\t\t\"seminar_id\": \"b985b695-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\"seminar_name\": \"中学生成长之路\",\n" +
            "\t\t\"description\": \"中学生成长之路\",\n" +
            "\t\t\"start_time\": \"2018-12-01T00:00:00.000+0800\",\n" +
            "\t\t\"end_time\": \"2019-04-19T23:59:59.000+0800\",\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"exam_id\": \"0431a5e9-df48-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"情绪健康与促进（初）\",\n" +
            "\t\t\t\"start_time\": \"2018-12-06T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-23T23:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 1\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"d47fac6c-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"家庭与学校支持（初）\",\n" +
            "\t\t\t\"start_time\": \"2018-12-24T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2019-01-02T23:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"c9a75573-f93c-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"生涯规划与发展（初）\",\n" +
            "\t\t\t\"start_time\": \"2019-02-20T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2019-03-19T23:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 1\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"21f7ff10-f93d-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"学习问题诊断与学习指导（初）\",\n" +
            "\t\t\t\"start_time\": \"2019-03-20T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2019-04-19T19:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"0431a5e9-df48-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"情绪健康与促进（初）\",\n" +
            "\t\t\t\"start_time\": \"2018-12-06T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2018-12-23T23:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 1\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"d47fac6c-f773-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"家庭与学校支持（初）\",\n" +
            "\t\t\t\"start_time\": \"2018-12-24T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2019-01-02T23:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"c9a75573-f93c-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"生涯规划与发展（初）\",\n" +
            "\t\t\t\"start_time\": \"2019-02-20T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2019-03-19T23:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 0\n" +
            "\t\t}, {\n" +
            "\t\t\t\"exam_id\": \"21f7ff10-f93d-11e8-a1b4-161768d3d95e\",\n" +
            "\t\t\t\"exam_name\": \"学习问题诊断与学习指导（初）\",\n" +
            "\t\t\t\"start_time\": \"2019-03-20T00:00:00.000+0800\",\n" +
            "\t\t\t\"end_time\": \"2019-04-19T19:59:59.000+0800\",\n" +
            "\t\t\t\"status\": 1\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";

}
