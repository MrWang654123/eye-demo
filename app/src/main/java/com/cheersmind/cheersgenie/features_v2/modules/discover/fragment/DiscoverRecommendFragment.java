package com.cheersmind.cheersgenie.features_v2.modules.discover.fragment;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

/**
 * 推荐
 */
public class DiscoverRecommendFragment extends DiscoverTabItemFragment {

    //最新测评模块
    View evaluationBlock;
    //最新评测的标题
    TextView tvLastDimensionTitle;

    //最后一次操作的量表
    DimensionInfoEntity lastDimension;

    //底部滑出显示动画
    private TranslateAnimation mShowAction;


    @Override
    protected int setContentView() {
        return R.layout.fragment_discover_tab_item;
    }

    @Override
    protected void onInitView(View rootView) {
        super.onInitView(rootView);

        //设置显示时的动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        //最新测评模块
//        View view = LayoutInflater.from(recycleView.getContext()).inflate(R.layout.recycler_header_home_evaluation, recycleView, false);
        evaluationBlock = getLayoutInflater().inflate(R.layout.recycler_header_home_evaluation, null);
        //测评标题
        tvLastDimensionTitle = evaluationBlock.findViewById(R.id.tv_last_dimension_title);
        //进入最新测评按钮
        Button btnGotoLastDimension = evaluationBlock.findViewById(R.id.btn_goto_last_dimension);
        btnGotoLastDimension.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                        lastDimension, null,
                        Dictionary.EXAM_STATUS_DOING,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);
            }
        });

        //添加为recyclerView的header
        recyclerAdapter.addHeaderView(evaluationBlock);
        //初始隐藏最新评测模块
        evaluationBlock.setVisibility(View.GONE);
    }

    @Override
    protected void doRefresh() {
        super.doRefresh();
        //加载最新操作的评测
        loadLastOperateEvaluation();
    }

    @Override
    protected void doReLoad() {
        super.doReLoad();
        //加载最新操作的评测
        loadLastOperateEvaluation();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        //加载最新操作的评测
        loadLastOperateEvaluation();
    }

    /**
     * 最新操作测评消息
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLastExamNotice(LastHandleExamEvent event) {
        if (event == null) {
            return;
        }

        //更新最新测评
        if (event.getHandleType() == LastHandleExamEvent.HANDLE_TYPE_UPDATE) {
            //加载最新操作的评测
            loadLastOperateEvaluation();

        } else if (event.getHandleType() == LastHandleExamEvent.HANDLE_TYPE_COMPLETE) {//刚完成一个新的测评，此时无最新操作测评
            //隐藏视图，清理数据
            lastDimension = null;
            evaluationBlock.setVisibility(View.GONE);
        }

    }


    /**
     * 获取最新操作的评测
     */
    public void loadLastOperateEvaluation() {
        String defaultChildId = ChildInfoDao.getDefaultChildId();
        DataRequestService.getInstance().getLatestDimensionV2(defaultChildId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                lastDimension = null;
                evaluationBlock.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    lastDimension = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoEntity.class);
                    //有量表对象，且量表处于未完成状态
                    if (lastDimension != null
                            && !TextUtils.isEmpty(lastDimension.getDimensionId())
                            && lastDimension.getChildDimension() != null
                            && lastDimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                        //刷新最新操作测评的视图
                        tvLastDimensionTitle.setText(lastDimension.getDimensionName());

                    } else {
                        //没数据或者数据异常
                        throw new QSCustomException("无最新操作测评");
                    }

                    evaluationBlock.setVisibility(View.VISIBLE);
                    evaluationBlock.startAnimation(mShowAction);
                    //空布局：隐藏
//                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } catch (Exception e) {
                    e.printStackTrace();
                    lastDimension = null;
                    evaluationBlock.setVisibility(View.GONE);
                }

            }
        }, httpTag, getActivity());
    }

}
