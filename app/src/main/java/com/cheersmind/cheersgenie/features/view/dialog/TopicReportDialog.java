package com.cheersmind.cheersgenie.features.view.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.LastReportFragment;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;


/**
 * 话题报告的对话框
 */
public class TopicReportDialog extends DialogFragment implements View.OnClickListener {

    private static final String TOPIC_INFO = "topic_info";
    //话题
    private TopicInfoEntity topicInfo;

    //关闭按钮
    private ImageView ivClose;
    //模拟边界视图
    private View viewSimulateOutSite;

    //操作监听
    private OnOperationListener listener;

    /**
     * 设置话题对象
     * @param topicInfo
     * @throws Exception
     */
    public TopicReportDialog setTopicInfo(TopicInfoEntity topicInfo) throws Exception {
        this.topicInfo = topicInfo;

        if (this.topicInfo == null) {
            throw new Exception("数据有误");
        } else {
            //话题ID
            if (TextUtils.isEmpty(this.topicInfo.getTopicId())) {
                throw new Exception("数据有误");
            }
            //孩子话题
            TopicInfoChildEntity childTopic = this.topicInfo.getChildTopic();
            if (childTopic == null) {
                throw new Exception("数据有误");
            }
            //孩子测评ID
            if (TextUtils.isEmpty(this.topicInfo.getChildTopic().getChildExamId())) {
                throw new Exception("数据有误");
            }
        }

        return this;
    }

    /**
     * 设置操作监听
     * @param listener
     * @return
     */
    public TopicReportDialog setListener(OnOperationListener listener) {
        this.listener = listener;
        return this;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        setStyle(STYLE_NO_FRAME, R.style.loading_dialog);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置dialog的 进出 动画
        getDialog().getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        //初始化布局
        View view = inflater.inflate(R.layout.dialog_topic_report, container, false);

        //话题数据包
        Bundle bundle = new Bundle();
        bundle.putSerializable(TOPIC_INFO, topicInfo);
        //最新测评
        LastReportFragment lastReportFragment = new LastReportFragment();
        lastReportFragment.setArguments(bundle);
        //添加最新测评fragment到容器中
        getChildFragmentManager().beginTransaction().add(R.id.fl_fragment, lastReportFragment, lastReportFragment.getClass().getName()).commit();

        //初始化视图
        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //位于底部
        layoutParams.gravity = Gravity.BOTTOM;
        //宽度
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //高度
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //DecorView的内间距（目前测试的机型还没发现有影响）
//        window.getDecorView().setPadding(0, 0, 0, 0);
        //背景灰度
        layoutParams.dimAmount = 0.4f;
        //设置属性
        window.setAttributes(layoutParams);
    }

    /**
     * 初始化视图
     */
    private void initView(View rootView) {
        ivClose = rootView.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        viewSimulateOutSite = rootView.findViewById(R.id.viewSimulateOutSite);
        viewSimulateOutSite.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (!RepetitionClickUtil.isFastClick()) {
            return;
        }
        if (v == ivClose || v == viewSimulateOutSite) {
//            if (listener != null) {
//                listener.onExit();
//            }
            dismiss();
        }
    }


    //操作监听
    public interface OnOperationListener {

        //退出操作
        void onExit();
    }

    /**
     * 对话框cancel监听
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

//        if (this.listener != null) {
//            //设置对话框cancel监听
//            getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    listener.onExit();
//                }
//            });
//        }
        if (listener != null) {
            listener.onExit();
        }
    }

}
