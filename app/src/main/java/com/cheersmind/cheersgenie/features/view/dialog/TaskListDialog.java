package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TaskListAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.SeminarEntity;
import com.cheersmind.cheersgenie.main.entity.SeminarRootEntity;
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

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //专题
    private SeminarEntity seminarEntity;
    //测评集合
    private List<ExamEntity> examList;

    //操作监听
    private OnOperationListener listener;

    //通信tag
    private String tag = System.currentTimeMillis() + "";

    //recycler子项的点击监听
    protected BaseQuickAdapter.OnItemClickListener recyclerItemClickListener =  new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            Object item = ((TaskListAdapter) adapter).getItem(position);
            if (item != null && listener != null) {
                listener.onOnClickItem(position, ((ExamEntity) item).getExamId());
                //点击完直接关闭对话框
                dismiss();
            }
        }
    };


    public TaskListDialog(@NonNull Context context, SeminarEntity seminarEntity, List<ExamEntity> examList, OnOperationListener listener) {
        super(context, R.style.loading_dialog);
        this.seminarEntity = seminarEntity;
        this.examList = examList;


        this.listener = listener;
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

        if (seminarEntity == null && examList == null) {
            doGetTaskList(tag);
        } else {
            //刷新视图
            refreshView(seminarEntity, examList);

            //第一次访问默认弹出任务列表弹窗
            String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
            setHasFirstShowTaskList(defaultChildId);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.setHasFixedSize(true);
        emptyLayout.setNoDataTip(getContext().getString(R.string.empty_tip_task_list));
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
                    SeminarRootEntity taskListRootEntity = InjectionWrapperUtil.injectMap(dataMap, SeminarRootEntity.class);

                    int totalCount = taskListRootEntity.getTotal();
                    List<SeminarEntity> dataList = taskListRootEntity.getItems();
                    seminarEntity = null;
                    examList = null;

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        if (emptyLayout != null) {
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        }
                        return;
                    } else {
                        seminarEntity = dataList.get(0);
                        examList = seminarEntity.getExams();
                        if (ArrayListUtil.isEmpty(examList)) {
                            if (emptyLayout != null) {
                                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                                return;
                            }
                        }
                    }

                    //刷新视图
                    refreshView(seminarEntity, examList);

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
        }, tag, getContext());
    }


    /**
     * 刷新视图
     * @param seminar 专题
     * @param examList 测评集合
     */
    private void refreshView(SeminarEntity seminar, List<ExamEntity> examList) {
        //任务列表
        TaskListAdapter taskListAdapter = new TaskListAdapter(R.layout.item_timeline_line_padding_custom, examList);
        //设置子项点击监听
        taskListAdapter.setOnItemClickListener(recyclerItemClickListener);
        recyclerView.setAdapter(taskListAdapter);
        recyclerView.scrollToPosition(getFirstDoingTaskPosition(examList));

        //标题
        if (seminar != null) {
            tvTitle.setText(seminar.getSeminar_name());
        } else {
            tvTitle.setText("");
        }
    }


    /**
     * 设置已经第一次显示了任务列表
     *
     * @param childId 孩子ID
     */
    private void setHasFirstShowTaskList(String childId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sp.getBoolean(childId, false);
    }


    /**
     * 获取第一个正在进行的测评位置
     * @return 索引
     */
    private int getFirstDoingTaskPosition(List<ExamEntity> examList) {
        int position = 0;

        if (ArrayListUtil.isNotEmpty(examList)) {
            for (int i=0; i<examList.size(); i++) {
                ExamEntity exam = examList.get(i);
                if (exam.getStatus() == Dictionary.TASK_STATUS_DOING) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }


    @OnClick({R.id.iv_close_right})
    public void onViewClick(View v) {
        if (!RepetitionClickUtil.isFastClick()) {
            return;
        }

        switch (v.getId()) {
            //右侧关闭按钮
            case R.id.iv_close_right: {
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

        //点击列表项
        void onOnClickItem(int position, String id);
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


//    String testTaskStr = "{\n" +
//            "\t\"total\": 1,\n" +
//            "\t\"items\": [{\n" +
//            "\t\t\"seminar_id\": \"b985b695-f773-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\"seminar_name\": \"中学生成长之路\",\n" +
//            "\t\t\"description\": \"中学生成长之路\",\n" +
//            "\t\t\"start_time\": \"2018-12-01T00:00:00.000+0800\",\n" +
//            "\t\t\"end_time\": \"2019-04-19T23:59:59.000+0800\",\n" +
//            "\t\t\"items\": [{\n" +
//            "\t\t\t\"exam_id\": \"0431a5e9-df48-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"情绪健康与促进（初）\",\n" +
//            "\t\t\t\"start_time\": \"2018-12-06T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2018-12-23T23:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 1\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"d47fac6c-f773-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"家庭与学校支持（初）\",\n" +
//            "\t\t\t\"start_time\": \"2018-12-24T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2019-01-02T23:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 0\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"c9a75573-f93c-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"生涯规划与发展（初）\",\n" +
//            "\t\t\t\"start_time\": \"2019-02-20T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2019-03-19T23:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 1\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"21f7ff10-f93d-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"学习问题诊断与学习指导（初）\",\n" +
//            "\t\t\t\"start_time\": \"2019-03-20T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2019-04-19T19:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 0\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"0431a5e9-df48-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"情绪健康与促进（初）\",\n" +
//            "\t\t\t\"start_time\": \"2018-12-06T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2018-12-23T23:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 1\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"d47fac6c-f773-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"家庭与学校支持（初）\",\n" +
//            "\t\t\t\"start_time\": \"2018-12-24T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2019-01-02T23:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 0\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"c9a75573-f93c-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"生涯规划与发展（初）\",\n" +
//            "\t\t\t\"start_time\": \"2019-02-20T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2019-03-19T23:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 0\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"exam_id\": \"21f7ff10-f93d-11e8-a1b4-161768d3d95e\",\n" +
//            "\t\t\t\"exam_name\": \"学习问题诊断与学习指导（初）\",\n" +
//            "\t\t\t\"start_time\": \"2019-03-20T00:00:00.000+0800\",\n" +
//            "\t\t\t\"end_time\": \"2019-04-19T19:59:59.000+0800\",\n" +
//            "\t\t\t\"status\": 1\n" +
//            "\t\t}]\n" +
//            "\t}]\n" +
//            "}";

}
