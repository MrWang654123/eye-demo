package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.entity.CommentEntity;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 测评任务详情
 */
public class ExamTaskDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //测评任务
    private ExamTaskEntity examTask;

    @BindView(R.id.iv_main)
    SimpleDraweeView ivMain;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    //评论模块
    @BindView(R.id.ll_comment_block)
    LinearLayout llCommentBlock;

    ExamTaskCommentFragment commentFragment;

    @Override
    protected int setContentView() {
        return R.layout.fragment_exam_task_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            examTask = (ExamTaskEntity) bundle.getSerializable(DtoKey.EXAM_TASK);
        }

        if (examTask != null) {
            //主图
            ivMain.setImageURI(examTask.getTask_icon());

            if (examTask.getChildTask() != null) {
                List<Pair<String, Fragment>> items = new ArrayList<>();

                ExamTaskItemFragment fragment1 = new ExamTaskItemFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString(DtoKey.CHILD_TASK_ID, examTask.getChildTask().getChild_task_id());
                bundle1.putInt(DtoKey.TASK_STATUS, examTask.getChildTask().getStatus());
                fragment1.setArguments(bundle1);

                commentFragment = new ExamTaskCommentFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString(DtoKey.TASK_ID, examTask.getTask_id());
                commentFragment.setArguments(bundle2);

                items.add(new Pair<String, Fragment>("内容", fragment1));
                items.add(new Pair<String, Fragment>("留言", commentFragment));
                viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
                //标签绑定viewpager
                tabs.setupWithViewPager(viewPager);

                //换页监听
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 1) {
                            llCommentBlock.setVisibility(View.VISIBLE);

                        } else {
                            llCommentBlock.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    //发送停止Fling的事件
                    EventBus.getDefault().post(new StopFlingEvent());
                }

            }
        });

        //初始隐藏评论模块
        llCommentBlock.setVisibility(View.GONE);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({ R.id.tv_comment_tip })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //触发评论对话框的提示文本
            case R.id.tv_comment_tip: {
                //弹出评论编辑弹窗
                popupCommentEditWindows();
                break;
            }
        }
    }

    //最后一次未发送的评论
    private String lastNotSendComment;

    /**
     * 弹出评论对话框
     */
    private void popupCommentEditWindows() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_comment_edit, null);
        final Dialog dialog = new Dialog(getContext(), R.style.dialog_bottom_full);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(view);

        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度占满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialog_Animation);//动画
        dialog.show();

        //评论编辑框
        final EditText etComment = view.findViewById(R.id.et_comment);

        //发送按钮
        final Button btnSend = view.findViewById(R.id.btn_send);
        btnSend.setEnabled(false);//初始设置为未激活状态
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //最后一次未发送的评论
                lastNotSendComment = etComment.getText().toString();
                //提交评论
                doComment(examTask.getTask_id(), etComment.getText().toString());
            }
        });
        //关闭按钮
        final Button btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //最后一次未发送的评论
                lastNotSendComment = etComment.getText().toString();
            }
        });

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //没输入，则发送按钮禁用
                if (s.length() == 0) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }
        });

        //初始化最后一次未发送的评论
        if (!TextUtils.isEmpty(lastNotSendComment)) {
            etComment.setText(lastNotSendComment);
            etComment.setSelection(lastNotSendComment.length());
        }

    }


    /**
     * 提交评论
     *
     * @param id 评论对象ID
     * @param content 评论内容
     */
    private void doComment(String id, String content) {
        DataRequestService.getInstance().postDoComment(id, content, Dictionary.COMMENT_TYPE_TASK, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CommentEntity commentEntity = InjectionWrapperUtil.injectMap(dataMap, CommentEntity.class);

                    showToast("评论成功");
                    //清空最后一次未发送的评论
                    lastNotSendComment = "";
                    //刷新我的评论视图
//                    refreshMineComment(commentEntity);
                    //滚动到我的评论
//                    scrollToMineComment();

                    //刷新评论列表
                    if (commentFragment != null) {
                        commentFragment.refreshData();
                    }

                } catch (Exception e) {
                    //操作失败
                    e.printStackTrace();
                    showToast("评论失败");
                }
            }
        }, httpTag,getContext());
    }

}

