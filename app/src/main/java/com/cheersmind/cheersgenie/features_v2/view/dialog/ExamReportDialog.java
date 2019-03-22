package com.cheersmind.cheersgenie.features_v2.view.dialog;

import android.app.Dialog;
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
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamReportFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;


/**
 * 测评报告对话框（话题、量表通用）
 */
public class ExamReportDialog extends DialogFragment implements View.OnClickListener {

    //测评报告dto
    private ExamReportDto reportDto;

    //关闭按钮
    private ImageView ivClose;
    //模拟边界视图
    private View viewSimulateOutSite;

    //操作监听
    private OnOperationListener listener;

    /**
     * 设置报告dto
     * @param reportDto 报告dto
     * @throws QSCustomException 异常
     */
    public ExamReportDialog setReportDto(ExamReportDto reportDto) throws QSCustomException {
        this.reportDto = reportDto;

        //量表非空
        if (this.reportDto == null) {
            throw new QSCustomException("报告请求参数不能为空");
        } else {
            //孩子测评ID非空
            if (TextUtils.isEmpty(this.reportDto.getChildExamId())) {
                throw new QSCustomException("孩子测评Id不能为空");
            }
            //维度ID非空
            if (TextUtils.isEmpty(this.reportDto.getRelationId())) {
                throw new QSCustomException("维度Id不能为空");
            }
            //维度类型非空
            if (TextUtils.isEmpty(this.reportDto.getRelationType())) {
                throw new QSCustomException("维度类型不能为空");
            }
        }

        return this;
    }

    /**
     * 设置操作监听
     * @param listener 监听器
     * @return ExamReportDialog
     */
    public ExamReportDialog setListener(OnOperationListener listener) {
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
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        }
        //初始化布局
        View view = inflater.inflate(R.layout.dialog_topic_report, container, false);

        //话题数据包
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtoKey.EXAM_REPORT_DTO, reportDto);
        //测评报告
        ExamReportFragment fragment = new ExamReportFragment();
        fragment.setArguments(bundle);
        //添加最新测评fragment到容器中
        getChildFragmentManager().beginTransaction().add(R.id.fl_fragment, fragment, fragment.getClass().getName()).commit();

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
        if (RepetitionClickUtil.isFastClick()) {
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
     * @param dialog 对话框
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (listener != null) {
            listener.onExit();
        }
    }


    /**
     * 清空监听
     */
    public void clearListener() {
        listener = null;
        try {
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.setOnDismissListener(null);
                dialog.setOnCancelListener(null);
                dialog.setOnShowListener(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
